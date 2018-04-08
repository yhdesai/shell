package me.isaiah.shell;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

public class DragListener extends MouseAdapter {
    private final Component COMPONENT_TO_DRAG;
    private final int MOUSE_BUTTON;
    private Point mousePosition;
    private Point sourceLocation;
    private Point locationOnScreen;
    private int buttonPressed;
    private Point lastdragloc;
    private boolean dragging;

    public DragListener(final Component componentToDrag) {
         this(componentToDrag, MouseEvent.BUTTON3);
    }

    public DragListener(final Component componentToDrag, final int mouseButton) {
        this.COMPONENT_TO_DRAG = componentToDrag;
        this.MOUSE_BUTTON = mouseButton;
        this.dragging = false;
    }

    @Override public void mousePressed(final MouseEvent e) {
        this.buttonPressed = e.getButton();
        this.mousePosition = MouseInfo.getPointerInfo().getLocation();
        this.sourceLocation = new Point();
    }

    @Override public void mouseDragged(final MouseEvent e) {
        if (this.buttonPressed == MOUSE_BUTTON) {
            this.dragging = true;
            this.locationOnScreen = e.getLocationOnScreen();
            this.sourceLocation = this.COMPONENT_TO_DRAG.getLocation(this.sourceLocation);
            this.sourceLocation.translate((int) (this.locationOnScreen.getX() - this.mousePosition.getX()), (int) (this.locationOnScreen.getY() - this.mousePosition.getY()));
            this.COMPONENT_TO_DRAG.setLocation(this.sourceLocation);
            this.mousePosition = MouseInfo.getPointerInfo().getLocation();
            this.lastdragloc = this.sourceLocation;
            this.dragging = false;
        }
    }

    public void addHandle(Component handle) {
        handle.addMouseListener(this);
        handle.addMouseMotionListener(this);
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (lastdragloc != null && !dragging) COMPONENT_TO_DRAG.setLocation(lastdragloc);
            }
        };
        new Timer(1000, taskPerformer).start();
    }
}