package ru.fizteh.fivt.students.altimin.shell;

/**
 * User: altimin
 * Date: 11/9/12
 * Time: 11:19 PM
 */
public class Shell {
    private String currentPath;
    private String[] commands = { "cd" , "mkdir", "pwd", "rm", "cp", "mv", "dir", "exit" };
    public void move(String from, String to) {

    }
    public void processCommand(String command) throws IllegalArgumentException {
        String[] parsedCommand = command.split(" ");
        if (parsedCommand.length == 0) {
            throw new IllegalArgumentException("Empty command");
        }
    }
}
