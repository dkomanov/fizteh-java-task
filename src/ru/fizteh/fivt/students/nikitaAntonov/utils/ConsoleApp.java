package ru.fizteh.fivt.students.nikitaAntonov.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Класс, представляющий абстракцию простого консольного приложения, получающего
 * что-то либо из параметров, либо из stdin (в случае отсутствия параметров)
 * 
 * Введён как средство избежать копипасты из калькулятора
 * 
 * @author Антонов Никита
 */
public abstract class ConsoleApp {

    public void run(String args[]) {
        try {
            if (args.length > 0) {
                runWithParams(args);
            } else {
                runInteractive();
            }
        } catch (IOException e) {
            System.err.println("Unknown IO error");
            System.exit(1);
        }
    }

    public void runWithParams(String args[]) throws IOException {
        String str = Utils.concat(args);

        if (str.trim().isEmpty()) {
            runInteractive();
            return;
        }

        try {
            processLine(str);
        } catch (ConsoleAppException e) {
            System.exit(1);
        }
    }

    public void runInteractive() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String s = getLine(in);
        boolean isComplete = false;

        while (!(s == null || isComplete)) {

            if (s.trim().isEmpty()) {
                s = getLine(in);
                continue;
            }

            try {
                isComplete = processLine(s);
            } catch (ConsoleAppException e) {
            }
            
            if (!isComplete) {
                s = getLine(in);
            }
        }

        Utils.closeResource(in);
    }

    private String getLine(BufferedReader in) throws IOException {
        printPrompt();
        return in.readLine();
    }

    /* Должен вернуть true в случае необходимости завершить работу */
    protected abstract boolean processLine(String s) throws ConsoleAppException;
    protected abstract void printPrompt();

}
