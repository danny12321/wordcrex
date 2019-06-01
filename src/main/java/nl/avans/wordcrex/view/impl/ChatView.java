package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ChatController;
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

public class ChatView extends View<ChatController> {
    private String message;

    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll);
    private final InputWidget input = new InputWidget("MESSAGE", 48, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 96, 48, (value) -> this.message = value);
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
        var y = 48;

        for (var i = 0; i < messages.size(); i++) {
            var userMessage = false;
            var x = this.gap;

            if (messages.get(i).user.username.equals(this.controller.getUsername())) {
                userMessage = true;
                x = Main.FRAME_SIZE - Main.TASKBAR_SIZE - this.size - this.gap;
            }

            if (!(i != 0 && messages.get(i - 1).user.username.equals(messages.get(i).user.username))) {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(x, y - this.scroll, this.size, this.size);
                g.setFont(Fonts.NORMAL);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, x, y - this.scroll, this.size, this.size, messages.get(i).user.getInitial());
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
                g.setColor(Colors.CHAT_BLUE);
                g.fillRect(stringX - this.gap / 2, y - this.scroll, (int) width + this.gap, this.size);

                g.setColor(Color.WHITE);
                g.drawString(line.trim(), stringX, y + (int) height - this.scroll);

                y += this.size;
            }

            y += this.gap;
        }

        this.scrollbar.setHeight(y + this.gap);
    }

    @Override
    public void update() {
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            new ButtonWidget("<", 0, Main.FRAME_SIZE - 48, 48, 48, this.controller::navigateGame),
            this.input,
            new ButtonWidget("+", Main.FRAME_SIZE - Main.TASKBAR_SIZE - 48, Main.FRAME_SIZE - 48, 48, 48, () -> {
                this.controller.sendChat(this.message);
                this.input.clearInput();
            }),
            this.scrollbar
        );
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
