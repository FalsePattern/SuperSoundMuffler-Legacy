package com.falsepattern.ssmlegacy.mixin.mixins.client.vanilla;

import com.falsepattern.ssmlegacy.SuperSoundMuffler;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

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

    // Temporary workaround for https://github.com/FalsePattern/SuperSoundMuffler-Legacy/issues/2
    @Redirect(method = "updateAllSounds",
              at = @At(value = "INVOKE",
                       target = "Ljava/util/Iterator;hasNext()Z"))
    private boolean tryFixFallback(Iterator instance) {
        try {
            return instance.hasNext();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            SuperSoundMuffler.log.warn("SSM suppressed a crash in the sound manager. This is a temporary fix until the true reason is found.");
            return false;
        }
    }
}
