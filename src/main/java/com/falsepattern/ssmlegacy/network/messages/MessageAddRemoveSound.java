package com.falsepattern.ssmlegacy.network.messages;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor
@AllArgsConstructor
public class MessageAddRemoveSound implements IMessage {

    private int x;
    private int y;
    private int z;
    private ResourceLocation sound;
    private Type type;
    private Action action;

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        sound = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        type = buf.readBoolean() ? Type.Bauble : Type.TileEntity;
        action = buf.readBoolean() ? Action.Add : Action.Remove;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf, sound.toString());
        buf.writeBoolean(type == Type.Bauble);
        buf.writeBoolean(action == Action.Add);
    }

    @NoArgsConstructor
    public static class Handler implements IMessageHandler<MessageAddRemoveSound, IMessage> {
        @Override
        public IMessage onMessage(final MessageAddRemoveSound message, final MessageContext ctx) {

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

        private void handleBauble(MessageAddRemoveSound message, MessageContext ctx) {
            val player = ctx.getServerHandler().playerEntity;
            if (player != null) {
                val stack = player.getHeldItem();
                if (stack != null && stack.getItem() == SuperSoundMuffler.itemSoundMufflerBauble) {
                    if (message.action == Action.Add) {
                        SuperSoundMuffler.itemSoundMufflerBauble.muffleSound(stack, message.sound);
                    } else {
                        SuperSoundMuffler.itemSoundMufflerBauble.unmuffleSound(stack, message.sound);
                    }
                }
            }
        }

        private void handleTileEntity(MessageAddRemoveSound message, MessageContext ctx) {
            val world = ctx.getServerHandler().playerEntity.getEntityWorld();
            val te = world.getTileEntity(message.x, message.y, message.z);
            if (te instanceof TileEntitySoundMuffler) {
                val tileEntity = (TileEntitySoundMuffler) te;
                if (message.action == Action.Add) {
                    tileEntity.muffleSound(message.sound);
                } else {
                    tileEntity.unmuffleSound(message.sound);
                }
            }
        }
    }

    public enum Action {
        Add,
        Remove
    }

    public enum Type {
        Bauble,
        TileEntity
    }
}