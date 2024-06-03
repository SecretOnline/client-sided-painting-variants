package co.secretonline.clientsidepaintingvariants.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import co.secretonline.clientsidepaintingvariants.PaintingVariantsResourceListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.util.Identifier;

@Mixin(PaintingEntityRenderer.class)
public class PaintingEntityRendererMixin {
	private static final int ARG_PAINTING_ENTITY = 2;
	private static final int ARG_PAINTING_SPRITE = 5;

	private Sprite getPaintingSprite(Identifier identifier) {
		var paintingManager = MinecraftClient.getInstance().getPaintingManager();

		return paintingManager.getSprite(identifier);
	}

	@ModifyArgs(method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"))
	private void switchRenderedPainting(Args args) {
		var knownPaintings = PaintingVariantsResourceListener.KNOWN_PAINTINGS;
		if (knownPaintings == null) {
			return;
		}

		var entity = (PaintingEntity) args.get(ARG_PAINTING_ENTITY);
		var paintingVariant = entity.getVariant().value();

		var paintingsForSize = knownPaintings.getPaintingsForSize(paintingVariant.width(), paintingVariant.height());
		if (paintingsForSize == null || paintingsForSize.getAddedPaintings().isEmpty()) {
			return;
		}
		int numRegistered = paintingsForSize.getRegisteredPaintings().size();
		int numAdded = paintingsForSize.getAddedPaintings().size();
		int numTotal = numRegistered + numAdded;

		// Use the hash of the UUID as a stable random value for this entity.
		int hash = entity.getUuid().hashCode();

		// % can be negative, so add the total and % again for the proper modulo.
		int modulo = ((hash % (numTotal)) + numTotal) % numTotal;
		if (modulo < numRegistered) {
			return;
		}

		int index = modulo - numRegistered;
		var newVariant = paintingsForSize.getAddedPaintings().get(index);
		if (newVariant == null) {
			// If this happens, then something has gone wrong.
			return;
		}

		// Yay!
		var newSprite = getPaintingSprite(newVariant.getAssetId());
		args.set(ARG_PAINTING_SPRITE, newSprite);
	}
}
