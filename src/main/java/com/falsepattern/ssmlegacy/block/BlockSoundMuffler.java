package com.falsepattern.ssmlegacy.block;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.gui.GuiHandler;
import com.falsepattern.ssmlegacy.render.ItemBlockSoundMufflerRenderer;
import lombok.val;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockSoundMuffler extends BlockContainer {
    public static final String NAME = "sound_muffler";

    public BlockSoundMuffler() {
        super(Material.wood);
        setBlockName(NAME);
        setHardness(0.1F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(.1f, .1f, .1f, .9f, .9f, .9f);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float subX, float subY, float subZ) {
        val te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntitySoundMuffler && !te.isInvalid()) {
            playerIn.openGui(SuperSoundMuffler.instance, GuiHandler.SOUND_MUFFLER_GUI_ID, worldIn, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        val te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntitySoundMuffler) {
            val tileEntity = (TileEntitySoundMuffler) te;

            if (stack.hasTagCompound() && (stack.getTagCompound().hasKey("sounds"))) {
                tileEntity.readNBT(stack.getTagCompound());
            }
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(world, player, x, y, z, false);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(worldIn, player, x, y, z, meta);
        worldIn.setBlockToAir(x, y, z);
    }

    @Override
    public String getHarvestTool(int metadata) {
        return null;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        val list = new ArrayList<ItemStack>();
        val stack = new ItemStack(this, 1);
        val te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntitySoundMuffler) {
            val tileEntity = (TileEntitySoundMuffler) te;
            if(!tileEntity.isDefault()) {
                val compound = new NBTTagCompound();
                tileEntity.writeNBT(compound);
                stack.setTagCompound(compound);
            }
        }
        list.add(stack);
        return list;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySoundMuffler();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }


    @Override
    public boolean canRenderInPass(int pass) {
        return false;
    }

    @Override
    public int getRenderType() {
        return ItemBlockSoundMufflerRenderer.ID;
    }
}
