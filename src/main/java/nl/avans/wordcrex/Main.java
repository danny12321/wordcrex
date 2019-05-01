package nl.avans.wordcrex;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.model.Model;
import nl.avans.wordcrex.view.swing.SwingView;

public class Main {
    public static void main(String[] args) {
        var controller = new SwingController();
        var view = new SwingView();
        var model = new Model();

        view.initialize(controller);
        controller.initialize(view, model);
    }
}
