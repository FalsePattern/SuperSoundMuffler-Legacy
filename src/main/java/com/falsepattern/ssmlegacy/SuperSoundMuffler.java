package com.falsepattern.ssmlegacy;


import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.EvictingQueue;
import com.falsepattern.ssmlegacy.bauble.ItemSoundMufflerBauble;
import com.falsepattern.ssmlegacy.block.BlockSoundMuffler;
import com.falsepattern.ssmlegacy.block.TileEntitySoundMuffler;
import com.falsepattern.ssmlegacy.compat.top.TOPCompatibility;
import com.falsepattern.ssmlegacy.compat.waila.SoundMufflerWailaDataProvider;
import com.falsepattern.ssmlegacy.config.Config;
import com.falsepattern.ssmlegacy.gui.GuiHandler;
import com.falsepattern.ssmlegacy.network.ThePacketeer;
import com.falsepattern.ssmlegacy.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.Set;

@Mod(modid = SuperSoundMuffler.MOD_ID, name = SuperSoundMuffler.NAME, version = SuperSoundMuffler.VERSION, dependencies = SuperSoundMuffler.DEPENDENCIES)
public class SuperSoundMuffler {
    public static final String MOD_ID = "supersoundmuffler";
    public static final String NAME = "Super Sound Muffler";
    public static final String VERSION = "1.0.2.10";
    public static final String DEPENDENCIES = "after:baubles;after:theoneprobe;after:waila";

    @Mod.Instance(MOD_ID)
    public static SuperSoundMuffler instance;

    public static final Logger log = LogManager.getLogger(NAME);
    private boolean checkBaubleSlots = false;

    @SidedProxy(clientSide = "com.falsepattern.ssmlegacy.proxy.ClientProxy", serverSide = "com.falsepattern.ssmlegacy.proxy.CommonProxy")
    public static CommonProxy proxy;

    @GameRegistry.ObjectHolder(BlockSoundMuffler.NAME)
    public static BlockSoundMuffler blockSoundMuffler;

    @GameRegistry.ObjectHolder(ItemSoundMufflerBauble.NAME)
    public static ItemSoundMufflerBauble itemSoundMufflerBauble;

    public Queue<ResourceLocation> recentSounds = EvictingQueue.create(16);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.readConfig(event.getSuggestedConfigurationFile());
        ThePacketeer.init();

        if (event.getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        if (Loader.isModLoaded("waila")) {
            SoundMufflerWailaDataProvider.register();
        }

        if (Loader.isModLoaded("theoneprobe")) {
            TOPCompatibility.register();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        checkBaubleSlots = Loader.isModLoaded("baubles");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlaySound(PlaySoundEvent event) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world != null) {
            ISound sound = event.getSound();

            if (tryMuffleBauble(event, sound)) {
                return;
            }
            if (tryMuffleBlock(event, world, sound)) {
                return;
            }

            recentSounds.offer(sound.getSoundLocation());
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean tryMuffleBauble(PlaySoundEvent event, ISound sound) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            InventoryPlayer inventory = player.inventory;
            for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() == itemSoundMufflerBauble) {
                    if (itemSoundMufflerBauble.shouldMuffleSound(stack, sound.getSoundLocation())) {
                        event.setResultSound(null);
                        return true;
                    }
                }
            }

            if (checkBaubleSlots) {
                IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, player.getHorizontalFacing());
                for (int slot = 0; slot < baubles.getSlots(); ++slot) {
                    ItemStack stack = baubles.getStackInSlot(slot);
                    if (!stack.isEmpty() && stack.getItem() == itemSoundMufflerBauble) {
                        if (itemSoundMufflerBauble.shouldMuffleSound(stack, sound.getSoundLocation())) {
                            event.setResultSound(null);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private boolean tryMuffleBlock(PlaySoundEvent event, WorldClient world, ISound sound) {
        Set<TileEntitySoundMuffler> mufflers = SuperSoundMuffler.proxy.getTileEntities();
        for (TileEntitySoundMuffler tile : mufflers) {
            if (!tile.isInvalid() && world == tile.getWorld() && tile.shouldMuffleSound(sound)) {
                event.setResultSound(null);
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