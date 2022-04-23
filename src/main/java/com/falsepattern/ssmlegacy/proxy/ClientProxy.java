package com.falsepattern.ssmlegacy.proxy;

import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ClientProxy extends CommonProxy {
    private Set<TileEntitySoundMuffler> soundMufflers = Collections.newSetFromMap(new WeakHashMap<>());
// TODO
//    @SubscribeEvent
//    public static void registerModels(ModelRegistryEvent event) {
//        SuperSoundMuffler.blockSoundMuffler.registerModels();
//        SuperSoundMuffler.itemSoundMufflerBauble.registerModels();
//    }

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
