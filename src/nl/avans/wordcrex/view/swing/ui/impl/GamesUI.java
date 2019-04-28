package nl.avans.wordcrex.view.swing.ui.impl;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.Colors;
import nl.avans.wordcrex.view.swing.GamePanel;
import nl.avans.wordcrex.view.swing.SwingView;
import nl.avans.wordcrex.view.swing.ui.UI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamesUI extends UI {
    private int scroll;
    private GamePanel game;
    private ArrayList<String> invites = new ArrayList<>();

    @Override
    public void initialize(GamePanel game, SwingController controller) {
        this.game = game;

        this.invites.add("broodjeaap");
        this.invites.add("nierennakker");
        this.invites.add("Danny");
    }

    @Override
    public void draw(Graphics2D g) {

        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, GamePanel.TASKBAR_SIZE - this.scroll, SwingView.SIZE - GamePanel.TASKBAR_SIZE, 50);
        g.setColor(Colors.DARK_YELLOW);
        g.drawString("INVITATIONS", GamePanel.TASKBAR_SIZE, 65 - this.scroll);

        int ovalY = 95;
        int textY = 125;
        int seperatorY = 156;
        for (int i = 0; i < invites.size(); i++) {
            g.setColor(Colors.DARK_YELLOW);
            g.fillOval(GamePanel.TASKBAR_SIZE, ovalY - this.scroll, 42, 42);
            g.setFont(this.game.getBigFont());
            g.setColor(Colors.DARKER_BLUE);
            g.drawString(String.valueOf(invites.get(i).charAt(0)).toUpperCase(), GamePanel.TASKBAR_SIZE + 14, textY - this.scroll);

            g.setFont(this.game.getNormalFont());
            g.setColor(Color.WHITE);
            g.drawString(invites.get(i), 120, textY - this.scroll);

            if(i + 1 < invites.size()){
                g.setColor(Colors.DARKERER_BLUE);
                g.fillRect(120, seperatorY - this.scroll, 236, 4);
                ovalY += 90;
                textY += 90;
                seperatorY += 90;
            }
        }


        g.setColor(Colors.DARK_BLUE);
        g.fillRect(0, (textY + GamePanel.TASKBAR_SIZE) - this.scroll, SwingView.SIZE - GamePanel.TASKBAR_SIZE, 50);
        g.setColor(Colors.DARK_YELLOW);
        g.drawString("YOUR TURN", GamePanel.TASKBAR_SIZE, (textY + 65) - this.scroll);
//
//        g.setColor(Colors.DARK_YELLOW);
//        g.fillOval(GamePanel.TASKBAR_SIZE, 95 - this.scroll, 42, 42);
//        g.setFont(this.game.getBigFont());
//        g.setColor(Colors.DARKER_BLUE);
//        g.drawString("B", GamePanel.TASKBAR_SIZE + 14, 125 - this.scroll);
//
//        g.setFont(this.game.getNormalFont());
//        g.setColor(Color.WHITE);
//        g.drawString("broodjeaap", 120, 110 - this.scroll);
//        g.drawString("5 - 69", 120, 135 - this.scroll);
//
//        g.setColor(Colors.DARKERER_BLUE);
//        g.fillRect(120, 156 - this.scroll, 236, 4);
    }

    @Override
    public List<UI> getChildren() {
        return List.of(
            new ScrollUI(SwingView.SIZE * 2, (scroll) -> this.scroll = scroll)
        );
    }
}
