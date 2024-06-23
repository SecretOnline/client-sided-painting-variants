package co.secretonline.clientsidepaintingvariants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;

public class ClientSidePaintingVariantsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new PaintingVariantsResourceListener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new PaintingDataListener());

		FabricLoader.getInstance()
				.getModContainer(ClientSidePaintingVariants.MOD_ID)
				.ifPresent(container -> {
					ResourceManagerHelper.registerBuiltinResourcePack(
							ClientSidePaintingVariants.id("logo"),
							container,
							Text.of("Client Side Painting Variants - Logo"),
							ResourcePackActivationType.NORMAL);
				});
	}
}
