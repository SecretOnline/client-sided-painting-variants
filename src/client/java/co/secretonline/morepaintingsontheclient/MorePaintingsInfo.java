package co.secretonline.morepaintingsontheclient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;

/**
 * Data structure for paingings old and new.
 */
public class MorePaintingsInfo {
	private static Logger LOGGER = MorePaintingsOnTheClient.LOGGER;

	private Map<String, PaintingsForSize> paintings = new HashMap<>();

	private String toKey(int a, int b) {
		return a + "x" + b;
	}

	private PaintingsForSize getOrAddSize(int widthPx, int heightPx) {
		String key = toKey(widthPx, heightPx);

		var size = paintings.get(key);
		if (size == null) {
			size = new PaintingsForSize();
			paintings.put(key, size);
		}

		return size;
	}

	public boolean addRegisteredPainting(Identifier identifier, PaintingVariant painting) {
		var size = getOrAddSize(painting.getWidth(), painting.getHeight());

		size.registeredPaintings.put(identifier, painting);
		return true;
	}

	public boolean addAddedPainting(Identifier identifier, AddedPaintingVariant painting) {
		var size = getOrAddSize(painting.getWidth(), painting.getHeight());

		if (size.registeredPaintings.containsKey(identifier)) {
			LOGGER.warn("Painting {} has already been registered by the game. Skipping", identifier.toString());
			return false;
		}

		size.addedPaintings.put(identifier, painting);
		return true;
	}

	public PaintingsForSize getPaintingsForSize(int widthPx, int heightPx) {
		return paintings.get(toKey(widthPx, heightPx));
	}

	static public class PaintingsForSize {
		private Map<Identifier, PaintingVariant> registeredPaintings = new HashMap<>();
		private Map<Identifier, AddedPaintingVariant> addedPaintings = new HashMap<>();

		public List<PaintingVariant> getRegisteredPaintings() {
			return List.copyOf(registeredPaintings.values());
		}

		public List<AddedPaintingVariant> getAddedPaintings() {
			return List.copyOf(addedPaintings.values());
		}
	}

	static public class AddedPaintingVariant {
		private int width;
		private int height;
		private Identifier assetId;

		public AddedPaintingVariant(int width, int height, Identifier assetId) {
			this.width = width;
			this.height = height;
			this.assetId = assetId;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public Identifier getAssetId() {
			return assetId;
		}
	}
}
