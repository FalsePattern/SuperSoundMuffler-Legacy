package com.falsepattern.ssmlegacy.proxy;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.Tags;
import com.falsepattern.ssmlegacy.bauble.ItemSoundMufflerBauble;
import com.falsepattern.ssmlegacy.block.BlockSoundMuffler;
import com.falsepattern.ssmlegacy.block.ItemBlockSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.compat.waila.SoundMufflerWailaDataProvider;
import com.falsepattern.ssmlegacy.config.Config;
import com.falsepattern.ssmlegacy.gui.GuiHandler;
import com.falsepattern.ssmlegacy.network.ThePacketeer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Set;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.readConfig(event.getSuggestedConfigurationFile());
        ThePacketeer.init();

        SuperSoundMuffler.blockSoundMuffler = new BlockSoundMuffler();
        GameRegistry.registerBlock(SuperSoundMuffler.blockSoundMuffler, ItemBlockSoundMuffler.class, BlockSoundMuffler.NAME);
        GameRegistry.registerTileEntity(TileEntitySoundMuffler.class, Tags.MODID + ":" + BlockSoundMuffler.NAME);

        SuperSoundMuffler.itemSoundMufflerBauble = new ItemSoundMufflerBauble();
        GameRegistry.registerItem(SuperSoundMuffler.itemSoundMufflerBauble, ItemSoundMufflerBauble.NAME);
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(SuperSoundMuffler.instance, new GuiHandler());
        if (Loader.isModLoaded("Waila")) {
            SoundMufflerWailaDataProvider.register();
        }

        GameRegistry.addRecipe(new ItemStack(SuperSoundMuffler.blockSoundMuffler), " W ", "WNW", " W ", 'W', Blocks.wool, 'N', Blocks.noteblock);
        GameRegistry.addRecipe(new ItemStack(SuperSoundMuffler.itemSoundMufflerBauble), " S ", "S S", " M ", 'S', Items.string, 'M', SuperSoundMuffler.blockSoundMuffler);
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void cacheMuffler(TileEntitySoundMuffler tileEntity) {}

    public void uncacheMuffler(TileEntitySoundMuffler tileEntity) {}

    public void clearCache() {}

    public Set<TileEntitySoundMuffler> getTileEntities() {return Collections.emptySet();}
}
