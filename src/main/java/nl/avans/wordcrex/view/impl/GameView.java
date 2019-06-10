package nl.avans.wordcrex.view.impl;

import nl.avans.wordcrex.controller.impl.AbstractGameController;
import nl.avans.wordcrex.model.TileType;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Assets;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.widget.Widget;
import nl.avans.wordcrex.widget.impl.ButtonWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameView extends View<AbstractGameController> {
    public GameView(AbstractGameController controller) {
        super(controller);
    }

    @Override
    public void draw(Graphics2D g) {
        for (var tile : this.controller.getTiles()) {
            var position = this.getAbsolutePos(tile.x, tile.y);

            if (position == null) {
                continue;
            }

            g.setColor(this.controller.getTileColor(tile));
            g.fillRect(position.a + 1, position.b + 1, 22, 22);

            if (tile.type != TileType.NONE && tile.type != TileType.CENTER) {
                g.setColor(Color.WHITE);
                StringUtil.drawCenteredString(g, 52 + tile.x * 24, 52 + tile.y * 24, 24, 24, tile.multiplier + tile.type.type);
            }
        }
    }

    @Override
    public void update(Consumer<Particle> addParticle) {
    }

    @Override
    public List<Widget> children() {
        var children = new ArrayList<Widget>();

        if (this.controller.canPlay()) {
            children.add(new ButtonWidget(Assets.read("next"), "spelen", 22, 76, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("messages"), "berichten", 22, 124, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("close"), "opgeven", 22, 172, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("reset"), "resetten", 22, 220, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("shuffle"), "shudden", 22, 458, 32, 32, () -> {}));
        } else {
            children.add(new ButtonWidget(Assets.read("winner"), "winnende bord", 22, 76, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("host"), "bord van uitdager", 22, 124, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("opponent"), "bord van tegenstander", 22, 172, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("next"), "volgende ronde", 22, 356, 32, 32, () -> {}));
            children.add(new ButtonWidget(Assets.read("back"), "vorige ronde", 22, 404, 32, 32, () -> {}));
        }

        return children;
    }

    private Pair<Integer, Integer> getAbsolutePos(int x, int y) {
        var size = Math.sqrt(this.controller.getTiles().size());

        if (x <= 0 || x > size || y <= 0 || y > size) {
            return null;
        }

        return new Pair<>(52 + x * 24, 52 + y * 24);
    }

    private Pair<Integer, Integer> getRelativePos(int x, int y) {
        var size = Math.sqrt(this.controller.getTiles().size()) + 1;

        if (x < 52 + 24 || x >= 52 + size * 24 || y < 52 + 24 || y >= 52 + size * 24) {
            return null;
        }

        var relativeX = (x - 52) / 24;
        var relativeY = (y - 52) / 24;

        return new Pair<>(relativeX, relativeY);
    }
}
