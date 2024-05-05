package co.secretonline.morepaintingsontheclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;

/**
 * Data structure for paingings old and new.
 */
public class MorePaintingsInfo {
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

	public void addRegisteredPainting(PaintingVariant painting) {
		var size = getOrAddSize(painting.getWidth(), painting.getHeight());

		size.registeredPaintings.add(painting);
	}

	public void addAddedPainting(AddedPaintingVariant painting) {
		var size = getOrAddSize(painting.getWidth(), painting.getHeight());

		size.addedPaintings.add(painting);
	}

	public PaintingsForSize getPaintingsForSize(int widthPx, int heightPx) {
		return paintings.get(toKey(widthPx, heightPx));
	}

	static public class PaintingsForSize {
		private List<PaintingVariant> registeredPaintings = new ArrayList<>();
		private List<AddedPaintingVariant> addedPaintings = new ArrayList<>();

		public List<PaintingVariant> getRegisteredPaintings() {
			return registeredPaintings;
		}

		public List<AddedPaintingVariant> getAddedPaintings() {
			return addedPaintings;
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
