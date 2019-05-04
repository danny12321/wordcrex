package nl.avans.wordcrex;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.model.Model;
import nl.avans.wordcrex.view.swing.SwingView;

public class Main {
    public static void main(String[] args) {
        var database = Database.connect(args.length > 0 ? args[0] : "prod");
        var controller = new SwingController();
        var view = new SwingView();
        var model = new Model(database);

        view.initialize(controller);
        controller.initialize(view, model);
    }
}
