package ru.fizteh.fivt.students.nikitaAntonov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.fizteh.fivt.students.nikitaAntonov.utils.Utils;

/**
 * Класс имитации shell`а
 * 
 * @author Антонов Никита
 */
class Shell extends ConsoleApp {

    public static void main(String args[]) {
        Shell shell = new Shell();

        shell.run(args);
    }

    public boolean execute(String str) throws Exception {
        String parts[] = str.split("\\s+");

        if (parts.length == 0)
            return false;

        switch (parts[0].toLowerCase()) {
        case "cd":
            doCd(parts);
            break;
        case "mkdir":
            doMkdir(parts);
            break;
        case "pwd":
            doPwd();
            break;
        case "rm":
            doRm(parts);
            break;
        case "cp":
            doCp(parts);
            break;
        case "mv":
            doMv(parts);
            break;
        case "dir":
            doDir(parts);
            break;
        case "exit":
            System.exit(0);
            // Arrays.sor`
            break;
        default:
            throw new Exception("Unknown command " + parts[0]);
        }

        return false;
    }

    private static void doCd(String parts[]) {

    }

    private static void doMkdir(String parts[]) {

    }

    private static void doPwd() {
        System.out.println(System.getProperty("user.dir"));
    }

    private static void doRm(String parts[]) {

    }

    private static void doCp(String parts[]) {
    }

    private static void doMv(String parts[]) {

    }

    private static void doDir(String parts[]) {

    }

    @Override
    protected boolean processLine(String s) throws ConsoleAppException {
        String expressions[] = s.split("\\s*;\\s*");

        for (String expr : expressions) {
            if (execute(expr)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void printPrompt() {
        System.out.print("$ ");
    }

}

/**
 * Класс, представляющий абстракцию простого консольного приложения, получающего
 * что-то либо из параметров, либо из stdin (в случае отсутствия параметров)
 * 
 * Введён как средство избежать копипасты из калькулятора
 * 
 * @author Антонов Никита
 */
abstract class ConsoleApp {

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

            s = getLine(in);
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

class ConsoleAppException extends Exception {

    private static final long serialVersionUID = -5154101410931907193L;

    public ConsoleAppException(String message) {
        super(message);
    }
}