package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.impl.ChatController;
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

    }

    @Override
    public void update() {
    }

    @Override
    public java.util.List<Widget> getChildren() {
        return List.of(
            new InputWidget("MESSAGE", 0, Main.FRAME_SIZE - 48, Main.FRAME_SIZE - 48, 48, (value) -> this.message = value),
            new ButtonWidget("+", Main.FRAME_SIZE - 48, Main.FRAME_SIZE - 48, 48, 48, () -> this.controller.sendChat(this.message))
        );
    }
}
