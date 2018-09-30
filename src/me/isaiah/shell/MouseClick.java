package me.isaiah.shell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//@Deprecated
public abstract class MouseClick extends MouseAdapter {

    @Override public void mouseClicked(MouseEvent e) { click(e); }

    public abstract void click(MouseEvent e);
}