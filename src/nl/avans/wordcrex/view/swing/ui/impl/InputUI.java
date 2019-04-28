package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class InputUI extends UI {
    private final StringBuilder input = new StringBuilder();

    private final String label;
    private final Character placeholder;
    private int x;
    private int y;
    private final int width;
    private final int height;

    private boolean hover;
    private boolean active;
    private int cursor;
    private int update;
    private int offset;
    private int character;

    public InputUI(int x, int y, int width, int height) {
        this("", x, y, width, height);
    }

    public InputUI(String label, int x, int y, int width, int height) {
        this(label, null, x, y, width, height);
    }

    public InputUI(String label, Character placeholder, int x, int y, int width, int height) {
        this.label = label;
        this.placeholder = placeholder;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void initialize(GamePanel game, SwingController controller) {
    }

    @Override
    public void draw(Graphics2D g) {
        var metrics = g.getFontMetrics(g.getFont());
        var rect = new Rectangle2D.Float(this.x, this.y, this.width, this.height);
        var text = this.placeholder == null ? this.input.toString() : String.valueOf(this.placeholder).repeat(this.input.length());
        var position = metrics.stringWidth(this.input.substring(0, this.cursor)) - 1;
        var line = (this.height - metrics.getHeight()) / 2 + metrics.getAscent();

        if (position - this.offset >= this.width - 16) {
            this.offset = position - this.width + 16;
        } else if (position - this.offset <= 16) {
            this.offset = position - 16;
        }

        this.character = metrics.charWidth(' ');

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(this.x, this.y, this.width, this.height);

        if (this.input.length() == 0) {
            g.setColor(Colors.DARKERER_BLUE);
            g.drawString(this.label, this.x + 16, this.y + line);
        }

        g.setClip(rect);
        g.setColor(Color.WHITE);
        g.drawString(text, this.x - this.offset, this.y + line);

        if (this.active && this.update % 30 <= 15) {
            g.fillRect(this.x + position - this.offset, this.y + this.height / 2 - 12, 2, 24);
        }

        g.setClip(null);
    }

    @Override
    public void update() {
        this.update++;
    }

    @Override
    public int mouseMove(int x, int y) {
        this.hover = x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;

        return this.hover ? Cursor.TEXT_CURSOR : Cursor.DEFAULT_CURSOR;
    }

    @Override
    public void mousePress(int x, int y) {
        if (!this.active && this.hover) {
            this.update = 0;
        }

        this.active = this.hover;

        if (this.active && this.character != 0) {
            this.cursor = Math.max(0, Math.min(this.input.length(), Math.round((x - this.x + this.offset) / (float) this.character)));
        }
    }

    @Override
    public void keyType(char character) {
        if (!this.active) {
            return;
        }

        if (this.isPrintableChar(character)) {
            this.input.insert(this.cursor, character);
            this.cursor++;
            this.update = 0;
        }
    }

    @Override
    public void keyPress(int code, int modifiers) {
        if (!this.active) {
            return;
        }

        if (code == KeyEvent.VK_BACK_SPACE && this.cursor > 0) {
            this.input.deleteCharAt(this.cursor - 1);
            this.cursor--;
            this.update = 0;
        } else if (code == KeyEvent.VK_DELETE && this.cursor < this.input.length()) {
            this.input.deleteCharAt(this.cursor);
            this.update = 0;
        } else if (code == KeyEvent.VK_LEFT) {
            this.cursor = Math.max(0, this.cursor - 1);
            this.update = 0;
        } else if (code == KeyEvent.VK_RIGHT) {
            this.cursor = Math.min(this.input.length(), this.cursor + 1);
            this.update = 0;
        } else if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0 && code == KeyEvent.VK_V) {
            var clipboard = this.getClipboard();

            this.input.insert(this.cursor, clipboard);
            this.cursor += clipboard.length();
            this.update = 0;
        }
    }

    private boolean isPrintableChar(char c) {
        var block = Character.UnicodeBlock.of(c);

        return !Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED && block != null && block != Character.UnicodeBlock.SPECIALS;
    }

    public String getClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
