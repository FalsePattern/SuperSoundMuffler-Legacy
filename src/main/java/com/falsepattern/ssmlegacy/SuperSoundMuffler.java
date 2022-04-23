package com.falsepattern.ssmlegacy;


import baubles.api.BaublesApi;
import com.google.common.collect.EvictingQueue;
import com.falsepattern.ssmlegacy.bauble.ItemSoundMufflerBauble;
import com.falsepattern.ssmlegacy.block.BlockSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.proxy.CommonProxy;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.Set;

@Mod(modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     dependencies = "after:Waila;after:Baubles")
public class SuperSoundMuffler {
    @Mod.Instance(Tags.MODID)
    public static SuperSoundMuffler instance;

    public static final Logger log = LogManager.getLogger(Tags.MODNAME);
    @Getter
    private static boolean baublesPresent = false;

    @SidedProxy(clientSide = Tags.GROUPNAME + ".proxy.ClientProxy", serverSide = Tags.GROUPNAME + ".proxy.CommonProxy")
    public static CommonProxy proxy;

    public static BlockSoundMuffler blockSoundMuffler;

    public static ItemSoundMufflerBauble itemSoundMufflerBauble;

    public Queue<ResourceLocation> recentSounds = EvictingQueue.create(16);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        baublesPresent = Loader.isModLoaded("Baubles");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldMuffle(ISound sound) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world != null) {

            if (tryMuffleBauble(sound)) {
                return true;
            }

            if (tryMuffleBlock(world, sound)) {
                return true;
            }

            recentSounds.offer(sound.getPositionedSoundLocation());
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private boolean tryMuffleBauble(ISound sound) {
        val player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            val inventory = player.inventory;
            for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
                val stack = inventory.getStackInSlot(slot);
                if (stack != null && stack.getItem() == itemSoundMufflerBauble) {
                    if (itemSoundMufflerBauble.shouldMuffleSound(stack, sound.getPositionedSoundLocation())) {
                        return true;
                    }
                }
            }

            if (baublesPresent) {
                val baubles = BaublesApi.getBaubles(player);
                for (int slot = 0; slot < baubles.getSizeInventory(); ++slot) {
                    val stack = baubles.getStackInSlot(slot);
                    if (stack != null && stack.getItem() == itemSoundMufflerBauble) {
                        if (itemSoundMufflerBauble.shouldMuffleSound(stack, sound.getPositionedSoundLocation())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private boolean tryMuffleBlock(WorldClient world, ISound sound) {
        Set<TileEntitySoundMuffler> mufflers = SuperSoundMuffler.proxy.getTileEntities();
        for (TileEntitySoundMuffler tile : mufflers) {
            if (!tile.isInvalid() && world == tile.getWorldObj() && tile.shouldMuffleSound(sound)) {
                return true;
            }
        }
        return false;
    }

    public static int ticksInGame = 0;
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null || !gui.doesGuiPauseGame()) {
                ticksInGame++;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
         proxy.clearCache();
    }
}