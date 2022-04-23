package com.falsepattern.ssmlegacy.network;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import com.falsepattern.ssmlegacy.network.messages.MessageAddRemoveSound;
import com.falsepattern.ssmlegacy.network.messages.MessageSetRange;
import com.falsepattern.ssmlegacy.network.messages.MessageToggleWhiteList;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ThePacketeer {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SuperSoundMuffler.MOD_ID);
    private static int ids = 0;

    public static void init() {
        ThePacketeer.INSTANCE.registerMessage(MessageAddRemoveSound.Handler.class, MessageAddRemoveSound.class, ids++, Side.SERVER);
        ThePacketeer.INSTANCE.registerMessage(MessageToggleWhiteList.Handler.class, MessageToggleWhiteList.class, ids++, Side.SERVER);
        ThePacketeer.INSTANCE.registerMessage(MessageSetRange.Handler.class, MessageSetRange.class, ids++, Side.SERVER);
    }
}
