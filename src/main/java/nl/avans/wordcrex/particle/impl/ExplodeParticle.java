package nl.avans.wordcrex.particle.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.particle.Particle;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.util.Fonts;
import nl.avans.wordcrex.util.StringUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.GameView;

import java.awt.*;
import java.util.function.Consumer;

public class ExplodeParticle extends Particle {
    private final Character character;
    private final Color color;

    private int age = 50;

    public ExplodeParticle(int x, int y, float velocityX, float velocityY, Character character) {
        super(x, y, velocityX, velocityY, (float) Math.random() * 0.85f, true);

        var rand = Main.RANDOM.nextInt(3);

        this.character = character;
        this.color = rand == 0 ? Colors.DARK_BLUE : rand == 1 ? Colors.DARKER_BLUE : Colors.DARKERER_BLUE;
    }

    @Override
    public void draw(Graphics2D g) {
        var current = g.getComposite();
        var alpha = Math.max(0.0f, Math.min(10, this.age) / 10.0f);
        var composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

        g.setComposite(composite);
        g.setColor(this.color);
        g.fillRoundRect(this.getX(), this.getY(), 16, 16, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(Fonts.SMALL);
        StringUtil.drawCenteredString(g, this.getX(), this.getY(), 16, 16, String.valueOf(this.character.character));
        g.setFont(Fonts.NORMAL);
        g.setComposite(current);
    }

    @Override
    public boolean update(Consumer<Particle> addParticle) {
        if (this.age-- <= 0) {
            return false;
        }

        return super.update(addParticle);
    }

    @Override
    public boolean persist(View<?> view) {
        return view.getClass() == GameView.class;
    }
}
