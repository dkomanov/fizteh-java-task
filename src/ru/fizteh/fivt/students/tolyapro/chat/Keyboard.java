package ru.fizteh.fivt.students.tolyapro.chat;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

import ru.fizteh.fivt.students.tolyapro.chat.client.GuiClient;

public class Keyboard implements KeyListener {

    JTextArea area;

    public Keyboard(JTextArea jTextArea) {
        area = jTextArea;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.print(e.getKeyChar());
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            GuiClient.sendMyMessage(area.getText().replaceAll("\n", ""));
            area.setText("");
            area.setCaretPosition(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}