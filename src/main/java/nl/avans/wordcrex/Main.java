package nl.avans.wordcrex;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.controller.impl.LoginController;
import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.particle.Particle;
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
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main extends JPanel {
    public static final Random RANDOM = new Random();
    public static final int FRAME_SIZE = 512;
    public static final int TASKBAR_SIZE = 32;

    private static final RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private static final Stroke OUTLINE = new BasicStroke(2);

    private final JFrame frame;
    private final Database database;
    private final List<Widget> widgets;
    private final List<Particle> particles;
    private final Loop loop;
    private final Listener listener;

    private Controller<?> controller;
    private User model;

    private Main(JFrame frame, String config) {
        this.frame = frame;
        this.database = new Database(config);
        this.widgets = new CopyOnWriteArrayList<>();
        this.particles = new CopyOnWriteArrayList<>();
        this.loop = new Loop(Map.of(
            2.0d, this::poll,
            30.0d, this::update,
            60.0d, this::repaint
        ));
        this.listener = new Listener(this.frame, this);
        this.model = new User(this.database);

        this.setFont(Fonts.NORMAL);
        this.setForeground(Color.WHITE);
        this.setBackground(Colors.DARKER_BLUE);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.setDoubleBuffered(true);
        this.addMouseListener(this.listener);
        this.addMouseMotionListener(this.listener);
        this.addKeyListener(this.listener);
        this.openController(LoginController.class);
        this.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var g = (Graphics2D) graphics;

        g.setRenderingHints(Main.RENDERING_HINTS);
        g.setStroke(Main.OUTLINE);

        this.drawParticles(g, false);
        this.widgets.forEach((widget) -> widget.draw(g));
        this.drawParticles(g, true);

        var view = this.getView();

        if (view != null) {
            view.drawForeground(g);
        }
    }

    public void start() {
        this.loop.start();
    }

    public void stop() {
        this.loop.stop();
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }

    public void openController(Class<? extends Controller<User>> cls) {
        this.openController(cls, Function.identity());
    }

    public <T extends Pollable<T>> void openController(Class<? extends Controller<T>> cls, Function<User, T> fn) {
        this.updateModel(fn.apply(this.getModel()).initialize());
        this.controller = this.createController(cls, fn);
        this.openView(this.controller.createView());
    }

    public void addParticle(Particle particle) {
        this.particles.add(particle);
    }

    public User getModel() {
        return this.model;
    }

    public void updateModel(Pollable<?> model) {
        this.model = model.persist(this.model);
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
            .filter((widget) -> widget == blocker || widget.childOf(blocker))
            .collect(Collectors.toList());
    }

    public boolean isOpen(Class<? extends View<?>> cls) {
        return this.widgets.stream()
            .anyMatch(cls::isInstance);
    }

    public void tabFocus(boolean reverse) {
        var widgets = this.getFocusable();
        var updated = false;

        for (int i = 0; i < widgets.size(); i++) {
            var widget = widgets.get(i);

            if (!widget.hasFocus()) {
                continue;
            }

            if (reverse && i == 0) {
                widgets.get(widgets.size() - 1).requestFocus();
            } else if (reverse) {
                widgets.get(i - 1).requestFocus();
            } else if (i < widgets.size() - 1) {
                widgets.get(i + 1).requestFocus();
            } else {
                widgets.get(0).requestFocus();
            }

            updated = true;
        }

        if (!updated && !widgets.isEmpty()) {
            widgets.get(reverse ? widgets.size() - 1 : 0).requestFocus();
        }
    }

    private void openView(View<?> view) {
        this.widgets.clear();

        var dead = this.particles.stream()
            .filter((particle) -> !particle.persist(view))
            .collect(Collectors.toList());
        this.particles.removeAll(dead);

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
        parents.add(widget);
        this.widgets.add(widget);
        widget.children().forEach((child) -> this.addWidget(child, parents));
    }

    private void poll() {
        if (this.controller == null) {
            return;
        }

        this.controller.poll();
    }

    private void update() {
        this.widgets.forEach((widget) -> widget.update(this::addParticle));

        var dead = this.particles.stream()
            .filter((particle) -> !particle.update(this::addParticle))
            .collect(Collectors.toList());
        this.particles.removeAll(dead);

        var view = this.getView();

        if (view.requestingInitialize()) {
            var current = this.getFocusable();

            this.openView(view);
            this.listener.mouseMoved(null);

            var next = this.getFocusable();

            if (current.size() == next.size()) {
                for (var i = 0; i < current.size(); i++) {
                    next.get(i).setFocus(current.get(i).hasFocus());
                }
            }

            return;
        }

        var widgets = this.getFocusable();
        var requester = widgets.stream()
            .filter(Widget::requestingFocus)
            .findFirst()
            .orElse(null);

        if (requester == null) {
            return;
        }

        this.getWidgets(true).forEach((widget) -> widget.setFocus(false));
        requester.setFocus(true);
    }

    private void drawParticles(Graphics2D g, boolean foreground) {
        this.particles.stream()
            .filter((particle) -> particle.foreground == foreground)
            .sorted(Comparator.comparingInt(Particle::priority))
            .forEach((particle) -> particle.draw(g));
    }

    private View<?> getView() {
        return this.widgets.stream()
            .filter(View.class::isInstance)
            .map(View.class::cast)
            .findFirst()
            .orElse(null);
    }

    private List<Widget> getFocusable() {
        return this.getWidgets(false).stream()
            .filter(Widget::focusable)
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
