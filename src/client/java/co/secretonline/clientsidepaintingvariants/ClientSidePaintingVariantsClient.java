package co.secretonline.clientsidepaintingvariants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class ClientSidePaintingVariantsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PaintingVariantsResourceListener listener = new PaintingVariantsResourceListener();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(listener);
	}
}
