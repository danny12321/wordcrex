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

import java.awt.*;
import java.util.List;

public class ChatView extends View<ChatController> {
    private String message;

    public ChatView(ChatController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        List<Message> messages = this.controller.getMessages();

        int y = 48;
        //x declared in loop
        final int size = 32;
        final int gap = 16;
        final int maxBubbleSize = Main.FRAME_SIZE - (gap * 4 + size * 2);

        for(int i = 0; i < messages.size(); i++) {
            boolean userMessage = false;
            int x = gap;

            if(messages.get(i).user.username.equals(this.controller.getUsername())) {
                userMessage = true;
                x = Main.FRAME_SIZE - size - gap;
            }

            if(!(i != 0 && messages.get(i - 1).user.username.equals(messages.get(i).user.username))) {
                g.setColor(Colors.DARK_YELLOW);
                g.fillOval(x, y, size, size);
                g.setFont(Fonts.NORMAL);
                g.setColor(Colors.DARKER_BLUE);
                StringUtil.drawCenteredString(g, x, y, size, size, messages.get(i).user.username.substring(0, 1).toUpperCase());
            }

            final String message = messages.get(i).message;
            double width = g.getFontMetrics().getStringBounds(message, g).getWidth(); //width of string
            final double height = g.getFontMetrics().getStringBounds(message, g).getHeight(); //height of string

            if(width > maxBubbleSize) width = maxBubbleSize;

            final int stringX = (int) (userMessage ? x - width - gap : x + size + gap); //start x of string

            String[] splitString = message.split("/ +/g");

            g.setColor(Colors.DARK_BLUE);
            g.fillRect(stringX - gap / 2, y, (int) width + gap, size);

            g.setColor(Colors.DARK_YELLOW);
            g.drawString(messages.get(i).message, stringX, y + (int) height);

            y += size + gap;
        }


    }

    @Override
    public void update() {
    }

    @Override
    public java.util.List<Widget> getChildren() {
        return List.of(
            new ButtonWidget("<", 0, Main.FRAME_SIZE - 48, 48, 48, this.controller::returnToGame),
            new InputWidget("MESSAGE", 48, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - 96, 48, (value) -> this.message = value),
            new ButtonWidget("+", Main.FRAME_SIZE - 48, Main.FRAME_SIZE - 48, 48, 48, () -> this.controller.sendChat(this.message))
        );
    }
}
