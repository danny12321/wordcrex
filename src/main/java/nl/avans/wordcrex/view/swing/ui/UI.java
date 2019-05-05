package nl.avans.wordcrex.view.swing.ui;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.GamePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class UI {
    private final List<UI> parents = new ArrayList<>();

    public abstract void initialize(GamePanel game, SwingController controller);

    public void cleanup() {
    }

    public abstract void draw(Graphics2D g);

    public void update() {
    }

    public int mouseMove(int x, int y) {
        return Cursor.DEFAULT_CURSOR;
    }

    public void mouseClick(int x, int y) {
    }

    public void mousePress(int x, int y) {
    }

    public int mouseRelease(int x, int y) {
        return Cursor.DEFAULT_CURSOR;
    }

    public int mouseDrag(int x, int y) {
        return Cursor.DEFAULT_CURSOR;
    }

    public void keyType(char character) {
    }

    public void keyPress(int code, int modifiers) {
    }

    public void keyRelease(int code, int modifiers) {
    }

    public List<UI> getChildren() {
        return null;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean forceTop() {
        return false;
    }

    public boolean isChild(UI ui) {
        return this.parents.contains(ui);
    }

    public boolean treeMatch(Predicate<UI> predicate) {
        return predicate.test(this) || this.parents.stream()
            .anyMatch(predicate);
    }

    public void addParent(UI parent) {
        this.parents.add(parent);
    }
}
