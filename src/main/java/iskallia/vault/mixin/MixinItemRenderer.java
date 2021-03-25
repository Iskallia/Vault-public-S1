package iskallia.vault.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.init.ModAttributes;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow protected abstract void fillRect(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

	@Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;showDurabilityBar(Lnet/minecraft/item/ItemStack;)Z"))
	private void render(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
		if(!ModAttributes.GEAR_MAX_LEVEL.exists(stack)) {
			return;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		float progress = (float)ModAttributes.GEAR_MAX_LEVEL.getOrDefault(stack, 1).getValue(stack);
		progress = (progress - ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack)) / progress;
		progress = MathHelper.clamp(progress, 0.0F, 1.0F);

		if(progress != 0.0F && progress != 1.0F) {
			int i = Math.round(13.0F - progress * 13.0F);

			int j = MathHelper.hsvToRgb(Math.max(0.0F, 1.0F - progress) / 3.0F, 1.0F, 1.0F);
			//this.draw(bufferbuilder, xPosition + 2, yPosition + 15, 13, 2, 0, 0, 0, 255);
			//this.draw(bufferbuilder, xPosition + 2, yPosition + 15, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
		}

		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
	}

}
