package nl.avans.wordcrex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Listener implements MouseListener, MouseMotionListener, KeyListener {
    private final JFrame frame;
    private final Main main;

    private Point dragPoint;
    private int lastX;
    private int lastY;

    public Listener(JFrame frame, Main main) {
        this.frame = frame;
        this.main = main;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        try {
            this.main.getWidgets(false).forEach((widget) -> widget.mouseClick(event.getX(), event.getY()));
        } catch (Exception e) {
            this.main.handleError(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getY() <= Main.TASKBAR_SIZE && event.getX() > Main.TASKBAR_SIZE && event.getX() < Main.FRAME_SIZE - Main.TASKBAR_SIZE) {
            this.dragPoint = event.getPoint();
        }

        try {
            this.main.getWidgets(false).forEach((widget) -> widget.mousePress(event.getX(), event.getY()));
        } catch (Exception e) {
            this.main.handleError(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (this.dragPoint != null) {
            this.dragPoint = null;
        }

        try {
            this.main.getWidgets(false).forEach((widget) -> widget.mouseRelease(event.getX(), event.getY()));
        } catch (Exception e) {
            this.main.handleError(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (this.dragPoint != null) {
            var current = event.getLocationOnScreen();

            this.frame.setLocation(current.x - this.dragPoint.x, current.y - this.dragPoint.y);
        } else {
            try {
                this.main.getWidgets(false).forEach((widget) -> widget.mouseDrag(event.getX(), event.getY()));
            } catch (Exception e) {
                this.main.handleError(e);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (event != null) {
            this.lastX = event.getX();
            this.lastY = event.getY();
        }

        try {
            this.main.getWidgets(false).forEach((widget) -> widget.mouseMove(this.lastX, this.lastY));
        } catch (Exception e) {
            this.main.handleError(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
        try {
            this.main.getWidgets(false).forEach((widget) -> widget.keyType(event.getKeyChar()));
        } catch (Exception e) {
            this.main.handleError(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getExtendedKeyCode() == KeyEvent.VK_TAB) {
            this.main.tabFocus(event.isShiftDown());

            return;
        }

        try {
            this.main.getWidgets(false).forEach((widget) -> widget.keyPress(event.getExtendedKeyCode(), event.getModifiersEx()));
        } catch (Exception e) {
            this.main.handleError(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
