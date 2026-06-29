package me.Azz_9.unbounded_bundle;

import static me.Azz_9.unbounded_bundle.Constants.MOD_ID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import me.Azz_9.unbounded_bundle.client.gui.components.toasts.CustomToastId;
import me.Azz_9.unbounded_bundle.compat.ClothConfigCompat;

@Mod(MOD_ID)
public class UnboundedBundle {

	public UnboundedBundle(IEventBus eventBus) {
		CommonClass.init();

		ModLoadingContext.get().registerExtensionPoint(
				IConfigScreenFactory.class,
				() -> {
					if (!ModList.get().isLoaded("cloth_config")) {
						Minecraft.getInstance().execute(() ->
								SystemToast.add(
										Minecraft.getInstance().gui.toastManager(),
										CustomToastId.MISSING_CLOTH_CONFIG,
										Component.translatable("unbounded_bundle.toast.missing_cloth_config.title"),
										Component.translatable("unbounded_bundle.toast.missing_cloth_config.message")
								)
						);
						return null;
					}
					return (container, parent) -> ClothConfigCompat.buildClothConfigScreen(parent);
				}
		);
	}
}