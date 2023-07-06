package com.falsepattern.ssmlegacy.gui;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.Tags;
import com.falsepattern.ssmlegacy.gui.data.IMufflerAccessor;
import com.falsepattern.ssmlegacy.mixin.interfaces.IGuiScrollingListMixin;
import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.client.config.GuiButtonExt;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.*;
import java.util.stream.Collectors;

public class GuiSoundMufflerAddSound extends GuiContainer {
    private static final ResourceLocation guiTexture = new ResourceLocation(Tags.MODID, "textures/gui/sound_muffler_add_sound.png");

    private static final int KEYCODE_ENTER = 28;
    private static final int KEYCODE_KP_ENTER = 156;
    private static final int TEXT_COLOR_FOCUSED = 0xE0E0E0;
    private static final int TEXT_COLOR_ACTIVE = 0xAAAAAA;
    private static final int TEXT_COLOR_DISABLED = 0x404040;

    private final GuiScreen prevScreen;
    private final IMufflerAccessor muffler;

    private GuiButtonExt allSoundsButton;
    private GuiButtonExt recentSoundsButton;
    private GuiButtonExt addSoundButton;
    private GuiButtonExt cancelButton;
    private GuiTextField searchField;
    private GuiSoundList soundList;

    private String lastFilterText = "";
    private boolean showAllSounds = true;

    private final List<ResourceLocation> recentSounds;
    private List<ResourceLocation> allSounds;

