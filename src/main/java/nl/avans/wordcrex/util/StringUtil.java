package nl.avans.wordcrex.util;

import java.awt.*;

public class StringUtil {
    private static final String REGEX = "^[a-zA-Z0-9]{5,25}$";

    public static void drawCenteredString(Graphics2D g, int x, int y, int width, int height, String text) {
        var metrics = g.getFontMetrics();

        StringUtil.drawCenteredString(g, x, y + (height - metrics.getHeight()) / 2 + metrics.getAscent(), width, text);
    }

    public static void drawCenteredString(Graphics2D g, int x, int y, int width, String text) {
        var metrics = g.getFontMetrics();

        g.drawString(text, x + (width - metrics.stringWidth(text)) / 2, y);
    }

    public static boolean containsWhitespace(String str) {
        var length = str.length();

        for (var i = 0; i < length; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAuthInput(String input) {
        return input.matches(StringUtil.REGEX);
    }
}
