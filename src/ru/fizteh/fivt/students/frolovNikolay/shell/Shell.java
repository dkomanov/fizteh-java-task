package ru.fizteh.fivt.students.frolovNikolay.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * Фролов Николай. 196 группа
 * Задание 3. Shell.
 * Исполнительный класс. 
 */
public class Shell {
    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir").toString();
        if (args.length == 0) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(System.in));
            } catch (Exception crush) {
                System.err.println(crush.getMessage());
                System.exit(1);
            }
            String command = null;
            while (true) {
                System.out.print("$ ");
                try {
                    command = reader.readLine();
                } catch (Exception crush) {
                    System.err.println(crush.getMessage());
                    System.exit(1);
                }
                if (command == null) {
                    System.exit(0);
                } else {
                    int i = 0;
                    for (; i < command.length(); ++i) {
                        if (!Character.isWhitespace(command.charAt(i))) {
                            break;
                        }
                    }
                    if (i == command.length()) {
                        continue;
                    }
                    String[] commandsArray = command.substring(i).split("[\\s]*;[\\s]*");
                    for (String iter : commandsArray) {
                        String[] commandArray = iter.split("[\\s]+");
                        try {
                            currentDir = Executer.execute(commandArray, currentDir);
                        } catch (Exception crush) {
                            System.err.println(crush.getMessage());
                        }
                        if (currentDir == null) {
                            System.exit(0);
                        }
                    }
                }
            }
        } else {
            StringBuilder argsConvertor = new StringBuilder();
            for (String iter : args) {
                argsConvertor.append(iter);
                argsConvertor.append(' ');
            }
            String fullArgs = argsConvertor.toString();
            int i = 0;
            for (; i < fullArgs.length(); ++i) {
                if (!Character.isWhitespace(fullArgs.charAt(i))) {
                    break;
                }
            }
            if (i == fullArgs.length()) {
                System.err.println("Error! Empty command");
                System.exit(1);
            }
            String[] commands = fullArgs.substring(i).split("[\\s]*;[\\s]*");
            for (String iter : commands) {
                if (iter.isEmpty()) {
                    System.err.println("Error! Empty command");
                    System.exit(1);
                } else {
                    String[] commandArray = iter.split("[\\s]+");
                    try {
                        currentDir = Executer.execute(commandArray, currentDir);
                    } catch (Exception crush) {
                        System.err.println(crush.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }
}