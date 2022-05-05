package com.falsepattern.ssmlegacy.mixin.mixins.client.vanilla;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {
    @Inject(method = "playSound",
            at = @At(value = "HEAD"),
            require = 1,
            cancellable = true)
    private void handleCancel(ISound sound, CallbackInfo ci) {
        if (SuperSoundMuffler.instance != null && SuperSoundMuffler.instance.shouldMuffle(sound)) {
            ci.cancel();
        }
    }
}
