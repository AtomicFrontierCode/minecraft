package com.github.radiationbitflip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private final KeyBinding openMenu = new KeyBinding("Radiation Bit Flip Mod: choose type and rate", Keyboard.KEY_K,
            "Radiation Bit Flip Mod");
    private boolean hadPlayer;

    @Override
    public void preInit() {
        ClientRegistry.registerKeyBinding(openMenu);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.currentScreen == null && openMenu.isPressed()) {
            mc.displayGuiScreen(new ModeScreen());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        boolean hasPlayer = Minecraft.getMinecraft().player != null;
        if (!hasPlayer && hadPlayer) ModeScreen.resetSelection();
        hadPlayer = hasPlayer;
        ModeScreen.sendSettings();
    }
}
