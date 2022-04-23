package com.falsepattern.ssmlegacy.block;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.client.audio.ISound;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileEntitySoundMuffler extends TileEntity {
    private final Set<ResourceLocation> muffledSounds = new HashSet<>();
    private boolean whiteListMode = true;
    private static final int[] ranges = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 32, 64, 128, 256 };
    private static final int defaultRangeIndex = 7;
    private int rangeIndex = defaultRangeIndex;

    @Override
    public void invalidate() {
        super.invalidate();
        SuperSoundMuffler.proxy.uncacheMuffler(this);
    }

    @Override
    public void validate() {
        super.validate();
        SuperSoundMuffler.proxy.cacheMuffler(this);
    }

    //region NBT Serialization
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeNBT(compound);
    }

    public void writeNBT(NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        for (ResourceLocation sound : muffledSounds) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("sound", sound.toString());
            tagList.appendTag(tag);
        }
        compound.setTag("sounds", tagList);
        compound.setBoolean("whiteList", whiteListMode);
        compound.setFloat("rangeIndex", rangeIndex);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readNBT(compound);
    }

    public void readNBT(NBTTagCompound compound) {
        muffledSounds.clear();
        whiteListMode = true;

        NBTTagList tagList = compound.getTagList("sounds", 10);
        for(int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound sound = tagList.getCompoundTagAt(i);
            muffledSounds.add(new ResourceLocation(sound.getString("sound")));
        }

        whiteListMode = compound.getBoolean("whiteList");
        rangeIndex = compound.getInteger("rangeIndex");
    }

    @Override
    public Packet getDescriptionPacket() {
        val compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
        return oldBlock != newBlock;
    }

    boolean isDefault() {
        return (whiteListMode && muffledSounds.isEmpty() && rangeIndex == 7);
    }
    //endregion

    public void muffleSound(ResourceLocation sound) {
        muffledSounds.add(sound);

        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void unmuffleSound(ResourceLocation sound) {
        muffledSounds.remove(sound);

        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public List<ResourceLocation> getMuffledSounds() {
        return new ArrayList<>(muffledSounds);
    }

    public void toggleWhiteListMode() {
        whiteListMode = !whiteListMode;

        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public boolean isWhiteList() { return whiteListMode; }

    public boolean shouldMuffleSound(ResourceLocation soundLocation) {
        if(isWhiteList()) {
            return !muffledSounds.contains(soundLocation);
        }

        return muffledSounds.contains(soundLocation);
    }

    public boolean shouldMuffleSound(ISound sound) {
        double dist = getDistanceFrom(sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
        int range = getRange();
        return dist <= ((range * range) + 1) && shouldMuffleSound(sound.getPositionedSoundLocation());
    }

    public void setRange(int value) {
        rangeIndex = value;

        markDirty();
    }

    public int getRange() {
        return ranges[rangeIndex];
    }

    public static int getRange(int rangeIndex) {
        return ranges[rangeIndex];
    }

    public int getRangeIndex() {
        return rangeIndex;
    }

    public static int getDefaultRange() { return ranges[defaultRangeIndex]; }

    public static int getDefaultRangeIndex() { return defaultRangeIndex; }
}
