package nl.avans.wordcrex.widget.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.controller.impl.ApproveController;
import nl.avans.wordcrex.controller.impl.DashboardController;
import nl.avans.wordcrex.controller.impl.LoginController;
import nl.avans.wordcrex.controller.impl.SuggestController;
import nl.avans.wordcrex.model.UserRole;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ApproveView;
import nl.avans.wordcrex.view.impl.DashboardView;
import nl.avans.wordcrex.view.impl.SuggestView;
import nl.avans.wordcrex.widget.Widget;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SidebarWidget extends Widget {
    private final List<Item> items = List.of(
        new Item<>("GAMES", DashboardController.class, DashboardView.class, UserRole.PLAYER),
        new Item<>("OBSERVE", null, null, UserRole.OBSERVER),
        new Item<>("SUGGEST", SuggestController.class, SuggestView.class, UserRole.PLAYER),
        new Item<>("APPROVE", ApproveController.class, ApproveView.class, UserRole.MODERATOR),
        new Item<>("MANAGE", null, null, UserRole.ADMINISTRATOR),
        new Item<>("ACCOUNT", null, null, null)
    );
    private final Map<String, ButtonWidget> children = new HashMap<>();
    private final Main main;

    private boolean open;

    public SidebarWidget(Main main) {
        this.main = main;
    }

    @Override
    public void draw(Graphics2D g) {
        this.children.forEach((key, value) -> {
            var item = this.items.stream()
                .filter((i) -> i.title.equals(key))
                .findFirst()
                .orElse(null);

            value.setVisible(this.open);

            if (item != null && item.controller != null) {
                value.setEnabled(!this.main.isOpen(item.controller));
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
        var filtered = this.items.stream()
            .filter((i) -> i.role == null || this.main.getModel().hasRole(i.role))
            .collect(Collectors.toList());

        for (var i = 0; i < filtered.size(); i++) {
            var item = filtered.get(i);

            this.children.put(item.title, new ButtonWidget(item.title, 32, 64 + 48 * i, 192, 32, () -> this.main.openController(item.controller)));
        }

        this.children.put("LOGOUT", new ButtonWidget("LOGOUT", 32, 448, 192, 32, () -> this.main.openController(LoginController.class)));

        return List.copyOf(this.children.values());
    }

    public void toggle() {
        this.open = !this.open;
    }

    public boolean isOpen() {
        return this.open;
    }

    private class Item<T extends Controller<?>> {
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
