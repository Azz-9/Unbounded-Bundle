package me.Azz_9.unbounded_bundle.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;

import org.apache.commons.lang3.math.Fraction;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import me.Azz_9.unbounded_bundle.Config;

@Mixin(ClientBundleTooltip.class)
public abstract class ClientBundleTooltipMixin {

	@Shadow protected abstract void extractSlot(int slotNumber, int drawX, int drawY, List<ItemStackTemplate> shownItems, int slotIndex, Font font, GuiGraphicsExtractor graphics);

	@Shadow @Final private BundleContents contents;

	@Shadow protected abstract void extractSelectedItemTooltip(Font font, GuiGraphicsExtractor graphics, int x, int y, int w);

	@Shadow @Final private static int SLOT_SIZE;

	@Shadow @Final private static int PROGRESSBAR_HEIGHT;

	@Shadow @Final private static int PROGRESSBAR_MARGIN_Y;

	@Shadow private static @Nullable Component getProgressBarFillText(Fraction weight) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow @Final private static Identifier PROGRESSBAR_BORDER_SPRITE;

	@Shadow private static Identifier getProgressBarTexture(Fraction weight) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique
	private int unbounded_bundle$bundleGridSizeX() {
		return Mth.clamp(this.contents.size(), Config.getMinColumns(), Config.getMaxColumns());
	}

	@Unique
	private int unbounded_bundle$bundleGridSizeY() {
		return Mth.positiveCeilDiv(this.contents.size(), unbounded_bundle$bundleGridSizeX());
	}

	@Unique
	private int unbounded_bundle$bundleVisibleRows() {
		if (!Config.isScrollable()) {
			return unbounded_bundle$bundleGridSizeY();
		}
		return Math.min(Config.getMaxRows(), unbounded_bundle$bundleGridSizeY());
	}

	@Unique
	private int unbounded_bundle$bundleGridWidth() {
		return unbounded_bundle$bundleGridSizeX() * SLOT_SIZE;
	}

	@Unique
	private int unbounded_bundle$bundleItemGridHeight() {
		return unbounded_bundle$bundleVisibleRows() * SLOT_SIZE;
	}

	@Unique
	private int unbounded_bundle$computeScrollOffset() {
		if (!Config.isScrollable()) return 0;

		int selectedIndex = this.contents.getSelectedItemIndex();
		if (selectedIndex < 0) return 0;

		int cols = unbounded_bundle$bundleGridSizeX();
		int visibleRows = unbounded_bundle$bundleVisibleRows();
		int totalRows = unbounded_bundle$bundleGridSizeY();
		int selectedRow = selectedIndex / cols;

		int offset = selectedRow - (visibleRows - 1) / 2;
		return Mth.clamp(offset, 0, Math.max(0, totalRows - visibleRows));
	}

	@Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
	private void getWidth(Font font, CallbackInfoReturnable<Integer> cir) {
		if (Config.isEnabled() && !this.contents.isEmpty()) {
			cir.setReturnValue(unbounded_bundle$bundleGridWidth());
		}
	}

	@Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
	private void getHeight(Font font, CallbackInfoReturnable<Integer> cir) {
		if (Config.isEnabled() && !this.contents.isEmpty()) {
			cir.setReturnValue(unbounded_bundle$bundleItemGridHeight() + PROGRESSBAR_HEIGHT + PROGRESSBAR_MARGIN_Y * 2);
		}
	}

	@Inject(method = "slotCount", at = @At("HEAD"), cancellable = true)
	private void slotCount(CallbackInfoReturnable<Integer> cir) {
		if (Config.isEnabled()) {
			cir.setReturnValue(this.contents.size());
		}
	}

	@Inject(method = "extractBundleWithItemsTooltip", at = @At("HEAD"), cancellable = true)
	private void extractBundleWithItemsTooltip(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics, Fraction weight, CallbackInfo ci) {
		if (!Config.isEnabled()) return;
		int scrollOffset = unbounded_bundle$computeScrollOffset();

		List<ItemStackTemplate> shownItems = this.contents.items();
		int cols = unbounded_bundle$bundleGridSizeX();
		int visibleRows = unbounded_bundle$bundleVisibleRows();
		int gridWidth = unbounded_bundle$bundleGridWidth();

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

		int progressY = y + unbounded_bundle$bundleItemGridHeight() + 4;
		int width = unbounded_bundle$bundleGridWidth();
		int fillMax = width - 2;
		int fill = Mth.clamp(Mth.mulAndTruncate(weight, fillMax), 0, fillMax);

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, getProgressBarTexture(weight), left + 1, progressY, fill, 13);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESSBAR_BORDER_SPRITE, left, progressY, width, 13);

		Component text = getProgressBarFillText(weight);
		if (text != null) {
			graphics.centeredText(font, text, left + width / 2, progressY + 3, -1);
		}

		ci.cancel();
	}
}