package com.github.radiationbitflip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = RadiationBitFlipMod.ID, name = "Radiation Bit Flip Mod", version = RadiationBitFlipMod.VERSION,
        guiFactory = "com.github.radiationbitflip.ModGuiFactory")
public class RadiationBitFlipMod {
    static final String ID = "radiation-bit-flip";
    static final String VERSION = "2.0.0";
    static final int MODE_OFF = 0;
    static final int MODE_FULL = 1;
    static final int MODE_SOLIDS_ONLY = 2;
    static final int RATE_LOW = 0;
    static final int RATE_MEDIUM = 1;
    static final int RATE_HIGH = 2;
    static final int RATE_REPLAY = 3;

    // representative rates from the original ram chip experiment
    private static final double LOW_RATE = 0.00000236689D;
    private static final double MEDIUM_RATE = 0.000224665D;
    private static final double HIGH_RATE = 0.0167743D;
    private static final int BLOCKS_PER_SECTION = 16 * 16 * 16;
    private static final int BITS_PER_BLOCK_ID = 8;

    @Mod.Instance(ID)
    public static RadiationBitFlipMod instance;

    @SidedProxy(clientSide = "com.github.radiationbitflip.ClientProxy",
                serverSide = "com.github.radiationbitflip.CommonProxy")
    static CommonProxy proxy;

    static SimpleNetworkWrapper network;
    private final Random random = new Random();
    private volatile int mode = MODE_SOLIDS_ONLY;
    private volatile int rateMode = RATE_MEDIUM;
    private long replayTicks;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(ID);
        network.registerMessage(ModeMessage.Handler.class, ModeMessage.class, 0, Side.SERVER);
        proxy.preInit();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        mode = MODE_SOLIDS_ONLY;
        rateMode = RATE_MEDIUM;
        replayTicks = 0;
    }

    void setSettings(int requestedMode, int requestedRate) {
        if (requestedMode >= MODE_OFF && requestedMode <= MODE_SOLIDS_ONLY) {
            mode = requestedMode;
        }
        if (requestedRate >= RATE_LOW && requestedRate <= RATE_REPLAY) {
            if (requestedRate == RATE_REPLAY && rateMode != RATE_REPLAY) replayTicks = 0;
            rateMode = requestedRate;
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && rateMode == RATE_REPLAY) replayTicks++;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || mode == 0
                || !(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        WorldServer world = player.getServerWorld();
        BlockPos center = player.getPosition();
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;
        int centerSectionY = center.getY() >> 4;
        List<Section> sections = new ArrayList<Section>(64);

        // the four sections on each axis are -1, 0, +1, +2 from the player's section.
        // only loaded chunks are considered; this does not load or generate terrain.
        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;
                Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
                if (chunk == null) continue;
                for (int dy = -1; dy <= 2; dy++) {
                    int sectionY = centerSectionY + dy;
                    if (sectionY >= 0 && sectionY < 16) {
                        sections.add(new Section(chunkX, sectionY, chunkZ));
                    }
                }
            }
        }
        if (sections.isEmpty()) return;

        // at 20 ticks/s, every represented block-id bit has the recorded
        // probability of an upset. round the fractional event count randomly.
        double expected = sections.size() * BLOCKS_PER_SECTION * BITS_PER_BLOCK_ID
                * getCurrentRate() / 20.0D;
        int attempts = (int) expected;
        if (random.nextDouble() < expected - attempts) attempts++;
        int selectedMode = mode;
        for (int i = 0; i < attempts; i++) {
            Section section = sections.get(random.nextInt(sections.size()));
            int index = random.nextInt(BLOCKS_PER_SECTION);
            BlockPos pos = new BlockPos((section.x << 4) + (index & 15),
                    (section.y << 4) + ((index >> 8) & 15),
                    (section.z << 4) + ((index >> 4) & 15));
            flip(world, pos, selectedMode);
        }
    }

    private double getCurrentRate() {
        if (rateMode == RATE_LOW) return LOW_RATE;
        if (rateMode == RATE_HIGH) return HIGH_RATE;
        if (rateMode != RATE_REPLAY) return MEDIUM_RATE;

        double seconds = replayTicks / 20.0D;
        if (seconds < 10.0D) return LOW_RATE;
        if (seconds < 20.0D) return mix(LOW_RATE, MEDIUM_RATE, (seconds - 10.0D) / 10.0D);
        if (seconds < 110.0D) return MEDIUM_RATE;
        if (seconds < 120.0D) return mix(MEDIUM_RATE, HIGH_RATE, (seconds - 110.0D) / 10.0D);
        return HIGH_RATE;
    }

    private static double mix(double start, double end, double amount) {
        return start + (end - start) * amount;
    }

    private void flip(WorldServer world, BlockPos pos, int selectedMode) {
        IBlockState original = world.getBlockState(pos);
        if (selectedMode == MODE_SOLIDS_ONLY && !original.getMaterial().isSolid()) return;

        int oldId = Block.getIdFromBlock(original.getBlock());
        int newId = oldId ^ (1 << random.nextInt(BITS_PER_BLOCK_ID));
        Block replacement = Block.getBlockById(newId);
        // unknown ids default to air, so we keep the original block
        if (replacement == null || Block.getIdFromBlock(replacement) != newId) return;

        try {
            int metadata = original.getBlock().getMetaFromState(original);
            IBlockState next = replacement.getStateFromMeta(metadata);
            if (replacement.getMetaFromState(next) != metadata) return;
            world.setBlockState(pos, next, 2);
        } catch (RuntimeException failure) {
            // if a block rejects the new state, retain the original state.
            try {
                if (!world.getBlockState(pos).equals(original)) world.setBlockState(pos, original, 2);
            } catch (RuntimeException ignored) {
                // there is no safe further operation if the world itself rejects rollback.
            }
        }
    }

    private static final class Section {
        final int x;
        final int y;
        final int z;

        Section(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
