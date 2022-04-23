package com.falsepattern.ssmlegacy.mixin.mixins.client.fml;

import com.falsepattern.ssmlegacy.mixin.interfaces.IGuiScrollingListMixin;
import cpw.mods.fml.client.GuiScrollingList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiScrollingList.class)
public abstract class GuiScrollingListMixin implements IGuiScrollingListMixin {
    @Shadow
    private int selectedIndex;

    @Override
    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public void setSelectedIndex(int value) {
        selectedIndex = value;
    }
}
