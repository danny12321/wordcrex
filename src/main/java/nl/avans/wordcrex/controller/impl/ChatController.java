package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Message;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ChatView;

import java.util.List;
import java.util.function.Function;

public class ChatController extends Controller<Game> {
    private String message;

    public ChatController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update(Game::poll);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new ChatView(this);
    }

    public void sendMessage() {
        this.getModel().sendMessage(this.getUsername(), this.message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean canSend() {
        return this.message.trim().length() > 0;
    }

    public String getUsername() {
        return this.getRoot().user.username;
    }

    public List<Message> getMessages() {
        return this.getModel().messages;
    }

    public void navigateGame() {
        this.main.openController(GameController.class, StreamUtil.getModelProperty((model) -> model.user.games, (game) -> game.id == this.getModel().id));
    }
}
