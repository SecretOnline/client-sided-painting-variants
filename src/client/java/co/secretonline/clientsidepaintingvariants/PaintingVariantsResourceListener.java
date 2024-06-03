package co.secretonline.clientsidepaintingvariants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import co.secretonline.clientsidepaintingvariants.PaintingVariantsInfo.AddedPaintingVariant;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

public class PaintingVariantsResourceListener implements IdentifiableResourceReloadListener {
	private static Logger LOGGER = ClientSidePaintingVariants.LOGGER;

	@Nullable
	public static PaintingVariantsInfo KNOWN_PAINTINGS = null;

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager resourceManager,
			Profiler propareProfiler, Profiler applyProfiler,
			Executor prepareExecutor, Executor applyExecutor) {
		// Rough plan for this method:
		// In the prepare stage, read paintings from the registry and resource packs.
		// In the apply stage, store statically for the mixin to pick up.

		var getRegistry = CompletableFuture.supplyAsync(
				() -> this.getPaintingsFromRegistry(),
				prepareExecutor);

		var getResources = CompletableFuture.supplyAsync(
				() -> this.getPaintingsFromResources(resourceManager),
				prepareExecutor);

		CompletableFuture<Void> future = getResources
				.thenCombine(
						getRegistry,
						(fromResources, fromRegistry) -> this.combineVariantInfo(fromRegistry, fromResources))
				.thenCompose(synchronizer::whenPrepared)
				.thenAcceptAsync((info) -> {
					LOGGER.info(info.getSummaryString());
					KNOWN_PAINTINGS = info;
				}, applyExecutor);

		return future;
	}

	private Map<Identifier, PaintingVariant> getPaintingsFromRegistry() {
		Map<Identifier, PaintingVariant> paintings = new HashMap<>();

		// 1.21 puts paintings in a data-driven registry, which makes this a little more
		// complicated.
		// I don't know whether this is the best way to go about this, but it works so
		// Im happy enough.
		var paintingRegistryWrapper = BuiltinRegistries.createWrapperLookup()
				.getWrapperOrThrow(RegistryKeys.PAINTING_VARIANT);

		paintingRegistryWrapper.streamEntries().forEach((painting) -> {
			paintings.put(painting.getKey().orElseThrow().getValue(), painting.value());
		});

		return paintings;
	}

	private Map<Identifier, AddedPaintingVariant> getPaintingsFromResources(ResourceManager resourceManager) {
		Map<Identifier, AddedPaintingVariant> paintings = new HashMap<>();

		// Load all files from resource packs that should contain painting variants.
		// Vanilla paintings shouldn't appear in this list, as this is reading from
		// resources and not data.
		var allVariantJsonFiles = resourceManager.findResources("painting_variant",
				identifier -> identifier.getPath().endsWith(".json"));

		allVariantJsonFiles.forEach((identifier, resource) -> {
			try (var reader = resource.getReader()) {
				var data = JsonHelper.deserialize(reader).getAsJsonObject();
				var width = data.get("width").getAsInt();
				var height = data.get("height").getAsInt();

				var assetId = Identifier.of(data.get("asset_id").getAsString());

				paintings.put(identifier, new AddedPaintingVariant(width, height, assetId));
			} catch (IOException ex) {
				LOGGER.warn("Failed to read data for " + identifier.toString() + ". Skipping");
			} catch (Exception ex) {
				LOGGER.warn("Error while reading painting variant " + identifier.toString() + ". Skipping");
			}
		});

		return paintings;
	}

	private PaintingVariantsInfo combineVariantInfo(Map<Identifier, PaintingVariant> registryPaintings,
			Map<Identifier, AddedPaintingVariant> addedPaintings) {
		var info = new PaintingVariantsInfo();

		registryPaintings.forEach((identifier, variant) -> info.addRegisteredPainting(identifier, variant));
		addedPaintings.forEach((identifier, variant) -> info.addAddedPainting(identifier, variant));

		return info;
	}

	@Override
	public Identifier getFabricId() {
		return ClientSidePaintingVariants.id("resource-listener");
	}
}
