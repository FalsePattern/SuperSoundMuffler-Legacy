package com.falsepattern.ssmlegacy.proxy;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.render.ItemBlockSoundMufflerRenderer;
import com.falsepattern.ssmlegacy.render.RenderTileSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ClientProxy extends CommonProxy {
    private Set<TileEntitySoundMuffler> soundMufflers = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySoundMuffler.class, new RenderTileSoundMuffler());
        FMLCommonHandler.instance().bus().register(SuperSoundMuffler.instance);
        RenderingRegistry.registerBlockHandler(new ItemBlockSoundMufflerRenderer());
    }

    @Override
    public void cacheMuffler(TileEntitySoundMuffler tileEntity) {
        soundMufflers.add(tileEntity);
    }

    @Override
    public void uncacheMuffler(TileEntitySoundMuffler tileEntity) {
        soundMufflers.remove(tileEntity);
    }

    @Override
    public void clearCache() {
        soundMufflers.clear();
    }

    @Override
    public Set<TileEntitySoundMuffler> getTileEntities() {
        return soundMufflers;
    }
}
