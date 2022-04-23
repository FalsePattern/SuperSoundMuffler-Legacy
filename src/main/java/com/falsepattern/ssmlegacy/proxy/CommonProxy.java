package com.falsepattern.ssmlegacy.proxy;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.Tags;
import com.falsepattern.ssmlegacy.bauble.ItemSoundMufflerBauble;
import com.falsepattern.ssmlegacy.block.BlockSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.compat.waila.SoundMufflerWailaDataProvider;
import com.falsepattern.ssmlegacy.config.Config;
import com.falsepattern.ssmlegacy.gui.GuiHandler;
import com.falsepattern.ssmlegacy.network.ThePacketeer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.Set;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.readConfig(event.getSuggestedConfigurationFile());
        ThePacketeer.init();

        if (event.getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(this);
        }

        GameRegistry.registerBlock(new BlockSoundMuffler(), BlockSoundMuffler.NAME);
        GameRegistry.registerTileEntity(TileEntitySoundMuffler.class, Tags.MODID + ":" + BlockSoundMuffler.NAME);

        if (SuperSoundMuffler.isBaublesPresent()) {
            GameRegistry.registerItem(new ItemSoundMufflerBauble(), ItemSoundMufflerBauble.NAME);
        }
        GameRegistry.registerItem(new ItemBlock(SuperSoundMuffler.blockSoundMuffler).setUnlocalizedName(SuperSoundMuffler.blockSoundMuffler.getUnlocalizedName()), BlockSoundMuffler.NAME);
    }
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        if (Loader.isModLoaded("waila")) {
            SoundMufflerWailaDataProvider.register();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void cacheMuffler(TileEntitySoundMuffler tileEntity) { }
    public void uncacheMuffler(TileEntitySoundMuffler tileEntity) { }
    public void clearCache() { }
    public Set<TileEntitySoundMuffler> getTileEntities() { return Collections.EMPTY_SET; }
}
