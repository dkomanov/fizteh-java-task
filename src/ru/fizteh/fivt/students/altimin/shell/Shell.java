package ru.fizteh.fivt.students.altimin.shell;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * User: altimin
 * Date: 11/9/12
 * Time: 11:19 PM
 */

public class Shell {
    private String currentPath; // current absolute path

    private class Command {
        final String name;
        final int expectedArguments;

        public Command(String name, int expectedArguments) throws IllegalArgumentException {
            this.name = name;
            this.expectedArguments = expectedArguments;
            if (expectedArguments > 2 || expectedArguments < -1) {
                throw new IllegalArgumentException(expectedArguments + " arguments not supported");
            }
        }

        public void run(Shell shell, String[] arguments) throws IllegalArgumentException, InterruptedException {
            if (expectedArguments != -1 && expectedArguments != arguments.length) {
                throw new IllegalArgumentException(name + ": expected " + expectedArguments
                        + " but " + arguments.length + " got");
            }
            if (expectedArguments == -1 && arguments.length == 0) {
                throw new IllegalArgumentException(name + ": some arguments expected, but nothing found");
            }
            try {
                if (expectedArguments == 0) {
                    Shell.class.getMethod(name).invoke(shell);
                } else if (expectedArguments == 1) {
                    Shell.class.getMethod(name, String.class).invoke(shell, arguments[0]);
                } else if (expectedArguments == 2) {
                    Shell.class.getMethod(name, String.class, String.class).invoke(shell, arguments[0], arguments[1]);
                } else if (expectedArguments == -1) {
                    for (String argument: arguments) {
                        Shell.class.getMethod(name, String.class).invoke(shell, argument);
                    }
                } else {
                }
            } catch (NoSuchMethodException exception) {
            } catch (IllegalAccessException exception) {
            } catch (InvocationTargetException exception) {
                try {
                    throw ((IllegalArgumentException) exception.getTargetException());
                } catch (ClassCastException e) {
                }
                try {
                    throw ((InterruptedException) exception.getTargetException());
                } catch (ClassCastException e) {
                }
            }
        }

    }

    private Command[] commands = {
            new Command("cd", 1),
            new Command("mkdir", -1),
            new Command("pwd", 0),
            new Command("rm", -1),
            new Command("cp", 2),
            new Command("mv", 2),
            new Command("dir", 0),
            new Command("exit", 0)
    };

    Shell() {
        currentPath = normalizePath(new File(".").getAbsolutePath());
    }

    private String normalizePath(String path) {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return new File(path).getAbsolutePath() ;
        }
    }


    private String getPath(String fileName) throws IllegalArgumentException {
        if (new File(fileName).isAbsolute()) {
            return normalizePath(new File(fileName).getAbsolutePath());
        } else {
            return normalizePath(new File(currentPath + File.separator + fileName).getAbsolutePath());
        }
    }


    public void rm(String fileName) throws IllegalArgumentException {
        File file = new File(getPath(fileName));
        if (!file.exists()) {
            throw new IllegalArgumentException("rm: cannot remove `" + fileName + "': No such file or directory");
        }
        if (file.isDirectory()) {
            try {
                for (File childFile: file.listFiles()) {
                    rm(fileName + File.separator + childFile.getName());
                }
            } catch (NullPointerException e) {
            }
        }
        boolean success = file.delete();
        if (!success) {
            throw new IllegalArgumentException("rm: cannot remove `" + fileName + "'");
        }
    }

    public void cp(String sourceFileName, String destinationFileName) throws IllegalArgumentException {
        String normalizedSourceFileName = getPath(sourceFileName);
        String normalizedDestinationFileName = getPath(destinationFileName);
        File sourceFile = new File(normalizedSourceFileName);
        File destinationFile = new File(normalizedDestinationFileName);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("cp: cannot stat `" + sourceFileName + "': No such file or directory");
        }
        if (destinationFile.exists() && destinationFile.isDirectory()) {
            cp(normalizedSourceFileName, normalizedDestinationFileName + File.separator + sourceFile.getName());
            return;
        }
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(sourceFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("cp: cannot stat `" + sourceFileName + "': No such file or directory");
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(destinationFile);
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException("cp: accessing to `" + destinationFileName + "' failed");
        }
        final int BUFFER_SIZE = 1024;
        byte buf[] = new byte[BUFFER_SIZE];
        int lengthRead = -1;
        try {
            while ((lengthRead = fileInputStream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, lengthRead);
            }
        }
        catch (IOException e) {
            destinationFile.delete();
            throw new IllegalArgumentException("cp: cannot copy `" + sourceFileName + "' to `" + destinationFileName + "'");
        }
        try {
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
        }
    }

    public void mv(String sourceFileName, String destinationFileName) throws IllegalArgumentException {
        File sourceFile = new File(getPath(sourceFileName));
        File destinationFile = new File(getPath(destinationFileName));
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("mv: cannot stat `" + sourceFileName + "': No such file or directory");
        }
        if (destinationFile.exists() && destinationFile.isDirectory()) {
            mv(sourceFileName, destinationFileName + File.separator + sourceFile.getName());
            return;
        }
        boolean success = sourceFile.renameTo(destinationFile);
        if (!success) {
            throw new IllegalArgumentException("mv: cannot move `" + sourceFileName
                    + "' to `" + destinationFileName + "'");
        }
    }

    public void cd(String path) throws IllegalArgumentException {
        File newPath = new File(getPath(path));
        if (!newPath.exists()) {
            throw new IllegalArgumentException("cd: " + path + ": No such file or directory");
        }
        if (!newPath.isDirectory()) {
            throw new IllegalArgumentException("cd: " + path + ": Not a directory");
        }
        currentPath = normalizePath(newPath.getAbsolutePath());
    }

    public void mkdir(String dirName) throws IllegalArgumentException {
        File file = new File(getPath(dirName));
        if (file.exists()) {
            throw new IllegalArgumentException("mkdir: cannot create directory `" + dirName + "': File exists");
        }
        boolean success = file.mkdir();
        if (!success) {
            throw new IllegalArgumentException("mkdir: failed to create directory `" + dirName + "'");
        }
    }

    public void pwd() {
        System.out.println(currentPath);
    }

    public void exit() throws InterruptedException {
        throw new InterruptedException();
    }

    public void dir() {
        File currentDir = new File(currentPath);
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            return;
        }
        try {
            File[] files = currentDir.listFiles();
            Arrays.sort(files);
            for (File file: files) {
                System.out.println(file.getName());
            }
        } catch (NullPointerException e) {

        }
    }

    public void processCommand(String cmd) throws IllegalArgumentException, InterruptedException {
        String[] parsedCommand = cmd.split(" ");
        String commandName = null;
        List<String> arguments = new ArrayList<String>();
        for (String string: parsedCommand) {
            if (string.length() > 0) {
                if (commandName == null) {
                    commandName = string;
                } else {
                    arguments.add(string);
                }
            }
        }

        if (commandName == null) {
            return; // empty commands
        }

        for (Command command: commands) {
            if (command.name.equals(commandName)) {
                command.run(this, arguments.toArray(new String[arguments.size()]));
                return;
            }
        }
        throw new IllegalArgumentException(commandName + ": command not found");
    }

    public void processCommands(String cmds) throws IllegalArgumentException, InterruptedException {
        String[] commands = cmds.split(";");
        for (String command: commands) {
            processCommand(command);
        }
    }

}
