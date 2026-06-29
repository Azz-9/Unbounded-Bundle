package me.Azz_9.unbounded_bundle.mixin;

import net.minecraft.world.item.component.BundleContents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.Azz_9.unbounded_bundle.Config;

@Mixin(BundleContents.class)
public abstract class BundleContentsMixin {

	@Shadow public abstract int size();

	@Inject(method = "getNumberOfItemsToShow", at = @At("HEAD"), cancellable = true)
	private void getNumberOfItemsToShow(CallbackInfoReturnable<Integer> cir) {
		if (Config.isEnabled()) {
			cir.setReturnValue(this.size());
		}
	}
}
