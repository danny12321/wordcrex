package nl.avans.wordcrex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Listener implements MouseListener, MouseMotionListener, KeyListener {
    private final JFrame frame;
    private final Main main;

    private Point dragPoint;

    public Listener(JFrame frame, Main main) {
        this.frame = frame;
        this.main = main;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.main.getWidgets(false).forEach((widget) -> widget.mouseClick(e.getX(), e.getY()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getY() <= Main.TASKBAR_SIZE && e.getX() > Main.TASKBAR_SIZE && e.getX() < Main.FRAME_SIZE - Main.TASKBAR_SIZE) {
            this.dragPoint = e.getPoint();
        } else {
            this.main.getWidgets(false).forEach((widget) -> widget.mousePress(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.dragPoint != null) {
            this.dragPoint = null;
        } else {
            this.main.getWidgets(false).forEach((widget) -> widget.mouseRelease(e.getX(), e.getY()));
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
        if (this.dragPoint != null) {
            var current = e.getLocationOnScreen();

            this.frame.setLocation(current.x - this.dragPoint.x, current.y - this.dragPoint.y);
        } else {
            this.main.getWidgets(false).forEach((widget) -> widget.mouseDrag(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.main.getWidgets(false).forEach((widget) -> widget.mouseMove(e.getX(), e.getY()));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.main.getWidgets(false).forEach((widget) -> widget.keyType(e.getKeyChar()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.main.getWidgets(false).forEach((widget) -> widget.keyPress(e.getExtendedKeyCode(), e.getModifiersEx()));
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
