/*
 * Gui.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.impl.displays;

import ru.fizteh.fivt.students.harius.chat.base.DisplayBase;
import javax.swing.*;
import java.awt.event.*;

public class Gui extends DisplayBase
{
    private ChatPanel main = new ChatPanel(this);
    private JFrame fr;

    public Gui() {
        fr = new JFrame("Free chat without SMS or registration");
        fr.setSize(800, 600);
        fr.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                close();
            }
        });
        fr.add(main);
        fr.setVisible(true);
    }

    @Override
    public void message(String user, String message) {
        main.message(user, message);
    }

    @Override
    public void warn(String warn) {
        main.warn(warn);
    }

    @Override
    public void error(String error) {
        main.error(error);
    }

    @Override
    public void run() {
        
    }

    @Override
    public void close() {
        if (fr.isEnabled()) {
            fr.setEnabled(false);
            fr.dispose();
            notifyClosed();
        }
    }

    @Override
    public void processServerAdded(String desc) {
        main.addServer(desc);
    }

    @Override
    public void processServerRemoved(int index) {
        main.removeServer(index);
    }

    @Override
    public void processServerChanged(int index) {
        main.changeServerTo(index);
    }

    @Override  
    public boolean needsReflect() {
        return true;
    }  
}