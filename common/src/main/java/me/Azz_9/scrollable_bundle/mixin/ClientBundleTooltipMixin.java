package me.Azz_9.scrollable_bundle.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import me.Azz_9.scrollable_bundle.Config;

@Mixin(ClientBundleTooltip.class)
public abstract class ClientBundleTooltipMixin {

	@Shadow protected abstract void extractSlot(int slotNumber, int drawX, int drawY, List<ItemStackTemplate> shownItems, int slotIndex, Font font, GuiGraphicsExtractor graphics);

	@Shadow @Final private BundleContents contents;

	@Shadow protected abstract void extractSelectedItemTooltip(Font font, GuiGraphicsExtractor graphics, int x, int y, int w);

	@Shadow @Final private static int SLOT_SIZE;

	@Shadow private static void extractProgressbar(int x, int y, Font font, GuiGraphicsExtractor graphics, Fraction weight) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow @Final private static int PROGRESSBAR_HEIGHT;

	@Shadow @Final private static int PROGRESSBAR_MARGIN_Y;

	@Unique
	private int scrollable_bundle$bundleGridSizeX() {
		return Mth.clamp(this.contents.size(), Config.getMinColumns(), Config.getMaxColumns());
	}

	@Unique
	private int scrollable_bundle$bundleGridSizeY() {
		return Mth.positiveCeilDiv(this.contents.size(), scrollable_bundle$bundleGridSizeX());
	}

	@Unique
	private int scrollable_bundle$bundleVisibleRows() {
		if (!Config.isScrollable()) {
			return scrollable_bundle$bundleGridSizeY();
		}
		return Math.min(Config.getMaxRows(), scrollable_bundle$bundleGridSizeY());
	}

	@Unique
	private int scrollable_bundle$bundleGridWidth() {
		return scrollable_bundle$bundleGridSizeX() * SLOT_SIZE;
	}

	@Unique
	private int scrollable_bundle$bundleItemGridHeight() {
		return scrollable_bundle$bundleVisibleRows() * SLOT_SIZE;
	}

	@Unique
	private int scrollable_bundle$computeScrollOffset() {
		if (!Config.isScrollable()) return 0;

		int selectedIndex = this.contents.getSelectedItemIndex();
		if (selectedIndex < 0) return 0;

		int cols = scrollable_bundle$bundleGridSizeX();
		int visibleRows = scrollable_bundle$bundleVisibleRows();
		int totalRows = scrollable_bundle$bundleGridSizeY();
		int selectedRow = selectedIndex / cols;

		int offset = selectedRow - (visibleRows - 1) / 2;
		return Mth.clamp(offset, 0, Math.max(0, totalRows - visibleRows));
	}

	@Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
	private void getWidth(Font font, CallbackInfoReturnable<Integer> cir) {
		if (!this.contents.isEmpty()) {
			cir.setReturnValue(scrollable_bundle$bundleGridWidth());
		}
	}

	@Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
	private void getHeight(Font font, CallbackInfoReturnable<Integer> cir) {
		if (!this.contents.isEmpty()) {
			cir.setReturnValue(scrollable_bundle$bundleItemGridHeight() + PROGRESSBAR_HEIGHT + PROGRESSBAR_MARGIN_Y * 2);
		}
	}

	@Inject(method = "slotCount", at = @At("HEAD"), cancellable = true)
	private void slotCount(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.contents.size());
	}

	@Inject(method = "extractBundleWithItemsTooltip", at = @At("HEAD"), cancellable = true)
	private void extractBundleWithItemsTooltip(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics, Fraction weight, CallbackInfo ci) {
		int scrollOffset = scrollable_bundle$computeScrollOffset();

		List<ItemStackTemplate> shownItems = this.contents.items();
		int cols = scrollable_bundle$bundleGridSizeX();
		int visibleRows = scrollable_bundle$bundleVisibleRows();
		int gridWidth = scrollable_bundle$bundleGridWidth();

		int left = x + (w - gridWidth) / 2;

		for (int gridRow = scrollOffset; gridRow < scrollOffset + visibleRows; gridRow++) {
			int rowInWindow = gridRow - scrollOffset;
			int drawY = y + rowInWindow * SLOT_SIZE;

			for (int col = 0; col < cols; col++) {
				int itemIndex = gridRow * cols + col;
				if (itemIndex >= shownItems.size()) break;
				int drawX = left + col * SLOT_SIZE;
				int slotNumber = shownItems.size() - itemIndex;

				extractSlot(slotNumber, drawX, drawY, shownItems, slotNumber, font, graphics);
			}
		}

		this.extractSelectedItemTooltip(font, graphics, x, y, w);
		extractProgressbar(left, y + scrollable_bundle$bundleItemGridHeight() + PROGRESSBAR_MARGIN_Y, font, graphics, weight);
		ci.cancel();
	}
}