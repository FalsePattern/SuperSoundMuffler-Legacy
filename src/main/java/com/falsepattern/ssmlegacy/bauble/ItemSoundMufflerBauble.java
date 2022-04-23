package com.falsepattern.ssmlegacy.bauble;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.Tags;
import com.falsepattern.ssmlegacy.gui.GuiHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(modid = "Baubles",
                    iface = "baubles.api.IBauble")
public class ItemSoundMufflerBauble extends Item implements IBauble {
    private IIcon disabledIcon;
    public static final String NAME = "sound_muffler_bauble";

    public ItemSoundMufflerBauble() {
        setUnlocalizedName(NAME);
        setTextureName(Tags.MODID + ":" + NAME);
        setNoRepair();
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (playerIn.isSneaking()) {
            toggleDisabled(playerIn, stack);
        } else {
            playerIn.openGui(SuperSoundMuffler.instance, GuiHandler.SOUND_MUFFLER_BAUBLE_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
        }
        return stack;
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return damage == 0 ? itemIcon : disabledIcon;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        disabledIcon = register.registerIcon(iconString + "_disabled");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean flagIn) {
        tooltip.add(I18n.format("item.sound_muffler_bauble.tooltip.header"));

        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();

            boolean showWhiteListTooltip = !compound.hasKey("whiteList") || compound.getBoolean("whiteList");
            String key = showWhiteListTooltip ? "item.sound_muffler.tooltip.mode.white_list" : "item.sound_muffler.tooltip.mode.black_list";
            tooltip.add(I18n.format(key));

            if (compound.hasKey("sounds")) {
                NBTTagList tagList = compound.getTagList("sounds", 10);
                int count = tagList.tagCount();
                tooltip.add(I18n.format("item.sound_muffler.tooltip.sounds.count", count));
                if (GuiScreen.isShiftKeyDown()) {
                    for (int i = 0; i < tagList.tagCount(); ++i) {
                        NBTTagCompound sound = tagList.getCompoundTagAt(i);
                        tooltip.add(I18n.format("item.sound_muffler.tooltip.sound", sound.getString("sound")));
                    }
                }
            } else {
                tooltip.add(I18n.format("item.sound_muffler.tooltip.sounds.count", 0));
            }
        } else {
            tooltip.add(I18n.format("item.sound_muffler.tooltip.mode.black_list"));
            tooltip.add(I18n.format("item.sound_muffler.tooltip.sounds.count", 0));
        }
    }

    public boolean shouldMuffleSound(ItemStack stack, ResourceLocation sound) {
        if (!stack.hasTagCompound()) {return false;}

        NBTTagCompound compound = stack.getTagCompound();
        if (compound.hasKey("disabled")) {return false;}

        boolean isWhiteList = compound.hasKey("whiteList") && compound.getBoolean("whiteList");
        if (compound.hasKey("sounds")) {
            NBTTagList tags = compound.getTagList("sounds", 10);
            if (containsSound(tags, sound)) {
                return !isWhiteList;
            }
        }

        return isWhiteList;
    }

    public void toggleWhiteList(ItemStack stack) {
        boolean isWhiteList = false;

        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("whiteList")) {
                isWhiteList = compound.getBoolean("whiteList");
            }

            compound.setBoolean("whiteList", !isWhiteList);
            stack.setTagCompound(compound);
        } else {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("whiteList", !isWhiteList);
            stack.setTagCompound(compound);
        }

    }

    public void muffleSound(ItemStack stack, ResourceLocation sound) {
        NBTTagCompound compound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        NBTTagList tags = compound.hasKey("sounds") ? compound.getTagList("sounds", 10) : new NBTTagList();

        if (containsSound(tags, sound)) {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("sound", sound.toString());
        tags.appendTag(tag);
        compound.setTag("sounds", tags);
        stack.setTagCompound(compound);
    }

    public void unmuffleSound(ItemStack stack, ResourceLocation sound) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("sounds")) {
                NBTTagList tags = compound.getTagList("sounds", 10);
                NBTTagList newTags = new NBTTagList();
                for (int i = 0; i < tags.tagCount(); ++i) {
                    NBTTagCompound s = tags.getCompoundTagAt(i);
                    String soundLocation = s.getString("sound");
                    if (!soundLocation.equals(sound.toString())) {
                        newTags.appendTag(s);
                    }
                }
                compound.setTag("sounds", newTags);
                stack.setTagCompound(compound);
            }
        }
    }

    private boolean containsSound(NBTTagList tags, ResourceLocation sound) {
        for (int i = 0; i < tags.tagCount(); ++i) {
            NBTTagCompound s = tags.getCompoundTagAt(i);
            String soundLocation = s.getString("sound");
            if (soundLocation.equals(sound.toString())) {
                return true;
            }
        }

        return false;
    }

    private boolean isDisabled(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            return compound.hasKey("disabled");
        }

        return false;
    }

    private void toggleDisabled(EntityPlayer playerIn, ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("disabled")) {
                compound.removeTag("disabled");
                stack.setTagCompound(compound);
                playerIn.playSound("random.orb", 0.1F, 1F);
                stack.setItemDamage(0);
            } else {
                compound.setBoolean("disabled", true);
                stack.setTagCompound(compound);
                playerIn.playSound("random.orb", 0.1F, 0.8F);
                stack.setItemDamage(1);
            }
        } else {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("disabled", true);
            stack.setTagCompound(compound);
            playerIn.playSound("random.orb", 0.1F, 0.8F);
            stack.setItemDamage(1);
        }
    }
}
