package co.secretonline.morepaintingsontheclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

public class MorePaintingsResourceListener implements IdentifiableResourceReloadListener {
	private static final int NUM_PIXELS_PER_BLOCK = 16;

	// I'd love a way to do this without having a static reference, but for now this
	// will do.
	@Nullable
	public static MorePaintingsInfo KNOWN_PAINTINGS = null;

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
			Map<Identifier, PaintingVariant> paintings = new HashMap<>();

			Registries.PAINTING_VARIANT.forEach((painting) -> {
				paintings.put(Registries.PAINTING_VARIANT.getId(painting), painting);
			});

			return paintings;
		}, prepareExecutor);

		var paintingsFromResourcesFuture = CompletableFuture.supplyAsync(() -> {
			Map<Identifier, PaintingVariant> paintings = new HashMap<>();

			var allVariantJsonFiles = resourceManager.findResources("painting_variant",
					identifier -> identifier.getPath().endsWith(".json"));

			allVariantJsonFiles.forEach((identifier, resource) -> {
				try (var reader = resource.getReader()) {
					var data = JsonHelper.deserialize(reader).getAsJsonObject();
					var width = data.get("width").getAsInt();
					var height = data.get("height").getAsInt();
					// TODO: Somehow get texture information through the process
					// var assetId = data.get("asset_id").getasString();

					paintings.put(identifier, new PaintingVariant(width * NUM_PIXELS_PER_BLOCK, height * NUM_PIXELS_PER_BLOCK));
				} catch (IOException ex) {
					MorePaintingsOnTheClient.LOGGER.info("Failed to read data for " + identifier.toString() + ". Skipping");
				}
			});

			return paintings;
		}, prepareExecutor);

		// Combine steps from prepare stage
		var prepareStage = paintingsFromRegistryFuture.thenCombine(paintingsFromResourcesFuture,
				(fromRegistry, fromResources) -> {
					var info = new MorePaintingsInfo();

					fromRegistry.forEach((identifier, variant) -> info.addRegisteredPainting(variant));
					fromResources.forEach((identifier, variant) -> info.addRegisteredPainting(variant));

					return info;
				});

		var afterSync = prepareStage.thenCompose(synchronizer::whenPrepared);

		var applyStage = afterSync.thenAcceptAsync((info) -> {
			MorePaintingsOnTheClient.LOGGER.info("Paintings added");

			KNOWN_PAINTINGS = info;
		}, applyExecutor);

		return applyStage;
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(MorePaintingsOnTheClient.MOD_ID, "resource-listener");
	}
}
