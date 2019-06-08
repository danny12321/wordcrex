package nl.avans.wordcrex.util;

import java.awt.*;

public class StringUtil {
    private static final String AUTH_REGEX = "^[a-zA-Z0-9]{5,25}$";
    private static final String WORD_REGEX = "^[a-z]{1,15}$";

    public static void drawCenteredString(Graphics2D g, int x, int y, int width, int height, String text) {
        var metrics = g.getFontMetrics();

        StringUtil.drawCenteredString(g, x, y + (height - metrics.getHeight()) / 2 + metrics.getAscent(), width, text);
    }

    public static void drawCenteredString(Graphics2D g, int x, int y, int width, String text) {
        var metrics = g.getFontMetrics();

        g.drawString(text, x + (width - metrics.stringWidth(text)) / 2, y);
    }

    public static boolean isAuthInput(String input) {
        return input.matches(StringUtil.AUTH_REGEX);
    }

    public static boolean isWordInput(String input) {
        return input.matches(StringUtil.WORD_REGEX);
    }

    public static String repeat(String str, int n) {
        if (str == null) {
            return null;
        }

        var builder = new StringBuilder();

        for (var i = 0; i < n; i++) {
            builder.append(str);
        }

        return builder.toString();
    }
}
