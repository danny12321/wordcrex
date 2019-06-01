package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ChatController;
import nl.avans.wordcrex.model.Message;
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
    private int scroll = 0;

    private final int size = 32;
    private final int gap = 16;
    private final int maxBubbleSize = Main.FRAME_SIZE - Main.TASKBAR_SIZE - (gap * 4 + size * 2);

    public ChatView(ChatController controller) {
        super(controller);
    }

    private ArrayList<String> getLines(Graphics2D g, String[] splitMessage) {
        var lines = new ArrayList<String>();
        var sb = new StringBuilder();
        var lastString = "";

        for (var i = 0; i < splitMessage.length; i++) {
            sb.append(" ").append(splitMessage[i]);

            if (g.getFontMetrics().getStringBounds(sb.toString(), g).getWidth() > maxBubbleSize) {
                if (g.getFontMetrics().getStringBounds(splitMessage[i], g).getWidth() > maxBubbleSize) {
                    //God help me, don't read this or you'll die
                    sb = new StringBuilder(lastString);
                    sb.append(" ");

                    for (var j = 1; j < splitMessage[i].length(); j++) {
                        sb.append(splitMessage[i], j - 1, j);

                        if (g.getFontMetrics().getStringBounds(sb.toString(), g).getWidth() > maxBubbleSize) {
                            lines.add(lastString);
                            sb = new StringBuilder();
                        }

                        lastString = sb.toString();
                    }
                } else {
                    //when string width is higher than maxbubble size
                    lines.add(lastString);
                    sb = new StringBuilder();
                    i--;
                }
            }
            lastString = sb.toString();
        }

        lines.add(lastString);

        return lines;
    }

    @Override
    public void draw(Graphics2D g) {
        var messages = this.controller.getMessages();

        var y = 48;
        //x declared in loop

        for (var i = 0; i < messages.size(); i++) {
            var userMessage = false;
            var x = gap;

            if (messages.get(i).user.username.equals(this.controller.getUsername())) {
                userMessage = true;
                x = Main.FRAME_SIZE - Main.TASKBAR_SIZE - size - gap;
            }

            if (!(i != 0 && messages.get(i - 1).user.username.equals(messages.get(i).user.username))) {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(x, y - this.scroll, size, size);
                g.setFont(Fonts.NORMAL);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, x, y - this.scroll, size, size, messages.get(i).user.username.substring(0, 1).toUpperCase());
            }

            final var message = messages.get(i).message;
            var width = g.getFontMetrics().getStringBounds(message, g).getWidth(); //width of string
            final var height = g.getFontMetrics().getStringBounds(message, g).getHeight(); //height of string

            if (width > maxBubbleSize) width = maxBubbleSize;

            final var stringX = (int) (userMessage ? x - width - gap : x + size + gap); //start x of string

            var splitMessage = message.split("\\s+");
            var lines = this.getLines(g, splitMessage);

            for (var line : lines) {
                g.setColor(Colors.CHAT_BLUE);
                g.fillRect(stringX - gap / 2, y - this.scroll, (int) width + gap, size);

                g.setColor(Color.WHITE);
                g.drawString(line.trim(), stringX, y + (int) height - this.scroll);

                y += size;
            }

            y += gap;
        }

        this.scrollbar.setHeight(y + gap);
    }

    @Override
    public void update() {
    }

    @Override
    public java.util.List<Widget> getChildren() {
        return List.of(
            new ButtonWidget("<", 0, Main.FRAME_SIZE - 48, 48, 48, this.controller::returnToGame),
            new InputWidget("MESSAGE", 48, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 96, 48, (value) -> this.message = value),
            new ButtonWidget("+", Main.FRAME_SIZE - Main.TASKBAR_SIZE - 48, Main.FRAME_SIZE - 48, 48, 48, () -> {
                this.controller.sendChat(this.message);
                this.controller.reloadChatView();
            }),
            this.scrollbar
        );
    }
}
