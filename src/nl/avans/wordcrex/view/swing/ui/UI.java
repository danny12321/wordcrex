package nl.avans.wordcrex.view.swing.ui;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.GamePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class UI {
    private final List<UI> parents = new ArrayList<>();

    public abstract void initialize(GamePanel game, SwingController controller);

    public abstract void draw(Graphics2D g);

    public void update() {
    }

    public int mouseMove(int x, int y) {
        return Cursor.DEFAULT_CURSOR;
    }

    public void mouseClick() {
    }

    public void mousePress(int x, int y) {
    }

    public void mouseRelease() {
    }

    public void mouseDrag(int x, int y) {
    }

    public void keyType(char character) {
    }

    public void keyPress(int code) {
    }

    public void keyRelease() {
    }

    public List<UI> getChildren() {
        return null;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isChild(UI ui) {
        return this.parents.contains(ui);
    }

    public void addParent(UI parent) {
        this.parents.add(parent);
    }
}
