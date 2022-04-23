package com.falsepattern.ssmlegacy.network.messages;

import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

@NoArgsConstructor
@AllArgsConstructor
public class MessageSetRange implements IMessage {
    int x;
    int y;
    int z;
    int rangeIndex;

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        rangeIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(rangeIndex);
    }

    @NoArgsConstructor
    public static class Handler implements IMessageHandler<MessageSetRange, IMessage> {
        @Override
        public IMessage onMessage(final MessageSetRange message, final MessageContext ctx) {
            val world = ctx.getServerHandler().playerEntity.getEntityWorld();
            val te = world.getTileEntity(message.x, message.y, message.z);

            if (te instanceof TileEntitySoundMuffler) {
                val tileEntity = (TileEntitySoundMuffler) te;
                tileEntity.setRange(message.rangeIndex);
            }

            return null;
        }
    }
}
