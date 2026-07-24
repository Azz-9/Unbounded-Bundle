package me.Azz_9.unbounded_bundle.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
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
import me.Azz_9.unbounded_bundle.client.BundleSmoothScroll;

@Mixin(ClientBundleTooltip.class)
public abstract class ClientBundleTooltipMixin {

	@Shadow
	protected abstract void renderSlot(int slotNumber, int drawX, int drawY, List<ItemStack> shownItems, int slotIndex, Font font, GuiGraphics graphics);

	@Shadow @Final private BundleContents contents;

	@Shadow
	protected abstract void drawSelectedItemTooltip(Font font, GuiGraphics graphics, int x, int y, int w);

	@Shadow @Final private static int SLOT_SIZE;

	@Shadow @Final private static int PROGRESSBAR_HEIGHT;

	@Shadow @Final private static int PROGRESSBAR_MARGIN_Y;

	@Shadow @Final private static Identifier PROGRESSBAR_BORDER_SPRITE;

	@Shadow
	protected abstract Identifier getProgressBarTexture();

	@Shadow
	@Nullable
	protected abstract Component getProgressBarFillText();

	@Unique
	private static int unbounded_bundle$lastSize = -1;

	@Unique
	private int unbounded_bundle$bundleGridSizeX() {
		return Mth.clamp(this.contents.size(), Config.INSTANCE.minColumns, Config.INSTANCE.maxColumns);
	}

	@Unique
	private int unbounded_bundle$bundleGridSizeY() {
		return Mth.positiveCeilDiv(this.contents.size(), unbounded_bundle$bundleGridSizeX());
	}

	@Unique
	private int unbounded_bundle$bundleVisibleRows() {
		if (!Config.INSTANCE.scrollable) {
			return unbounded_bundle$bundleGridSizeY();
		}
		return Math.min(Config.INSTANCE.maxRows, unbounded_bundle$bundleGridSizeY());
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
		if (!Config.INSTANCE.scrollable) return 0;

		int selectedIndex = this.contents.getSelectedItem();
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
		if (Config.INSTANCE.enabled && !this.contents.isEmpty()) {
			cir.setReturnValue(unbounded_bundle$bundleGridWidth());
		}
	}

	@Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
	private void getHeight(Font font, CallbackInfoReturnable<Integer> cir) {
		if (Config.INSTANCE.enabled && !this.contents.isEmpty()) {
			cir.setReturnValue(unbounded_bundle$bundleItemGridHeight() + PROGRESSBAR_HEIGHT + PROGRESSBAR_MARGIN_Y * 2);
		}
	}

	@Inject(method = "slotCount", at = @At("HEAD"), cancellable = true)
	private void slotCount(CallbackInfoReturnable<Integer> cir) {
		if (Config.INSTANCE.enabled) {
			cir.setReturnValue(this.contents.size());
		}
	}

	@Inject(method = "renderBundleWithItemsTooltip", at = @At("HEAD"), cancellable = true)
	private void renderBundleWithItemsTooltip(Font font, int x, int y, int width, int height, GuiGraphics graphics, CallbackInfo ci) {
		if (!Config.INSTANCE.enabled) return;

		int targetOffset = unbounded_bundle$computeScrollOffset();
		int currentSize = this.contents.size();

		if (currentSize != unbounded_bundle$lastSize) {
			BundleSmoothScroll.reset(targetOffset);
			unbounded_bundle$lastSize = currentSize;
		} else if (Config.INSTANCE.smoothScrolling) {
			BundleSmoothScroll.update(targetOffset);
		} else {
			BundleSmoothScroll.reset(targetOffset);
		}

		float smoothOffset = Config.INSTANCE.smoothScrolling
				? BundleSmoothScroll.getSmoothOffset()
				: targetOffset;

		List<ItemStack> shownItems = this.contents.itemCopyStream().toList();
		int cols = unbounded_bundle$bundleGridSizeX();
		int gridWidth = unbounded_bundle$bundleGridWidth();
		int left = x + (width - gridWidth) / 2;

		// Hauteur totale de la zone visible en pixels
		int visibleHeightPx = unbounded_bundle$bundleItemGridHeight();

		// On active le scissor pour clipper les items qui débordent
		graphics.enableScissor(left, y, left + gridWidth, y + visibleHeightPx);

		for (int gridRow = 0; gridRow < unbounded_bundle$bundleGridSizeY(); gridRow++) {
			// Position Y avec le décalage smooth en float
			float drawYf = y + (gridRow - smoothOffset) * SLOT_SIZE;
			int drawY = Math.round(drawYf);

			// Skip les lignes complètement hors de la fenêtre visible
			if (drawY + SLOT_SIZE <= y || drawY >= y + visibleHeightPx) continue;

			for (int col = 0; col < cols; col++) {
				int itemIndex = gridRow * cols + col;
				if (itemIndex >= shownItems.size()) break;

				int drawX = left + col * SLOT_SIZE;
				int slotNumber = shownItems.size() - itemIndex;
				renderSlot(slotNumber, drawX, drawY, shownItems, slotNumber, font, graphics);
			}
		}

		graphics.disableScissor();

		this.drawSelectedItemTooltip(font, graphics, x, y, width);

		int progressY = y + visibleHeightPx + PROGRESSBAR_MARGIN_Y;
		int fillMax = gridWidth - 2;
		int fill = Mth.clamp(Mth.mulAndTruncate(this.contents.weight(), fillMax), 0, fillMax);

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, getProgressBarTexture(), left + 1, progressY, fill, PROGRESSBAR_HEIGHT);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESSBAR_BORDER_SPRITE, left, progressY, gridWidth, PROGRESSBAR_HEIGHT);

		Component text = getProgressBarFillText();
		if (text != null) {
			graphics.drawCenteredString(font, text, left + gridWidth / 2, progressY + 3, -1);
		}

		ci.cancel();
	}

	@ModifyReturnValue(method = "getProgressBarFillText", at = @At("RETURN"))
	private Component getProgressBarFillText(Component original) {
		// not filled and not empty
		if (this.contents.weight().compareTo(Fraction.ZERO) != 0 && this.contents.weight().compareTo(Fraction.ONE) != 0) {
			if (Config.INSTANCE.usePercentageProgress) {
				return Component.literal(Math.round(this.contents.weight().floatValue() * 100) + "%");
			} else {
				return Component.literal((int) (this.contents.weight().doubleValue() * 64) + "/64");
			}
		}
		return original;
	}
}