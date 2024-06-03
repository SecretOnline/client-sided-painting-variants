package co.secretonline.clientsidepaintingvariants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;

/**
 * Data structure for paingings old and new.
 */
public class PaintingVariantsInfo {
	private static Logger LOGGER = ClientSidePaintingVariants.LOGGER;

	private Map<String, PaintingsForSize> paintingSizes = new HashMap<>();

	private String toKey(int a, int b) {
		return a + "x" + b;
	}

	private PaintingsForSize getOrAddSize(int widthPx, int heightPx) {
		String key = toKey(widthPx, heightPx);

		var size = paintingSizes.get(key);
		if (size == null) {
			size = new PaintingsForSize();
			paintingSizes.put(key, size);
		}

		return size;
	}

	public boolean addRegisteredPainting(Identifier identifier, PaintingVariant painting) {
		var size = getOrAddSize(painting.width(), painting.height());

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
		return paintingSizes.get(toKey(widthPx, heightPx));
	}

	public String getSummaryString() {
		int numPaintings = 0;
		var sizeSummaries = new ArrayList<String>(paintingSizes.size());

		for (var entry : paintingSizes.entrySet()) {
			var key = entry.getKey();
			var paintings = entry.getValue();

			numPaintings += paintings.registeredPaintings.size() + paintings.addedPaintings.size();
			sizeSummaries.add(new StringBuilder()
					.append(key)
					.append(" (")
					.append(paintings.registeredPaintings.size())
					.append("+")
					.append(paintings.addedPaintings.size())
					.append(")")
					.toString());
		}

		var sb = new StringBuilder()
				.append(numPaintings)
				.append(" paintings for ")
				.append(paintingSizes.size())
				.append(" sizes: ")
				.append(String.join(", ", sizeSummaries));
		return sb.toString();
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
