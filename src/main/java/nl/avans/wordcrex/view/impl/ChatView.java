package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ChatController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatView extends View<ChatController> {
    private String message;

    private final int buttonHeight = 48;
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll, true);
    private final InputWidget input = new InputWidget("BERICHT", 48, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 96, this.buttonHeight, (value) -> this.message = value);
    private final ButtonWidget sendMessage = new ButtonWidget("+", Main.FRAME_SIZE - Main.TASKBAR_SIZE - 48, Main.FRAME_SIZE - 48, 48, this.buttonHeight, this::chat);
    private final int size = 32;
    private final int gap = 16;
    private final int maxBubbleSize = Main.FRAME_SIZE - Main.TASKBAR_SIZE - (this.gap * 4 + this.size * 2);

    private int scroll = 0;

    public ChatView(ChatController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var messages = this.controller.getMessages();
        var y = Main.TASKBAR_SIZE + this.gap;
        int contentHeight = messages.stream().mapToInt(m -> this.splitMessage(g, m.message.split("\\s+")).size() * this.size + this.gap).sum() - Main.FRAME_SIZE + Main.TASKBAR_SIZE + this.buttonHeight + this.gap;

        for (var i = 0; i < messages.size(); i++) {
            var userMessage = false;
            var x = this.gap;

            if (messages.get(i).username.equals(this.controller.getUsername())) {
                userMessage = true;
                x = Main.FRAME_SIZE - Main.TASKBAR_SIZE - this.size - this.gap;
            }

            if (!(i != 0 && messages.get(i - 1).username.equals(messages.get(i).username))) {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(x, y - contentHeight + this.scroll, this.size, this.size);
                g.setFont(Fonts.NORMAL);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, x, y - contentHeight + this.scroll, this.size, this.size, messages.get(i).username.substring(0, 1).toUpperCase());
            }

            var message = messages.get(i).message;
            var width = g.getFontMetrics().getStringBounds(message, g).getWidth();
            var height = g.getFontMetrics().getStringBounds(message, g).getHeight();

            if (width > this.maxBubbleSize) {
                width = this.maxBubbleSize;
            }

            var stringX = (int) (userMessage ? x - width - this.gap : x + this.size + this.gap);
            var splitMessage = message.split("\\s+");
            var lines = this.splitMessage(g, splitMessage);

            for (var line : lines) {
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(stringX - this.gap / 2, y - contentHeight + this.scroll, (int) width + this.gap, this.size);

                g.setColor(Color.WHITE);
                g.drawString(line.trim(), stringX, y + (int) height - contentHeight + this.scroll);

                y += this.size;
            }

            y += this.gap;
        }
        this.scrollbar.setHeight(y + this.gap);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        sendMessage.setEnabled(this.message.trim().length() > 0);
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            new ButtonWidget("<", 0, Main.FRAME_SIZE - 48, 48, 48, this.controller::navigateGame),
            this.input,
            this.sendMessage,
            this.scrollbar
        );
    }

    private void chat() {
        this.controller.sendChat(this.message);
        this.input.clearInput();
    }

    private List<String> splitMessage(Graphics2D g, String[] words) {
        var lines = new ArrayList<String>();
        var builder = new StringBuilder();
        var lastString = "";

        for (var i = 0; i < words.length; i++) {
            builder.append(" ").append(words[i]);

            if (g.getFontMetrics().getStringBounds(builder.toString(), g).getWidth() > this.maxBubbleSize) {
                if (g.getFontMetrics().getStringBounds(words[i], g).getWidth() > this.maxBubbleSize) {
                    builder = new StringBuilder(lastString);
                    builder.append(" ");

                    for (var j = 1; j < words[i].length(); j++) {
                        builder.append(words[i], j - 1, j);

                        if (g.getFontMetrics().getStringBounds(builder.toString(), g).getWidth() > this.maxBubbleSize) {
                            lines.add(lastString);
                            builder = new StringBuilder();
                        }

                        lastString = builder.toString();
                    }
                } else {
                    lines.add(lastString);
                    builder.setLength(0);
                    i--;
                }
            }

            lastString = builder.toString();
        }

        lines.add(lastString);

        return lines;
    }
}
