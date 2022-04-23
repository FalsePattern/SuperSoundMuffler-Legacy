package com.falsepattern.ssmlegacy.gui;

import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public abstract class GuiScrollingListExt extends GuiScrollingList {
    public GuiScrollingListExt(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight) {
        super(client, width, height, top, bottom, left, entryHeight);
    }

    private static final Field selectedIndexField = ReflectionHelper.findField(GuiScrollingList.class, "selectedIndex");

    @SneakyThrows
    protected int getSelectedIndex() {
        return selectedIndexField.getInt(this);
    }

    @SneakyThrows
    protected void setSelectedIndex(int value) {
        selectedIndexField.setInt(this, value);
    }
}
