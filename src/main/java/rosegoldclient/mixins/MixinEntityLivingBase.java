package rosegoldclient.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegoldclient.features.SpellCaster;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    public void swingArm(EnumHand hand, CallbackInfo ci) {
        if(SpellCaster.cancelServerSwing) {
            ci.cancel();
            SpellCaster.cancelServerSwing = false;
        }
    }
}
