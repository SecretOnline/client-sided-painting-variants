package co.secretonline.morepaintingsontheclient.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import co.secretonline.morepaintingsontheclient.MorePaintingsOnTheClient;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.Sprite;

@Mixin(PaintingEntityRenderer.class)
public class MorePaintingsPaintingEntityRendererMixin {
	boolean didLog = false;

	@ModifyArg(method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"), index = 5)
	private Sprite switchRenderedPainting(Sprite originalSprite) {
		if (!didLog) {
			didLog = true;
			MorePaintingsOnTheClient.LOGGER.info("rendering sprite from " + originalSprite.getAtlasId());
		}

		return originalSprite;
	}
}
