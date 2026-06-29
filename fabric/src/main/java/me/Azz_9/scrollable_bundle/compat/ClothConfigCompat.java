package me.Azz_9.scrollable_bundle.compat;

import static me.shedaniel.clothconfig2.ClothConfigInitializer.MOD_ID;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import me.Azz_9.scrollable_bundle.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

public class ClothConfigCompat {

	public static Screen buildClothConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("scrollable_bundle.config.title"))
				.setSavingRunnable(Config::save);

		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("scrollable_bundle.config.category.general"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder
				.startIntSlider(
						Component.translatable("scrollable_bundle.config.max_columns"),
						Config.getMaxColumns(), 4, 8
				)
				.setDefaultValue(6)
				.setSaveConsumer(Config::setMaxColumns)
				.build()
		);

		general.addEntry(entryBuilder
				.startIntSlider(
						Component.translatable("scrollable_bundle.config.min_columns"),
						Config.getMinColumns(), 4, 8
				)
				.setDefaultValue(4)
				.setSaveConsumer(Config::setMinColumns)
				.build()
		);

		BooleanListEntry scrollableEntry = entryBuilder
				.startBooleanToggle(
						Component.translatable("scrollable_bundle.config.scrollable"),
						Config.isScrollable()
				)
				.setDefaultValue(true)
				.setSaveConsumer(Config::setScrollable)
				.build();

		general.addEntry(scrollableEntry);

		general.addEntry(entryBuilder
				.startIntSlider(
						Component.translatable("scrollable_bundle.config.max_rows"),
						Config.getMaxRows(), 3, 11
				)
				.setDefaultValue(3)
				.setSaveConsumer(Config::setMaxRows)
				.setRequirement(scrollableEntry::getValue)
				.build()
		);

		return builder.build();
	}
}
