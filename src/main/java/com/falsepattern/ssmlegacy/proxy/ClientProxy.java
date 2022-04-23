package com.falsepattern.ssmlegacy.proxy;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private Set<TileEntitySoundMuffler> soundMufflers = Collections.newSetFromMap(new WeakHashMap<>());

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        SuperSoundMuffler.blockSoundMuffler.registerModels();
        SuperSoundMuffler.itemSoundMufflerBauble.registerModels();
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