    GuiSoundMufflerAddSound(GuiScreen prevScreen, IMufflerAccessor muffler, List<ResourceLocation> recentSounds) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        });

        this.xSize = 256;
        this.ySize = 170;
        this.prevScreen = prevScreen;
        this.muffler = muffler;
        this.recentSounds = recentSounds;
        lazyLoadAllSoundsList();
    }

    private void lazyLoadAllSoundsList() {
        allSounds = new ArrayList<>();
        allSounds.addAll(getSoundList());
        allSounds.sort(Comparator.comparing(ResourceLocation::toString));
    }

    private Set<ResourceLocation> getSoundList() {
        Set<ResourceLocation> sounds = Collections.EMPTY_SET;
        try {
            val registry = Minecraft.getMinecraft().getSoundHandler().sndRegistry;

            if (registry != null) {
                sounds = registry.getKeys();
            }
        } catch (Throwable t) {
            SuperSoundMuffler.log.error("Couldn't access sound registry for sounds list", t);
        }

        return sounds;
    }

    @Override
    public void initGui() {
        super.initGui();

        allSoundsButton = new GuiButtonExt(0, guiLeft + 159, guiTop + 5, 44, 14, I18n.format("tile.sound_muffler.add_sound.gui.button.all"));
        buttonList.add(allSoundsButton);
        allSoundsButton.enabled = false;
        recentSoundsButton = new GuiButtonExt(1, guiLeft + 205, guiTop + 5, 44, 14, I18n.format("tile.sound_muffler.add_sound.gui.button.recent"));
        buttonList.add(recentSoundsButton);
        addSoundButton = new GuiButtonExt(2, guiLeft + 159, guiTop + 151, 44, 14, I18n.format("tile.sound_muffler.add_sound.gui.button.add"));
        buttonList.add(addSoundButton);
        addSoundButton.enabled = false;
        cancelButton = new GuiButtonExt(3, guiLeft + 205, guiTop + 151, 44, 14, I18n.format("tile.sound_muffler.add_sound.gui.button.cancel"));
        buttonList.add(cancelButton);

        soundList = new GuiSoundList(240, 112, guiTop + 22, guiTop + 134, guiLeft + 8, 14);
        soundList.setSounds(allSounds);

        Keyboard.enableRepeatEvents(true);
        searchField = new GuiTextField(fontRendererObj, guiLeft + 11, guiTop + 139, 232, fontRendererObj.FONT_HEIGHT);
        searchField.setMaxStringLength(256);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setTextColor(0xE0E0E0);
        searchField.setCanLoseFocus(true);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == recentSoundsButton.id) {
                recentSoundsButton.enabled = false;
                allSoundsButton.enabled = true;
                showAllSounds = false;
                updateSoundsList(recentSounds);
                return;
            }

            if (button.id == allSoundsButton.id) {
                allSoundsButton.enabled = false;
                recentSoundsButton.enabled = true;
                showAllSounds = true;
                updateSoundsList(allSounds);
                return;
            }

            if (button.id == addSoundButton.id) {
                List<ResourceLocation> selectedSounds = soundList.getSelectedSounds();
                for (ResourceLocation sound : selectedSounds) {
                    if (sound != null) {
                        muffler.muffleSound(sound);
                    }
                }

                mc.displayGuiScreen(prevScreen);
                return;
            }

            if (button.id == cancelButton.id) {
                mc.displayGuiScreen(prevScreen);
                return;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchField.updateCursorCounter();

        if (!searchField.getText().equals(lastFilterText)) {
            lastFilterText = searchField.getText();
            updateSoundsList(showAllSounds ? allSounds : recentSounds);
        }

        addSoundButton.enabled = soundList.hasSelectedElements();
    }

    private void updateSoundsList(List<ResourceLocation> sounds) {
        if (lastFilterText.isEmpty()) {
            soundList.setSounds(sounds);
        } else {
            soundList.setSounds(sounds.stream().filter(sound -> sound.toString().toLowerCase().contains(lastFilterText.toLowerCase())).collect(Collectors.toList()));
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        searchField.mouseClicked(x, y, button);
        if (button == 1 && x >= searchField.xPosition && x < searchField.xPosition + searchField.width && y >= searchField.yPosition && y < searchField.yPosition + searchField.height) {
            searchField.setText("");
        }
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        super.handleMouseInput();
        //soundList.handleMouseInput(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char c, int keyCode) {
        if (keyCode != KEYCODE_ENTER && keyCode != KEYCODE_KP_ENTER) {
            if (!searchField.textboxKeyTyped(c, keyCode)) {
                super.keyTyped(c, keyCode);
            }
        } else {
            if (searchField.isFocused()) {
                searchField.setFocused(false);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(I18n.format("tile.sound_muffler.add_sound.gui.title"), 8, 9, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        RenderHelper.disableStandardItemLighting();
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;

        mc.getTextureManager().bindTexture(guiTexture);
        drawTexturedModalRect(xPos, yPos, 0, 0, xSize, ySize);
        drawSearchField();
        soundList.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableStandardItemLighting();
    }

    private void drawSearchField() {
        if (searchField.getText().isEmpty() && !searchField.isFocused()) {
            fontRendererObj.drawString(I18n.format("tile.sound_muffler.add_sound.gui.search"), guiLeft + 11, guiTop + 139, TEXT_COLOR_DISABLED);
        } else {
            searchField.setTextColor(searchField.isFocused() ? TEXT_COLOR_FOCUSED : TEXT_COLOR_ACTIVE);
            searchField.drawTextBox();
        }
    }

    private final class GuiSoundList extends GuiScrollingList {
        private List<ResourceLocation> sounds;
        private final int slotHeight;
        private final List<Integer> selectedIndicies = new ArrayList<>();

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
            if (isCtrlKeyDown()) {
                if (isSelected(index)) {
                    removeSelection(index);
                } else {
                    selectIndex(index);
                }
            } else if (isShiftKeyDown()) {
                clearSelection();
                val selectedIndex = ((IGuiScrollingListMixin) (Object) this).getSelectedIndex();
                int start = index > selectedIndex ? selectedIndex : index;
                int end = index > selectedIndex ? index : selectedIndex;
                selectRange(start, end);
            } else {
                clearSelection();
                selectIndex(index);
            }
        }

        @Override
        protected boolean isSelected(int index) {
            for (int i : selectedIndicies) {
                if (i == index) {
                    return true;
                }
            }
            return false;
        }

        void removeSelection(int index) {
            for (int i = 0; i < selectedIndicies.size(); i++) {
                if (selectedIndicies.get(i) == index) {
                    selectedIndicies.remove(i);
                    return;
                }
            }
        }

        void selectIndex(int index) {
            removeSelection(index);
            selectedIndicies.add(index);
            ((IGuiScrollingListMixin) (Object) this).setSelectedIndex(index);
        }

        void clearSelection() {
            selectedIndicies.clear();
        }

        void selectRange(int start, int end) {
            for (int i = start; i <= end; i++) {
                selectedIndicies.add(i);
            }
            ((IGuiScrollingListMixin) (Object) this).setSelectedIndex(end);
        }

        @Override
        protected void drawBackground() {}

        @Override
        protected int getContentHeight() {
            return (getSize()) * slotHeight + 1;
        }

        @Override
        protected void drawSlot(int idx, int right, int top, int height, Tessellator tess) {
            ResourceLocation sound = sounds.get(idx);
            fontRendererObj.drawString(fontRendererObj.trimStringToWidth(sound.toString(), listWidth - 10), left + 3, top + 2, 0xCCCCCC);
        }

        void setSounds(List<ResourceLocation> sounds) {
            this.sounds = sounds;
            ((IGuiScrollingListMixin) (Object) this).setSelectedIndex(-1);
            selectedIndicies.clear();
        }

        boolean hasSelectedElements() {return selectedIndicies.size() > 0;}

        List<ResourceLocation> getSelectedSounds() {
            List<ResourceLocation> ret = new ArrayList<>();

            for (int i : selectedIndicies) {
                ret.add(sounds.get(i));
            }

            return ret;
        }
    }
}
