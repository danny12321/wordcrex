package nl.avans.wordcrex.widget;

import nl.avans.wordcrex.particle.Particle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Widget {
    private final List<Widget> parents = new ArrayList<>();
    private boolean focus;
    private boolean request;

    public abstract void draw(Graphics2D g);

    public abstract void update(Consumer<Particle> addParticle);

    public void mouseClick(int x, int y) {
    }

    public void mousePress(int x, int y) {
    }

    public void mouseRelease(int x, int y) {
    }

    public void mouseDrag(int x, int y) {
    }

    public void mouseMove(int x, int y) {
    }

    public void keyType(char character) {
    }

    public void keyPress(int code, int modifiers) {
    }

    public List<Widget> children() {
        return List.of();
    }

    public boolean blocking() {
        return false;
    }

    public boolean top() {
        return false;
    }

    public boolean childOf(Widget widget) {
        return widget == this || this.parents.contains(widget);
    }

    public boolean treeMatch(Predicate<Widget> predicate) {
        return predicate.test(this) || this.parents.stream()
            .anyMatch(predicate);
    }

    public void addParent(Widget parent) {
        this.parents.add(parent);
    }

    public boolean focusable() {
        return false;
    }

    public boolean hasFocus() {
        return this.focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public boolean requestingFocus() {
        var requested = this.request;

        this.request = false;

        return requested;
    }

    public void requestFocus() {
        this.request = true;
    }
}
