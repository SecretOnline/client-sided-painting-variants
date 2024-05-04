package co.secretonline.morepaintingsontheclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class MorePaintingsResourceListener implements IdentifiableResourceReloadListener {

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager resourceManager,
			Profiler propareProfiler, Profiler applyProfiler,
			Executor prepareExecutor, Executor applyExecutor) {
		// Rough plan for this method:
		// In the prepare stage, read paintings from the registry and resource packs.
		// In the apply stage, do some stuff. This is likely to include stiching a new
		// texture atlas but since I don't actually now how this works I'm really just
		// guessing here.

		// 1.21 makes paintings data-driven, so we need to figure out how many paintings
		// Minecraft provides every time.
		// TODO: Confirm that this still works on 1.21 (I know, kinda important).
		// This also means that if a server provides a painting that is also provided by
		// the client, we can de-duplicate later.
		var paintingsFromRegistryFuture = CompletableFuture.supplyAsync(() -> {
			Map<String, Map<Identifier, PaintingVariant>> paintingSizes = new HashMap<>();

			Registries.PAINTING_VARIANT.forEach((painting) -> {
				var size = painting.getWidth() + "x" + painting.getHeight();

				if (!paintingSizes.containsKey(size)) {
					paintingSizes.put(size, new HashMap<>());
				}
				var paintingsForSize = paintingSizes.get(size);

				paintingsForSize.put(Registries.PAINTING_VARIANT.getId(painting), painting);
			});

			return paintingSizes;
		}, prepareExecutor);

		// When there are more steps to do in the propare stage, will need to combine
		// these somehow.
		// I kind of miss the flattenning that Javascript/Typescript does with Promises.
		// A Promise<Promise<T>> is flattened down to just Promise<T>. For now this
		// assignment will just live in peace, taking up space on the stack for no
		// reason.
		// var prepareStage = CompletableFuture.supplyAsync(() -> {
		// return getPaintingsFromRegistry;
		// }, prepareExecutor);
		var prepareStage = paintingsFromRegistryFuture;

		var afterSync = prepareStage.thenCompose(synchronizer::whenPrepared);

		var applyStage = afterSync.thenAcceptAsync((minecraftPaintings) -> {
			MorePaintingsOnTheClient.LOGGER.info("Yeah paintings! Woo!");

			minecraftPaintings.forEach((size, variants) -> {
				List<String> ids = new ArrayList<>();
				variants.forEach((id, variant) -> {
					ids.add(id.toString());
				});

				MorePaintingsOnTheClient.LOGGER.info(size + ": " + String.join(", ", ids));
			});
		}, applyExecutor);

		return applyStage;
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(MorePaintingsOnTheClient.MOD_ID, "resource-listener");
	}

}
