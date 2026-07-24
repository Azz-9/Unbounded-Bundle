package me.Azz_9.unbounded_bundle.platform;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import me.Azz_9.unbounded_bundle.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
