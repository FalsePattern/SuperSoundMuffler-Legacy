package com.falsepattern.ssmlegacy.mixin.mixins.client.vanilla;

import com.falsepattern.ssmlegacy.mixin.interfaces.ISoundHandlerMixin;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(SoundHandler.class)
public abstract class SoundHandlerMixin implements ISoundHandlerMixin {
    @Shadow @Final private SoundRegistry sndRegistry;

    @Override
    public SoundRegistry getSoundRegistry() {
        return sndRegistry;
    }
}
