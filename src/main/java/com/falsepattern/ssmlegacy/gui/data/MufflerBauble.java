package com.falsepattern.ssmlegacy.gui.data;

import com.falsepattern.ssmlegacy.network.ThePacketeer;
import com.falsepattern.ssmlegacy.network.messages.MessageAddRemoveSound;
import com.falsepattern.ssmlegacy.network.messages.MessageToggleWhiteList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MufflerBauble implements IMufflerAccessor {
    private final EntityPlayer player;

    public MufflerBauble(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isWhiteList() {
        ItemStack bauble = player.getHeldItem();
        if(bauble.hasTagCompound()) {
            NBTTagCompound compound = bauble.getTagCompound();
            if(compound.hasKey("whiteList")) {
                return compound.getBoolean("whiteList");
            }
        }

        return false;
    }

    @Override
    public List<ResourceLocation> getMuffledSounds() {
        List<ResourceLocation> sounds = new ArrayList<>();

        ItemStack bauble = player.getHeldItem();
        if(bauble.hasTagCompound()) {
            NBTTagCompound compound = bauble.getTagCompound();
            if(compound.hasKey("sounds")) {
                NBTTagList list = compound.getTagList("sounds", 10);
                for(int i = 0; i < list.tagCount(); ++i) {
                    NBTTagCompound c = list.getCompoundTagAt(i);
                    String s = c.getString("sound");
                    sounds.add(new ResourceLocation(s));
                }
            }
        }

        sounds.sort(Comparator.comparing(ResourceLocation::toString));
        return sounds;
    }

    @Override
    public void toggleWhiteList() {
        ThePacketeer.INSTANCE.sendToServer(new MessageToggleWhiteList(0, 0, 0, MessageToggleWhiteList.Type.Bauble));
    }

    @Override
    public void muffleSound(ResourceLocation sound) {
        ThePacketeer.INSTANCE.sendToServer(new MessageAddRemoveSound(0, 0, 0, sound, MessageAddRemoveSound.Type.Bauble, MessageAddRemoveSound.Action.Add));
    }

    @Override
    public void unmuffleSound(ResourceLocation sound) {
        ThePacketeer.INSTANCE.sendToServer(new MessageAddRemoveSound(0, 0, 0, sound, MessageAddRemoveSound.Type.Bauble, MessageAddRemoveSound.Action.Remove));
    }
}
