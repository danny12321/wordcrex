package nl.avans.wordcrex.view.swing;

import nl.avans.wordcrex.controller.swing.SwingController;
import nl.avans.wordcrex.view.swing.ui.impl.FrameUI;
import nl.avans.wordcrex.view.swing.ui.UI;
import nl.avans.wordcrex.view.swing.ui.impl.LoginUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    public static final int TASKBAR_SIZE = 32;

    private final JFrame frame;
    private final SwingController controller;
    private final List<UI> interfaces;

    private Point movePosition;
    private Font normalFont;
    private Font bigFont;

    public GamePanel(JFrame frame, SwingController controller) {
        this.setFocusable(true);
        this.setForeground(Color.WHITE);
        this.setBackground(Colors.DARKER_BLUE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.setFocusTraversalKeysEnabled(false);
        this.setDoubleBuffered(true);

        this.frame = frame;
        this.controller = controller;
        this.interfaces = new CopyOnWriteArrayList<>();

        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, GamePanel.class.getResourceAsStream("/fonts/RobotoMono.ttf"));

            this.normalFont = font.deriveFont(16.0f);
            this.bigFont = font.deriveFont(Font.BOLD, 24.0f);

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();

            this.close();
        }

        this.setFont(this.normalFont);
        this.openUI(new LoginUI());
    }

    public Font getNormalFont() {
        return this.normalFont;
    }

    public Font getBigFont() {
        return this.bigFont;
    }

    public void openUI(UI ui) {
        this.interfaces.clear();
        this.addUI(ui, new ArrayList<>());
        this.addUI(new FrameUI(), new ArrayList<>());
    }

    private void addUI(UI ui, List<UI> parents) {
        ui.initialize(this, this.controller);
        parents.forEach(ui::addParent);
        this.interfaces.add(ui);

        var children = ui.getChildren();

        if (children != null) {
            parents.add(ui);
            children.forEach((child) -> this.addUI(child, parents));
        }
    }

    public boolean isOpen(Class<? extends UI> cls) {
        return this.interfaces.stream()
            .anyMatch(cls::isInstance);
    }

    public void close() {
        this.controller.stop();
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }

    public void update() {
        this.interfaces.forEach(UI::update);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var g = (Graphics2D) graphics;

        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        this.interfaces.forEach((ui) -> ui.draw(g));
    }

    private List<UI> getUnblocked() {
        var blocker = this.interfaces.stream()
            .filter(UI::isBlocking)
            .reduce((first, second) -> second)
            .orElse(null);

        if (blocker == null) {
            return this.interfaces;
        }

        return this.interfaces.stream()
            .filter((ui) -> ui.isChild(blocker))
            .collect(Collectors.toList());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.getUnblocked().forEach((ui) -> ui.mouseClick(e.getX(), e.getY()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getY() <= GamePanel.TASKBAR_SIZE && e.getX() > GamePanel.TASKBAR_SIZE && e.getX() < SwingView.SIZE - GamePanel.TASKBAR_SIZE) {
            this.movePosition = e.getPoint();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else {
            this.getUnblocked().forEach((ui) -> ui.mousePress(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.movePosition != null) {
            this.movePosition = null;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            this.getUnblocked().forEach((ui) -> ui.mouseRelease(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.movePosition != null) {
            var current = e.getLocationOnScreen();

            this.frame.setLocation(current.x - this.movePosition.x, current.y - this.movePosition.y);
        } else {
            this.getUnblocked().forEach((ui) -> ui.mouseDrag(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        var cursor = this.getUnblocked().stream()
            .mapToInt((ui) -> ui.mouseMove(e.getX(), e.getY()))
            .filter((c) -> c != Cursor.DEFAULT_CURSOR)
            .findFirst()
            .orElse(Cursor.DEFAULT_CURSOR);

        this.setCursor(Cursor.getPredefinedCursor(cursor));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.getUnblocked().forEach((ui) -> ui.keyType(e.getKeyChar()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.getUnblocked().forEach((ui) -> ui.keyPress(e.getExtendedKeyCode(), e.getModifiersEx()));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.getUnblocked().forEach((ui) -> ui.keyRelease(e.getKeyChar()));
    }
}
