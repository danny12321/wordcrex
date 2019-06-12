package nl.avans.wordcrex.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        return input != null && input.matches(StringUtil.AUTH_REGEX);
    }

    public static boolean isWordInput(String input) {
        return input != null && input.matches(StringUtil.WORD_REGEX);
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

    public static String getPlaceholders(int amount) {
        var placeholders = new String[amount];

        Arrays.fill(placeholders, "?");

        return String.join(",", placeholders);
    }

    public static List<String> split(Graphics2D g, String str, int length) {
        var metrics = g.getFontMetrics();
        var lines = new ArrayList<String>();
        var builder = new StringBuilder();
        var words = str.split("\\s+");
        var last = "";

        for (var i = 0; i < words.length; i++) {
            builder.append(" ").append(words[i]);

            if (metrics.getStringBounds(builder.toString(), g).getWidth() > length) {
                if (metrics.getStringBounds(words[i], g).getWidth() > length) {
                    builder.setLength(0);
                    builder.append(last).append(" ");

                    for (var j = 1; j < words[i].length(); j++) {
                        builder.append(words[i], j - 1, j);

                        if (metrics.getStringBounds(builder.toString(), g).getWidth() > length) {
                            lines.add(last);
                            builder.setLength(0);
                        }

                        last = builder.toString();
                    }
                } else {
                    lines.add(last);
                    builder.setLength(0);

                    i--;
                }
            }

            last = builder.toString();
        }

        lines.add(last);

        return lines;
    }
}
