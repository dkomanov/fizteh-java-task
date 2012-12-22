package ru.fizteh.fivt.students.tolyapro.chat;

import java.io.Writer;

import javax.swing.JTextArea;

/**
 * @author tolyapro
 * 
 * @22.12.2012
 */
public class TextAreaWriter extends Writer {
    protected final JTextArea text;

    public TextAreaWriter(JTextArea text) {
        this.text = text;
    }

    @Override
    public void write(char[] c, int off, int len) {
        text.append(new String(c, off, len));
        moveCaret(); //
    }

    public void moveCaret() {
        text.setCaretPosition(text.getDocument().getLength());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
