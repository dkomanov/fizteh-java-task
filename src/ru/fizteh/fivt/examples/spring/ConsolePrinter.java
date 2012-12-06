package ru.fizteh.fivt.examples.spring;

/**
 * @author Dmitriy Komanov (spacelord)
 */
public class ConsolePrinter implements Printer {
    @Override
    public void println(String s) {
        System.out.println(s);
    }
}
