package ru.fizteh.fivt.students.harius.chat.impl.displays;

import ru.fizteh.fivt.students.harius.chat.base.DisplayBase;
import java.io.*;

public class Terminal extends DisplayBase
{
    private boolean closed = false;

    private BufferedReader input;

    @Override
    public void message(String user, String message) {
        System.out.println("<" + user + "> " + message);
    }

    @Override
    public void warn(String warn) {
        System.err.println(warn);
    } 

    @Override
    public void error(String error) {
        System.err.println(error);
    }

    @Override
    public void run() {
        input = new BufferedReader(
            new InputStreamReader(System.in));
        while (!closed) {
            try {
                String line = input.readLine();
                notifyObserver(line);
            } catch (IOException ioEx) {
                if (!closed) {
                    System.err.println("i/o error while reading from terminal: " + ioEx.getMessage());
                }
            }
        }
    }

    @Override
    public void close() {
        closed = true;
        try {
            input.close();
        } catch (IOException ioEx) {
            System.err.println("i/o error while closing terminal: " + ioEx.getMessage());
        }
    }      
}