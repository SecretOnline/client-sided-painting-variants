package co.secretonline.clientsidepaintingvariants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class PaintingVariantsDataListener implements IdentifiableResourceReloadListener {
	private static Logger LOGGER = ClientSidePaintingVariants.LOGGER;

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager resourceManager,
			Profiler propareProfiler, Profiler applyProfiler,
			Executor prepareExecutor, Executor applyExecutor) {
		return CompletableFuture.supplyAsync(
				() -> this.getPaintingsFromRegistry(),
				prepareExecutor)
				.thenCompose(synchronizer::whenPrepared)
				.thenAcceptAsync(
						(info) -> {
							PaintingsInfo.getInstance().setRegistryPaintings(info);
						},
						applyExecutor);
	}

	private Map<Identifier, PaintingVariant> getPaintingsFromRegistry() {
		Map<Identifier, PaintingVariant> paintings = new HashMap<>();

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) {
			LOGGER.warn("No world to load painting variants registry from");
			return paintings;
		}

		client.world
				.getRegistryManager()
				.get(RegistryKeys.PAINTING_VARIANT)
				.streamEntries()
				.forEach((entry) -> {
					paintings.put(entry.registryKey().getValue(), entry.value());
				});

		return paintings;
	}

	@Override
	public Identifier getFabricId() {
		return ClientSidePaintingVariants.id("data-listener");
	}
}
