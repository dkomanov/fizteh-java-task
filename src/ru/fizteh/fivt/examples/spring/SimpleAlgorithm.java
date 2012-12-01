package ru.fizteh.fivt.examples.spring;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dmitriy Komanov (spacelord)
 */
public class SimpleAlgorithm implements Algorithm {

    @Autowired
    private Printer printer;

    @Override
    public void performAction(String parameter) {
        printer.println("Parameters is " + parameter);
    }
}
