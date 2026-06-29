package me.Azz_9.scrollable_bundle;

import static me.Azz_9.scrollable_bundle.Constants.LOG;
import static me.Azz_9.scrollable_bundle.Constants.MOD_ID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import me.Azz_9.scrollable_bundle.platform.Services;

public class Config {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_FILE = Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json");

	private int maxColumns = 6;
	private int minColumns = 4;
	private boolean scrollable = true;
	private int maxRows = 3;

	public static Config INSTANCE = new Config();

	public static void save() {
		LOG.info("Saving config...");

		try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
			GSON.toJson(INSTANCE, writer);
		} catch (IOException e) {
			LOG.error("Failed to save config file : {}", e.getMessage());
			return;
		}

		LOG.info("Config successfully saved!");
	}

	public static void load() {
		LOG.info("Loading config...");
		if (!Files.exists(CONFIG_FILE)) {
			LOG.info("Config file does not exist!");
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
			INSTANCE = GSON.fromJson(reader, Config.class);
		} catch (IOException e) {
			LOG.error("Failed to load config file : {}", e.getMessage());
			return;
		}

		LOG.info("Config successfully loaded!");
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

	public static int getMaxRows() {
		return INSTANCE.maxRows;
	}

	public static void setMaxRows(int maxRows) {
		INSTANCE.maxRows = maxRows;
	}
}
