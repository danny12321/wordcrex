package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ChatController;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Assets;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;
import nl.avans.wordcrex.widget.impl.InputWidget;
import nl.avans.wordcrex.widget.impl.ScrollbarWidget;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ChatView extends View<ChatController> {
    private final ScrollbarWidget scrollbar = new ScrollbarWidget((scroll) -> this.scroll = scroll, true);
    private final InputWidget input = new InputWidget("BERICHT", 48, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 96, 48, this.controller::setMessage);
    private final ButtonWidget sendMessage = new ButtonWidget(Assets.read("next"), null, Main.FRAME_SIZE - Main.TASKBAR_SIZE - 40, Main.FRAME_SIZE - 40, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, this::sendMessage);

    private int scroll = 0;

    public ChatView(ChatController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        var gap = 16;
        var size = 32;

        var maxWidth = (Main.FRAME_SIZE - Main.TASKBAR_SIZE) / 2;
        var messages = this.controller.getMessages();
        var offset = Main.TASKBAR_SIZE + 16;
        var contentHeight = messages.stream().mapToInt(m -> StringUtil.split(g, m.message, maxWidth).size() * size + gap / 2).sum() - Main.FRAME_SIZE + Main.TASKBAR_SIZE + 64;

        for (var i = 0; i < messages.size(); i++) {
            var userMessage = false;
            var x = gap;

            if (messages.get(i).username.equals(this.controller.getUsername())) {
                userMessage = true;
                x = Main.FRAME_SIZE - Main.TASKBAR_SIZE - size - gap;
            }

            if (!(i != 0 && messages.get(i - 1).username.equals(messages.get(i).username))) {
                g.setColor(Colors.DARK_BLUE);
                if (userMessage) {
                    g.fillPolygon(new int[]{x - size + gap / 2, x - size + gap / 2 + 13, x - size + gap / 2}, new int[]{offset - contentHeight + this.scroll, offset - contentHeight + this.scroll, offset - contentHeight + this.scroll + 17}, 3);
                } else {
                    g.fillPolygon(new int[]{x + size + gap / 2 + gap, x + size + gap / 2 + gap - 13, x + size + gap / 2 + gap}, new int[]{offset - contentHeight + this.scroll, offset - contentHeight + this.scroll, offset - contentHeight + this.scroll + 17}, 3);
                }

                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(x, offset - contentHeight + this.scroll, size, size);
                g.setFont(Fonts.NORMAL);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, x, offset - contentHeight + this.scroll, size, size, messages.get(i).username.substring(0, 1).toUpperCase());
            }

            var message = messages.get(i).message;
            var bounds = g.getFontMetrics().getStringBounds(message, g);
            var width = (int) bounds.getWidth();
            var height = (int) bounds.getHeight();

            if (width > maxWidth) {
                width = maxWidth;
            }

            var stringX = userMessage ? x - width - gap * 2: x + size + gap * 2;
            var lines = StringUtil.split(g, message, maxWidth);

            for (var line : lines) {
                g.setColor(Colors.DARK_BLUE);
                g.fillRect(stringX - gap / 2, offset - contentHeight + this.scroll, width + gap, size);

                g.setColor(Color.WHITE);
                g.drawString(line.trim(), stringX, offset + height - contentHeight + this.scroll);

                offset += size;
            }

            //g.fillPolygon(new int[]{stringX - gap / 2 - 7, stringX - gap / 2, stringX - gap / 2}, new int[]{stringX - gap / 2 + 16, offset - contentHeight + this.scroll + 9, offset - contentHeight + this.scroll + 23}, 3);

            offset += gap / 2;
        }

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - Main.TASKBAR_SIZE, 48);

        this.scrollbar.setHeight(offset + gap / 2);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
        this.sendMessage.setEnabled(this.controller.canSend());
    }

    @Override
    public List<Widget> children() {
        return List.of(
            new ButtonWidget(Assets.read("back"), null, 8, Main.FRAME_SIZE - 40, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, this.controller::navigateGame),
            this.scrollbar,
            this.input,
            this.sendMessage
        );
    }

    private void sendMessage() {
        this.controller.sendMessage();
        this.input.clearInput();
    }
}
