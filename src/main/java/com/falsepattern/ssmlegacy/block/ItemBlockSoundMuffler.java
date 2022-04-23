package com.falsepattern.ssmlegacy.block;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockSoundMuffler extends ItemBlock {
    public ItemBlockSoundMuffler(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean flag) {
        if(stack.hasTagCompound()) {
            val compound = stack.getTagCompound();

            val showWhiteListTooltip = !compound.hasKey("whiteList") || compound.getBoolean("whiteList");
            val key = showWhiteListTooltip ? "item.sound_muffler.tooltip.mode.white_list" : "item.sound_muffler.tooltip.mode.black_list";
            tooltip.add(I18n.format(key));

            int rangeIndex = compound.hasKey("rangeIndex") ? compound.getInteger("rangeIndex") : TileEntitySoundMuffler.getDefaultRangeIndex();
            tooltip.add(I18n.format("item.sound_muffler.tooltip.range", TileEntitySoundMuffler.getRange(rangeIndex)));

            if(compound.hasKey("sounds")) {
                val tagList = compound.getTagList("sounds", 10);
                val count = tagList.tagCount();
                tooltip.add(I18n.format("item.sound_muffler.tooltip.sounds.count", count));
                if(GuiScreen.isShiftKeyDown()) {
                    for(int i = 0; i < tagList.tagCount(); ++i) {
                        val sound = tagList.getCompoundTagAt(i);
                        tooltip.add(I18n.format("item.sound_muffler.tooltip.sound", sound.getString("sound")));
                    }
                }
            } else {
                tooltip.add(I18n.format("item.sound_muffler.tooltip.sounds.count", 0));
            }
        } else {
            tooltip.add(I18n.format("item.sound_muffler.tooltip.mode.white_list"));
            tooltip.add(I18n.format("item.sound_muffler.tooltip.range", TileEntitySoundMuffler.getDefaultRange()));
            tooltip.add(I18n.format("item.sound_muffler.tooltip.sounds.count", 0));
        }
    }


}
