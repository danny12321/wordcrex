package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.view.impl.LoginView;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.List;

public class FrameWidget extends Widget {
    private final Main main;
    private final SidebarWidget sidebar;
    private final ButtonWidget sidebarButton;

    public FrameWidget(Main main) {
        this.main = main;
        this.sidebar = new SidebarWidget(this.main);
        this.sidebarButton = new ButtonWidget("+", 0, 0, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, this.sidebar::toggle);
    }

    @Override
    public void draw(Graphics2D g) {
        this.sidebarButton.setEnabled(!this.main.isOpen(LoginView.class));

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(0, 0, Main.FRAME_SIZE, Main.TASKBAR_SIZE);
        g.setColor(Colors.DARK_BLUE);
        g.setFont(Fonts.BIG);
        g.drawString("WORDCREX", 39, 25);
        g.setFont(Fonts.NORMAL);
    }

    @Override
    public void update() {
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(
            this.sidebar,
            this.sidebarButton,
            new ButtonWidget("x", Main.FRAME_SIZE - Main.TASKBAR_SIZE, 0, Main.TASKBAR_SIZE, Main.TASKBAR_SIZE, Color.RED, Colors.DARK_RED, Color.WHITE, this.main::close)
        );
    }

    @Override
    public boolean blocking() {
        return this.sidebar.isOpen();
    }
}
