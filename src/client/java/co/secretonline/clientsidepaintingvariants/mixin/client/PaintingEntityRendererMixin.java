package co.secretonline.clientsidepaintingvariants.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import co.secretonline.clientsidepaintingvariants.PaintingsInfo;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.state.PaintingEntityRenderState;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;

@Mixin(PaintingEntityRenderer.class)
public class PaintingEntityRendererMixin {
	@Inject(method = "updateRenderState", at = @At("RETURN"))
	private void injectUpdateRenderState(PaintingEntity paintingEntity,
			PaintingEntityRenderState paintingEntityRenderState, float f, CallbackInfo ci) {
		PaintingVariant paintingVariant = (PaintingVariant) paintingEntity.getVariant().value();

		PaintingsInfo paintingInfo = PaintingsInfo.getInstance();
		var registryPaintings = paintingInfo.getRegistryPaintingsForSize(paintingVariant.width(),
				paintingVariant.height());
		var resourcePaintings = paintingInfo.getResourcePaintingsForSize(paintingVariant.width(),
				paintingVariant.height());

		if (registryPaintings == null || resourcePaintings == null ||
				resourcePaintings.isEmpty()) {
			return;
		}
		int numRegistered = registryPaintings.size();
		int numAdded = resourcePaintings.size();
		int numTotal = numRegistered + numAdded;

		// Use the hash of the UUID as a stable random value for this entity.
		int hash = paintingEntity.getUuid().hashCode();

		// % can be negative, so add the total and % again for the proper modulo.
		int modulo = ((hash % (numTotal)) + numTotal) % numTotal;
		if (modulo < numRegistered) {
			return;
		}

		int index = modulo - numRegistered;
		var newVariant = resourcePaintings.get(index);
		if (newVariant == null) {
			// If this happens, then something has gone wrong.
			return;
		}

		paintingEntityRenderState.variant = newVariant;
	}
}
