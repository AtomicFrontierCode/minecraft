package com.github.radiationbitflip;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ModeMessage implements IMessage {
    private int mode;
    private int rate;

    public ModeMessage() { }

    ModeMessage(int mode, int rate) {
        this.mode = mode;
        this.rate = rate;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mode = buf.readByte();
        rate = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(mode);
        buf.writeByte(rate);
    }

    public static class Handler implements IMessageHandler<ModeMessage, IMessage> {
        @Override
        public IMessage onMessage(ModeMessage message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(
                    () -> RadiationBitFlipMod.instance.setSettings(message.mode, message.rate));
            return null;
        }
    }
}
