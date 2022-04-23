package com.falsepattern.ssmlegacy;


import com.google.common.collect.EvictingQueue;
import com.falsepattern.ssmlegacy.bauble.ItemSoundMufflerBauble;
import com.falsepattern.ssmlegacy.block.BlockSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.proxy.CommonProxy;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.Set;

@Mod(modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     dependencies = "after:waila")
public class SuperSoundMuffler {
    @Mod.Instance(Tags.MODID)
    public static SuperSoundMuffler instance;

    public static final Logger log = LogManager.getLogger(Tags.MODNAME);
    private boolean checkBaubleSlots = false;

    @SidedProxy(clientSide = Tags.GROUPNAME + ".proxy.ClientProxy", serverSide = Tags.GROUPNAME + ".proxy.CommonProxy")
    public static CommonProxy proxy;

    @GameRegistry.ObjectHolder(BlockSoundMuffler.NAME)
    public static BlockSoundMuffler blockSoundMuffler;

    @GameRegistry.ObjectHolder(ItemSoundMufflerBauble.NAME)
    public static ItemSoundMufflerBauble itemSoundMufflerBauble;

    public Queue<ResourceLocation> recentSounds = EvictingQueue.create(16);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        checkBaubleSlots = Loader.isModLoaded("baubles");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlaySound(SoundEvent.SoundSourceEvent event) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world != null) {
            ISound sound = event.sound;

            //TODO
//            if (tryMuffleBauble(event, sound)) {
//                return;
//            }
            if (tryMuffleBlock(event, world, sound)) {
                return;
            }

            recentSounds.offer(sound.getPositionedSoundLocation());
        }
    }

// TODO
//    @SideOnly(Side.CLIENT)
//    private boolean tryMuffleBauble(SoundEvent event, ISound sound) {
//        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//        if (player != null) {
//            InventoryPlayer inventory = player.inventory;
//            for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
//                ItemStack stack = inventory.getStackInSlot(slot);
//                if (stack != null && stack.getItem() == itemSoundMufflerBauble) {
//                    if (itemSoundMufflerBauble.shouldMuffleSound(stack, sound.getPositionedSoundLocation())) {
//                        event.setCanceled(true);
//                        return true;
//                    }
//                }
//            }
//
//            if (checkBaubleSlots) {
//                IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, player.getHorizontalFacing());
//                for (int slot = 0; slot < baubles.getSlots(); ++slot) {
//                    ItemStack stack = baubles.getStackInSlot(slot);
//                    if (!stack.isEmpty() && stack.getItem() == itemSoundMufflerBauble) {
//                        if (itemSoundMufflerBauble.shouldMuffleSound(stack, sound.getSoundLocation())) {
//                            event.setResultSound(null);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }

    @SideOnly(Side.CLIENT)
    private boolean tryMuffleBlock(SoundEvent.SoundSourceEvent event, WorldClient world, ISound sound) {
        Set<TileEntitySoundMuffler> mufflers = SuperSoundMuffler.proxy.getTileEntities();
        for (TileEntitySoundMuffler tile : mufflers) {
            if (!tile.isInvalid() && world == tile.getWorldObj() && tile.shouldMuffleSound(sound)) {
                event.setCanceled(true);
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