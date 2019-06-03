package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.GameState;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ObserveView;

import java.util.List;
import java.util.function.Function;

public class ObserveController extends Controller<User> {
    public ObserveController(Main main, Function<User, User> fn) {
        super(main, fn);
    }
    private String search = "";

    @Override
    public View<? extends Controller<User>> createView() {
        return new ObserveView(this);
    }


    public boolean isSelectable(Game game) {
        return game.state == GameState.PLAYING || (game.state == GameState.FINISHED );
    }

    public String getLabel(Game game) {
        if (game.state == GameState.PLAYING) {
            return "BEZIG";
        } else {
            return "AFGELOPEN";
        }
    }


    public List<Game> getGames() {
        return this.getModel().observeGames(search);
    }

    public void setSearch(String value){
        search = value;
    }

    public void navigateGame(int id) {
        this.main.openController(GameController.class, StreamUtil.getModelProperty((user) -> user.games, (game) -> game.id == id));
    }


}
