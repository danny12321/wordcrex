package nl.avans.wordcrex.view.swing.util;

import java.awt.*;

public class StringUtil {
    public static void drawCenteredString(Graphics2D g, int x, int y, int width, int height, String text) {
        var metrics = g.getFontMetrics(g.getFont());

        StringUtil.drawCenteredString(g, x, y + (height - metrics.getHeight()) / 2 + metrics.getAscent(), width, text);
    }

    public static void drawCenteredString(Graphics2D g, int x, int y, int width, String text) {
        var metrics = g.getFontMetrics(g.getFont());

        g.drawString(text, x + (width - metrics.stringWidth(text)) / 2, y);
    }
}
