package com.falsepattern.ssmlegacy.network.messages;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;

import static com.falsepattern.ssmlegacy.network.messages.MessageToggleWhiteList.Type.Bauble;

@NoArgsConstructor
@AllArgsConstructor
public class MessageToggleWhiteList implements IMessage {

    int x;
    int y;
    int z;
    Type type;

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        type = buf.readBoolean() ? Type.Bauble : Type.TileEntity;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(type == Bauble);
    }

    @NoArgsConstructor
    public static class Handler implements IMessageHandler<MessageToggleWhiteList, IMessage> {
        @Override
        public IMessage onMessage(final MessageToggleWhiteList message, final MessageContext ctx) {
            switch (message.type) {
                case Bauble:
                    handleBauble(message, ctx);
                    break;
                case TileEntity:
                    handleTileEntity(message, ctx);
                    break;
            }

            return null;
        }

        private void handleBauble(MessageToggleWhiteList message, MessageContext ctx) {
            val player = ctx.getServerHandler().playerEntity;
            if (player != null) {
                val stack = player.getHeldItem();
                if (stack != null && stack.getItem() == SuperSoundMuffler.itemSoundMufflerBauble) {
                    SuperSoundMuffler.itemSoundMufflerBauble.toggleWhiteList(stack);
                }
            }
        }

        private void handleTileEntity(MessageToggleWhiteList message, MessageContext ctx) {
            val world = ctx.getServerHandler().playerEntity.getEntityWorld();
            val te = world.getTileEntity(message.x, message.y, message.z);

            if (te instanceof TileEntitySoundMuffler) {
                val tileEntity = (TileEntitySoundMuffler) te;
                tileEntity.toggleWhiteListMode();
            }
        }
    }

    public enum Type {
        Bauble,
        TileEntity
    }
}
