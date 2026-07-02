package me.Azz_9.unbounded_bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Azz_9.unbounded_bundle.platform.Services;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.Azz_9.unbounded_bundle.Constants.MOD_ID;

public class Config {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_FILE = Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json");

	private boolean enabled = true;
	private int maxColumns = 6;
	private int minColumns = 4;
	private boolean scrollable = true;
	private boolean smoothScrolling = false;
	private int maxRows = 3;

	public static Config INSTANCE = new Config();

	public static void save() {
		BundleLogger.info("Saving config...");

		try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
			GSON.toJson(INSTANCE, writer);
		} catch (IOException e) {
			BundleLogger.error("Failed to save config file : {}", e.getMessage());
			return;
		}

		BundleLogger.info("Config successfully saved!");
	}

	public static void load() {
		BundleLogger.info("Loading config...");
		if (!Files.exists(CONFIG_FILE)) {
			BundleLogger.info("Config file does not exist, creating a new one");
			Config.save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
			INSTANCE = GSON.fromJson(reader, Config.class);
		} catch (IOException e) {
			BundleLogger.error("Failed to load config file : {}", e.getMessage());
			return;
		}

		BundleLogger.info("Config successfully loaded!");
	}

	public static boolean isEnabled() {
		return INSTANCE.enabled;
	}

	public static void setEnabled(boolean enabled) {
		INSTANCE.enabled = enabled;
	}

	public static int getMaxColumns() {
		return INSTANCE.maxColumns;
	}

	public static void setMaxColumns(int maxColumns) {
		INSTANCE.maxColumns = maxColumns;
	}

	public static int getMinColumns() {
		return INSTANCE.minColumns;
	}

	public static void setMinColumns(int minColumns) {
		INSTANCE.minColumns = minColumns;
	}

	public static boolean isScrollable() {
		return INSTANCE.scrollable;
	}

	public static void setScrollable(boolean scrollable) {
		INSTANCE.scrollable = scrollable;
	}

	public static boolean isSmoothScrolling() {
		return INSTANCE.smoothScrolling;
	}

	public static void setSmoothScrolling(boolean smoothScrolling) {
		INSTANCE.smoothScrolling = smoothScrolling;
	}

	public static int getMaxRows() {
		return INSTANCE.maxRows;
	}

	public static void setMaxRows(int maxRows) {
		INSTANCE.maxRows = maxRows;
	}
}
