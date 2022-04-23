package com.falsepattern.ssmlegacy.gui;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.Tags;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.gui.data.IMufflerAccessor;
import com.falsepattern.ssmlegacy.mixin.interfaces.IGuiScrollingListMixin;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

@SideOnly(Side.CLIENT)
public class GuiSoundMuffler extends GuiContainer implements GuiSlider.ISlider {

    private static final ResourceLocation guiTexture = new ResourceLocation(Tags.MODID, "textures/gui/sound_muffler.png");

    private final IMufflerAccessor muffler;

    private GuiButtonExt modeButton;
    private GuiButtonExt addSoundButton;
    private GuiButtonExt removeSoundButton;
    private GuiSliderExt rangeSlider;
    private GuiSoundList soundList;

    public GuiSoundMuffler(IMufflerAccessor muffler) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        });

        this.xSize = 256;
        this.ySize = 170;
        this.muffler = muffler;
    }

    @Override
    public void initGui() {
        super.initGui();
        String key = muffler.isWhiteList() ? "tile.sound_muffler.gui.button.mode.white_list" : "tile.sound_muffler.gui.button.mode.black_list";
        modeButton = new GuiButtonExt(0, guiLeft + 159, guiTop + 5, 90, 14, I18n.format(key));
        buttonList.add(modeButton);

        addSoundButton = new GuiButtonExt(1, guiLeft + 159, guiTop + 151, 44, 14, I18n.format("tile.sound_muffler.gui.button.add"));
        buttonList.add(addSoundButton);

        removeSoundButton = new GuiButtonExt(2, guiLeft + 205, guiTop + 151,44, 14, I18n.format("tile.sound_muffler.gui.button.remove"));
        removeSoundButton.enabled = false;
        buttonList.add(removeSoundButton);

        if(muffler.isRanged()) {
            rangeSlider = new GuiSliderExt(3, guiLeft + 7, guiTop + 151, 128, 14, I18n.format("tile.sound_muffler.gui.slider.range"), "", 0f, 19f, muffler.getRangeIndex(), false, false, this);
            buttonList.add(rangeSlider);
        }

        soundList = new GuiSoundList(240, 126, guiTop + 22, guiTop + 148, guiLeft + 8, 14);
        List<ResourceLocation> sounds = muffler.getMuffledSounds();
        sounds.sort(Comparator.comparing(ResourceLocation::toString));
        soundList.setSounds(sounds);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        removeSoundButton.enabled = soundList.hasSelectedElements();

        List<ResourceLocation> sounds = muffler.getMuffledSounds();
        sounds.sort(Comparator.comparing(ResourceLocation::toString));
        soundList.setSounds(sounds);

        String key = muffler.isWhiteList() ? "tile.sound_muffler.gui.button.mode.white_list" : "tile.sound_muffler.gui.button.mode.black_list";
        modeButton.displayString = I18n.format(key);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled) {
            if (button.id == modeButton.id) {
                muffler.toggleWhiteList();
                String key = muffler.isWhiteList() ? "tile.sound_muffler.gui.button.mode.white_list" : "tile.sound_muffler.gui.button.mode.black_list";
                modeButton.displayString = I18n.format(key);
            } else if (button.id == addSoundButton.id) {
                Set<ResourceLocation> unique = new HashSet<>(SuperSoundMuffler.instance.recentSounds);
                Minecraft.getMinecraft().displayGuiScreen(new GuiSoundMufflerAddSound(this, muffler, new ArrayList<>(unique)));
            } else if(button.id == removeSoundButton.id) {
                List<ResourceLocation> selectedSounds = soundList.getSelectedSounds();
                for(ResourceLocation sound : selectedSounds) {
                    if(sound != null) {
                        muffler.unmuffleSound(sound);
                    }
                }
                soundList.clearSelection();
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(Tags.MODNAME, 8, 9, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        RenderHelper.disableStandardItemLighting();
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;

        mc.getTextureManager().bindTexture(guiTexture);
        drawTexturedModalRect(xPos, yPos, 0, 0, xSize, ySize);
        soundList.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        slider.displayString = slider.dispString + " " + TileEntitySoundMuffler.getRange(slider.getValueInt());
        muffler.setRange(slider.getValueInt());
    }

    private final class GuiSoundList extends GuiScrollingList {
        private List<ResourceLocation> sounds;
        private final int slotHeight;
        private List<Integer> selectedIndicies = new ArrayList<>();

        GuiSoundList(int width, int height, int top, int bottom, int left, int slotHeight) {
            super(Minecraft.getMinecraft(), width, height, top, bottom, left, slotHeight);
            this.slotHeight = slotHeight;
        }

        @Override
        protected int getSize() {
            return sounds.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {
            if(isCtrlKeyDown()) {
                if(isSelected(index)) {
                    removeSelection(index);
                } else {
                    selectIndex(index);
                }
            } else if(isShiftKeyDown()) {
                clearSelection();
                val selectedIndex = ((IGuiScrollingListMixin)(Object)this).getSelectedIndex();
                int start = Math.min(index, selectedIndex);
                int end = Math.max(index, selectedIndex);
                selectRange(start, end);
            } else {
                clearSelection();
                selectIndex(index);
            }
        }

        @Override
        protected boolean isSelected(int index) {
            for(int i : selectedIndicies) {
                if(i == index) {
                    return true;
                }
            }
            return false;
        }

        void removeSelection(int index) {
            for(int i = 0; i < selectedIndicies.size(); i++) {
                if(selectedIndicies.get(i) == index) {
                    selectedIndicies.remove(i);
                    return;
                }
            }
        }

        void selectIndex(int index) {
            removeSelection(index);
            selectedIndicies.add(index);
            ((IGuiScrollingListMixin)(Object)this).setSelectedIndex(index);
        }

        void clearSelection() {
            selectedIndicies.clear();
        }

        void selectRange(int start, int end) {
            for(int i = start; i <= end; i++) {
                selectedIndicies.add(i);
            }
            ((IGuiScrollingListMixin)(Object)this).setSelectedIndex(end);
        }

        @Override
        protected void drawBackground() { }

        @Override
        protected int getContentHeight() {
            return (getSize()) * slotHeight + 1;
        }

        @Override
        protected void drawSlot(int idx, int right, int top, int height, Tessellator tess) {
            ResourceLocation sound = sounds.get(idx);
            fontRendererObj.drawString(fontRendererObj.trimStringToWidth(sound.toString(), listWidth - 10), left + 3 , top +  2, 0xCCCCCC);
        }

        void setSounds(List<ResourceLocation> sounds) {
            this.sounds = sounds;
        }

        boolean hasSelectedElements() { return selectedIndicies.size() > 0; }

        List<ResourceLocation> getSelectedSounds() {
            List<ResourceLocation> ret = new ArrayList<>();

            for(int i : selectedIndicies) {
                ret.add(sounds.get(i));
            }

            return ret;
        }
    }

    private static final class GuiSliderExt extends GuiSlider {
        GuiSliderExt(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par) {
            super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
            displayString = dispString + " " + TileEntitySoundMuffler.getRange(getValueInt());
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                if (this.dragging) {
                    this.sliderValue = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                    updateSlider();
                }
                GL11.glColor4f(1, 1, 1, 1);

                GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 8, height, 200, 20, 2, 3, 2, 2, zLevel);
            }
        }
    }
}
