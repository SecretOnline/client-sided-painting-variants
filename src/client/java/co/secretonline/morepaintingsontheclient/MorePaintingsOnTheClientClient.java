package co.secretonline.morepaintingsontheclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class MorePaintingsOnTheClientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MorePaintingsResourceListener listener = new MorePaintingsResourceListener();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(listener);
	}
}
