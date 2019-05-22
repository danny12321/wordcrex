package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.NewGameView;

import java.util.function.Function;

public class NewGameController extends Controller<User> {

    public NewGameController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new NewGameView(this);
    }

    public void search(String username) {
        System.out.println((username));
    }
}
