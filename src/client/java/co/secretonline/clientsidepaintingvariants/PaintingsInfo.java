package co.secretonline.clientsidepaintingvariants;

import java.util.ArrayList;
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
		if (registryPaintings == null && resourcePaintings == null) {
			LOGGER.warn("Waiting for world data and resources to be loaded");
			return false;
		}
		if (registryPaintings != null && resourcePaintings == null) {
			LOGGER.warn("Waiting for resources to be loaded");
			return false;
		}
		if (registryPaintings == null && resourcePaintings != null) {
			LOGGER.warn("Waiting for world data to be loaded");
			return false;
		}

		resolvedPaintingsMap.clear();

		// Add all paintings in the registry
		registryPaintings.forEach((id, variant) -> {
			String key = toKey(variant.width(), variant.height());
			if (!resolvedPaintingsMap.containsKey(key)) {
				resolvedPaintingsMap.put(key, new PaintingsForSize());
			}

			PaintingsForSize forSize = resolvedPaintingsMap.get(key);
			forSize.registryPaintings.put(id, variant);
		});

		// Add all paintings defined in resource packs, unless they conflict with a
		// registry entry
		resourcePaintings.forEach((id, variant) -> {
			String key = toKey(variant.width(), variant.height());
			if (!resolvedPaintingsMap.containsKey(key)) {
				resolvedPaintingsMap.put(key, new PaintingsForSize());
			}

			PaintingsForSize forSize = resolvedPaintingsMap.get(key);
			if (forSize.registryPaintings.containsKey(id)) {
				LOGGER.warn("Resource painting " + id.toString() + " is already defined in the registry");
				return;
			}

			forSize.resourcePaintings.put(id, variant);
		});

		LOGGER.info(this.getSummaryString());

		return true;
	}

	public String getSummaryString() {
		int numPaintings = 0;
		var sizeSummaries = new ArrayList<String>(resolvedPaintingsMap.size());

		for (var entry : resolvedPaintingsMap.entrySet()) {
			var key = entry.getKey();
			var paintings = entry.getValue();

			numPaintings += paintings.registryPaintings.size() + paintings.resourcePaintings.size();
			sizeSummaries.add(new StringBuilder()
					.append(key)
					.append(" (")
					.append(paintings.registryPaintings.size())
					.append("+")
					.append(paintings.resourcePaintings.size())
					.append(")")
					.toString());
		}

		var sb = new StringBuilder()
				.append(numPaintings)
				.append(" paintings for ")
				.append(resolvedPaintingsMap.size())
				.append(" sizes: ")
				.append(String.join(", ", sizeSummaries));
		return sb.toString();
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
