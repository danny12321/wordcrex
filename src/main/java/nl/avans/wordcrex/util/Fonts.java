package nl.avans.wordcrex.util;

import java.awt.*;
import java.io.IOException;

public class Fonts {
    public static final Font SMALL;
    public static final Font NORMAL;
    public static final Font BIG;

    static {
        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/fonts/RobotoMono.ttf"));

            SMALL = font.deriveFont(10.0f);
            NORMAL = font.deriveFont(16.0f);
            BIG = font.deriveFont(Font.BOLD, 24.0f);

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
