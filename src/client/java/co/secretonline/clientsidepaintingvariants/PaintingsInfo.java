package co.secretonline.clientsidepaintingvariants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;

public class PaintingsInfo {
	private static Logger LOGGER = ClientSidePaintingVariants.LOGGER;

	private static PaintingsInfo INSTANCE = null;

	public static PaintingsInfo getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PaintingsInfo();
		}

		return INSTANCE;
	}

	private static String toKey(int a, int b) {
		return a + "x" + b;
	}

	private Map<Identifier, PaintingVariant> registryPaintings;
	private Map<Identifier, PaintingVariant> resourcePaintings;

	private Map<String, PaintingsForSize> resolvedPaintingsMap = new HashMap<>();

	private PaintingsInfo() {
	}

	public void setRegistryPaintings(Map<Identifier, PaintingVariant> paintings) {
		registryPaintings = paintings;
		this.resolvePaintings();
	}

	public void setResourcePaintings(Map<Identifier, PaintingVariant> paintings) {
		resourcePaintings = paintings;
		this.resolvePaintings();
	}

	private boolean resolvePaintings() {
		if (registryPaintings == null || resourcePaintings == null) {
			LOGGER.warn("Waiting for all variants");
			return false;
		}

		// TODO:

		return true;
	}

	@Nullable
	public List<PaintingVariant> getRegistryPaintingsForSize(int width, int height) {
		PaintingsForSize forSize = resolvedPaintingsMap.get(toKey(width, height));
		if (forSize == null) {
			return null;
		}

		return forSize.getRegistryPaintings();
	}

	@Nullable
	public List<PaintingVariant> getResourcePaintingsForSize(int width, int height) {
		PaintingsForSize forSize = resolvedPaintingsMap.get(toKey(width, height));
		if (forSize == null) {
			return null;
		}

		return forSize.getResourcePaintings();
	}

	static public class PaintingsForSize {
		private Map<Identifier, PaintingVariant> registryPaintings = new HashMap<>();
		private Map<Identifier, PaintingVariant> resourcePaintings = new HashMap<>();

		public List<PaintingVariant> getRegistryPaintings() {
			return List.copyOf(registryPaintings.values());
		}

		public List<PaintingVariant> getResourcePaintings() {
			return List.copyOf(resourcePaintings.values());
		}
	}
}
