package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.controller.impl.*;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.*;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SidebarWidget extends Widget {
    public static final List<Item> ITEMS = List.of(
        new Item<>("SPELLEN", DashboardController.class, DashboardView.class, UserRole.PLAYER),
        new Item<>("BEKIJKEN", ObserveController.class, ObserveView.class, UserRole.OBSERVER),
        new Item<>("SUGGEREREN", SuggestController.class, SuggestView.class, UserRole.PLAYER),
        new Item<>("GOEDKEUREN", ApproveController.class, ApproveView.class, UserRole.MODERATOR),
        new Item<>("BEHEREN", ManageController.class, ManageView.class, UserRole.ADMINISTRATOR),
        new Item<>("ACCOUNT", AccountController.class, AccountView.class, null)
    );
    private final Map<ButtonWidget, Item> children = new LinkedHashMap<>();
    private final Main main;

    private boolean open;

    public SidebarWidget(Main main) {
        this.main = main;
    }

    @Override
    public void draw(Graphics2D g) {
        var index = new AtomicInteger();
        this.children.forEach((key, value) -> {
            var has = value == null || value.role == null || this.main.getModel().hasRole(value.role);

            key.setVisible(this.open && has);

            if (has && value != null) {
                key.setPosition(32, 64 + 48 * index.getAndIncrement());
            }

            if (value != null && value.controller != null) {
                key.setEnabled(!this.main.isOpen(value.controller));
            }
        });

        var width = Main.FRAME_SIZE / 2;

        g.setColor(Colors.DARKERER_BLUE);
        g.fillRect(this.open ? 0 : -width, Main.TASKBAR_SIZE, width, Main.FRAME_SIZE - Main.TASKBAR_SIZE);
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public List<Widget> getChildren() {
        this.ITEMS.forEach((item) -> this.children.put(new ButtonWidget(item.title, 32, 64, 192, 32, () -> this.main.openController(item.controller)), item));
        this.children.put(new ButtonWidget("LOG UIT", 32, 448, 192, 32, () -> this.main.openController(LoginController.class)), null);

        return List.copyOf(this.children.keySet());
    }

    public void toggle() {
        this.open = !this.open;
    }

    public boolean isOpen() {
        return this.open;
    }

    public static class Item<T extends Controller<?>> {
        public final String title;
        public final Class<T> controller;
        public final Class<? extends View<T>> view;
        public final UserRole role;

        private Item(String title, Class<T> controller, Class<? extends View<T>> view, UserRole role) {
            this.title = title;
            this.controller = controller;
            this.view = view;
            this.role = role;
        }
    }
}
