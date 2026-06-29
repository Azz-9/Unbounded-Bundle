package me.Azz_9.unbounded_bundle.compat;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import me.Azz_9.unbounded_bundle.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;

public class ClothConfigCompat {

	public static Screen buildClothConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("unbounded_bundle.config.title"))
				.setSavingRunnable(Config::save);

		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("unbounded_bundle.config.category.general"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		BooleanListEntry enabledEntry = entryBuilder
				.startBooleanToggle(
						Component.translatable("unbounded_bundle.config.enabled"),
						Config.isEnabled()
				)
				.setDefaultValue(true)
				.setSaveConsumer(Config::setEnabled)
				.build();

		IntegerSliderEntry maxColumnsSlider = entryBuilder
				.startIntSlider(
						Component.translatable("unbounded_bundle.config.max_columns"),
						Config.getMaxColumns(), 4, 8
				)
				.setDefaultValue(6)
				.setSaveConsumer(Config::setMaxColumns)
				.setRequirement(enabledEntry::getValue)
				.build();

		IntegerSliderEntry minColumnsSlider = entryBuilder
				.startIntSlider(
						Component.translatable("unbounded_bundle.config.min_columns"),
						Config.getMinColumns(), 4, 8
				)
				.setDefaultValue(4)
				.setSaveConsumer(Config::setMinColumns)
				.setRequirement(enabledEntry::getValue)
				.build();

		BooleanListEntry scrollableEntry = entryBuilder
				.startBooleanToggle(
						Component.translatable("unbounded_bundle.config.scrollable"),
						Config.isScrollable()
				)
				.setDefaultValue(true)
				.setSaveConsumer(Config::setScrollable)
				.setRequirement(enabledEntry::getValue)
				.build();

		BooleanListEntry smoothScrollingEntry = entryBuilder
				.startBooleanToggle(
						Component.translatable("unbounded_bundle.config.smooth_scrolling"),
						Config.isSmoothScrolling()
				)
				.setDefaultValue(false)
				.setSaveConsumer(Config::setSmoothScrolling)
				.setRequirement(() -> scrollableEntry.getValue() && enabledEntry.getValue())
				.build();

		IntegerSliderEntry maxRowsSlider = entryBuilder
				.startIntSlider(
						Component.translatable("unbounded_bundle.config.max_rows"),
						Config.getMaxRows(), 3, 11
				)
				.setDefaultValue(3)
				.setSaveConsumer(Config::setMaxRows)
				.setRequirement(() -> scrollableEntry.getValue() && enabledEntry.getValue())
				.build();

		general.addEntry(enabledEntry);
		general.addEntry(maxColumnsSlider);
		general.addEntry(minColumnsSlider);
		general.addEntry(scrollableEntry);
		general.addEntry(smoothScrollingEntry);
		general.addEntry(maxRowsSlider);

		return builder.build();
	}
}
