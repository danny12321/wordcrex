package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.controller.impl.AccountController;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.List;

public class AccountView extends View<AccountController> {
    public AccountView(AccountController controller) {
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

        );
    }
}
