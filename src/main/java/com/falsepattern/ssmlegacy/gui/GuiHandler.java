package com.falsepattern.ssmlegacy.gui;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.gui.data.MufflerBauble;
import com.falsepattern.ssmlegacy.gui.data.IMufflerAccessor;
import com.falsepattern.ssmlegacy.gui.data.MufflerTileEntity;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int SOUND_MUFFLER_GUI_ID = 0;
    public static final int SOUND_MUFFLER_BAUBLE_GUI_ID = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == SOUND_MUFFLER_GUI_ID) {
            val tile = world.getTileEntity(x, y, z);
            if(tile instanceof TileEntitySoundMuffler) {
                val muffler = new MufflerTileEntity((TileEntitySoundMuffler)tile);
                return new GuiSoundMuffler(muffler);
            }
        } else if(ID == SOUND_MUFFLER_BAUBLE_GUI_ID) {
            val stack = player.getHeldItem();
            if(stack != null && stack.getItem() == SuperSoundMuffler.itemSoundMufflerBauble) {
                IMufflerAccessor muffler = new MufflerBauble(player);
                return new GuiSoundMuffler(muffler);
            }
        }

        return null;
    }
}
