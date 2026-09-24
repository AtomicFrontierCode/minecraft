package com.github.radiationbitflip;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ModeMessage implements IMessage {
    private int mode;

    public ModeMessage() { }

    ModeMessage(int mode) {
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(mode);
    }

    public static class Handler implements IMessageHandler<ModeMessage, IMessage> {
        @Override
        public IMessage onMessage(ModeMessage message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> RadiationBitFlipMod.instance.setMode(message.mode));
            return null;
        }
    }
}
