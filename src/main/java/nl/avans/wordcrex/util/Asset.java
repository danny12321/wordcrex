package nl.avans.wordcrex.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Asset {
    private static final Map<String, BufferedImage> ASSETS = new HashMap<>();

    public static BufferedImage read(String asset) {
        if (Asset.ASSETS.containsKey(asset)) {
            return Asset.ASSETS.get(asset);
        }

        try {
            var loaded = ImageIO.read(Asset.class.getResourceAsStream("/assets/" + asset + ".png"));

            Asset.ASSETS.put(asset, loaded);

            return loaded;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}