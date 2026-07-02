package me.Azz_9.unbounded_bundle.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import me.Azz_9.unbounded_bundle.client.gui.components.toasts.CustomToastId;

import static me.Azz_9.unbounded_bundle.Constants.CLOTH_CONFIG_ID_FABRIC;

public class ModMenuCompat implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded(CLOTH_CONFIG_ID_FABRIC)) {
			return parent -> {
				Minecraft.getInstance().execute(() ->
						SystemToast.add(
								Minecraft.getInstance().gui.toastManager(),
								CustomToastId.MISSING_CLOTH_CONFIG,
								Component.translatable("unbounded_bundle.toast.missing_cloth_config.title"),
								Component.translatable("unbounded_bundle.toast.missing_cloth_config.message")
						)
				);
				return null;
			};
		}
		return ClothConfigCompat::buildClothConfigScreen;
	}
}
