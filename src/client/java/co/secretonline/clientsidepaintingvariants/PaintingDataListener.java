package co.secretonline.clientsidepaintingvariants;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class PaintingDataListener implements IdentifiableResourceReloadListener {
	private static Logger LOGGER = ClientSidePaintingVariants.LOGGER;

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager resourceManager,
			Profiler propareProfiler, Profiler applyProfiler,
			Executor prepareExecutor, Executor applyExecutor) {

	}

	@Override
	public Identifier getFabricId() {
		return ClientSidePaintingVariants.id("data-listener");
	}
}
