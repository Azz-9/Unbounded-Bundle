package me.Azz_9.unbounded_bundle;

import static me.Azz_9.unbounded_bundle.Constants.MOD_ID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import me.Azz_9.unbounded_bundle.platform.Services;

public class Config {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_FILE = Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json");

	public boolean enabled = true;
	public int maxColumns = 6;
	public int minColumns = 4;
	public boolean scrollable = true;
	public boolean smoothScrolling = false;
	public int maxRows = 3;
	public boolean usePercentageProgress = false;

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
}
