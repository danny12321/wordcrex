package nl.avans.wordcrex.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {
    private static final Map<String, BufferedImage> ASSETS = new HashMap<>();

    public static BufferedImage read(String asset) {
        if (Assets.ASSETS.containsKey(asset)) {
            return Assets.ASSETS.get(asset);
        }

        try {
            var loaded = ImageIO.read(Assets.class.getResourceAsStream("/assets/" + asset + ".png"));

            Assets.ASSETS.put(asset, loaded);

            return loaded;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
