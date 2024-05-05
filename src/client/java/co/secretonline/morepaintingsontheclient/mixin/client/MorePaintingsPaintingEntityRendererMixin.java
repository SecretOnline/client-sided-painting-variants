package co.secretonline.morepaintingsontheclient.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import co.secretonline.morepaintingsontheclient.MorePaintingsResourceListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.util.Identifier;

@Mixin(PaintingEntityRenderer.class)
public class MorePaintingsPaintingEntityRendererMixin {
	private static final int ARG_PAINTING_ENTITY = 2;
	private static final int ARG_PAINTING_SPRITE = 5;

	private Sprite getPaintingSprite(Identifier identifier) {
		var paintingManager = MinecraftClient.getInstance().getPaintingManager();

		return paintingManager.getSprite(identifier);
	}

	@ModifyArgs(method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"))
	private void switchRenderedPainting(Args args) {
		var knownPaintings = MorePaintingsResourceListener.KNOWN_PAINTINGS;
		if (knownPaintings == null) {
			return;
		}

		var entity = (PaintingEntity) args.get(ARG_PAINTING_ENTITY);
		var paintingVariant = entity.getVariant().value();

		var paintingsForSize = knownPaintings.getPaintingsForSize(paintingVariant.getWidth(), paintingVariant.getHeight());
		if (paintingsForSize == null || paintingsForSize.getAddedPaintings().isEmpty()) {
			return;
		}
		int numRegistered = paintingsForSize.getRegisteredPaintings().size();
		int numAdded = paintingsForSize.getAddedPaintings().size();
		int numTotal = numRegistered + numAdded;

		// Use the hash of the UUID as a stable random value.
		// For whatever reason, the project is complaining about the UUID type.
		// I'm sure it's some tiny thing, but I simply don't have the energy to figure
		// it out so we'll just use the string and hash that instead.
		// This shouldn't change much of how the mod behaves.
		int hash = entity.getUuidAsString().hashCode();

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
