package nl.avans.wordcrex;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.controller.impl.LoginController;
import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.Loop;
import nl.avans.wordcrex.util.Pollable;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.FrameWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main extends JPanel {
    public static final int FRAME_SIZE = 512;
    public static final int TASKBAR_SIZE = 32;

    private static final RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    private final JFrame frame;
    private final Database database;
    private final List<Widget> widgets;
    private final Loop loop;

    private Controller<?> controller;
    private User model;

    private Main(JFrame frame, String config) {
        this.frame = frame;
        this.database = new Database(config);
        this.widgets = new ArrayList<>();
        this.loop = new Loop(Map.of(
            4.0d, this::poll,
            30.0d, this::update,
            60.0d, this::repaint
        ));
        this.model = new User(this.database);

        var listener = new Listener(this.frame, this);

        this.setFont(Fonts.NORMAL);
        this.setForeground(Color.WHITE);
        this.setBackground(Colors.DARKER_BLUE);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.setDoubleBuffered(true);
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
        this.addKeyListener(listener);
        this.openController(LoginController.class);
        this.start();
    }

    private void start() {
        this.loop.start();
    }

    public void close() {
        this.loop.stop();
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }

    public void openController(Class<? extends Controller<User>> cls) {
        this.openController(cls, Function.identity());
    }

    public <T extends Pollable<T>> void openController(Class<? extends Controller<T>> cls, Function<User, T> fn) {
        this.controller = this.createController(cls, fn);
        this.openView(this.controller.createView());
    }

    private void openView(View<?> view) {
        this.widgets.clear();

        this.addWidget(view, new ArrayList<>());
        this.addWidget(new FrameWidget(this), new ArrayList<>());
    }

    private <T extends Pollable<T>> Controller<T> createController(Class<? extends Controller<T>> cls, Function<User, T> fn) {
        try {
            return cls.getConstructor(Main.class, Function.class).newInstance(this, fn);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void addWidget(Widget widget, List<Widget> parents) {
        parents.forEach(widget::addParent);
        this.widgets.add(widget);

        var children = widget.getChildren();

        parents.add(widget);
        children.forEach((child) -> this.addWidget(child, parents));
    }

    public boolean isOpen(Class<? extends View<?>> cls) {
        return this.widgets.stream()
            .anyMatch(cls::isInstance);
    }

    private void poll() {
        if (this.controller == null) {
            return;
        }

        this.controller.poll();
    }

    public void updateModel(Pollable<?> model) {
        this.model = model.persist(this.model);
    }

    public User getModel() {
        return this.model;
    }

    private void update() {
        this.widgets.forEach(Widget::update);

        this.widgets.stream()
            .filter(View.class::isInstance)
            .map(View.class::cast)
            .filter(View::shouldReinitialize)
            .findAny()
            .ifPresent(this::openView);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var g = (Graphics2D) graphics;

        g.setRenderingHints(Main.RENDERING_HINTS);

        this.widgets.stream()
            .sorted(Comparator.comparingInt((ui) -> ui.treeMatch(Widget::forceTop) ? 1 : 0))
            .forEach((widget) -> widget.draw(g));
    }

    public List<Widget> getWidgets(boolean blocked) {
        var blocker = this.widgets.stream()
            .filter(Widget::blocking)
            .reduce((a, b) -> b)
            .orElse(null);

        if (blocked || blocker == null) {
            return List.copyOf(this.widgets);
        }

        return this.widgets.stream()
            .filter((widget) -> widget.isChild(blocker))
            .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        var frame = new JFrame();

        frame.setTitle("Wordcrex");
        frame.setSize(Main.FRAME_SIZE, Main.FRAME_SIZE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new Main(frame, args.length > 0 ? args[0] : "prod"));
        frame.setVisible(true);
    }
}
