package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Message;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ChatView;

import java.util.List;
import java.util.function.Function;

public class ChatController extends Controller<Game> {
    public ChatController(Main main, Function<User, Game> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new ChatView(this);
    }

    public void sendChat(String message) {
        this.getModel().sendChatMessage(message);
    }

    public String getUsername() {
        return this.getModel().isHostAuthenticated() ? this.getModel().host.username : this.getModel().opponent.username;
    }

    public List<Message> getMessages() {
        return this.getModel().messages;
    }

    public void navigateGame() {
        this.main.openController(GameController.class, StreamUtil.getModelProperty((user) -> user.games, (game) -> game.id == this.getModel().id));
    }
}
