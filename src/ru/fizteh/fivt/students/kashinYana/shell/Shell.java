package ru.fizteh.fivt.students.kashinYana.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

/**
 * User: Yana Kashinskaya
 * Group: 195
*/

public class Shell {

    static File path = new File(".");

    public static void main(String[] args) throws Exception {
        if(args.length > 0) {
            packageRegime(args);
        } else {
            interactiveRegime();
        }
    }

    static void packageRegime(String[] args) throws Exception{
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            input.append(args[i] + " ");
        }
        String commands = input.toString();
        String[] command = commands.split(";");
        for (int i = 0; i < command.length; i++) {
            try {
                recognizeCommand(command[i]);
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
    }

    static void interactiveRegime() throws Exception{
        while(true) {
            System.out.print("$ ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String command = reader.readLine();
            try {
                recognizeCommand(command);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static void recognizeCommand(String command) throws Exception{
        String[] parseCommand = command.split("[ \t]");
        if (parseCommand[0].equals("exit")) {
            System.exit(0);
        }
        if (parseCommand[0].equals("cd")) {
            if (parseCommand.length != 2) {
                throw new Exception("strange numbers of argv");
            }
            File newPath = getFile(parseCommand[1]);
            if(newPath.isDirectory()) {
                path = newPath;
            } else {
                System.err.println("cd: \'" + parseCommand[1] + "\': No such file or directory");
            }
        } else if(parseCommand[0].equals("pwd")){
            if (parseCommand.length != 1) {
                throw new Exception("strange numbers of argv");
            }
            System.out.println(path.getCanonicalPath());
        } else if(parseCommand[0].equals("dir")) {
            if (parseCommand.length != 1) {
                throw new Exception("strange numbers of argv");
            }
            String[] list;
            list = path.list();
            for (int i = 0; i < list.length; i++) {
                System.out.println(list[i]);
            }
        } else if (parseCommand[0].equals("mkdir")) {
            if (parseCommand.length != 2) {
                throw new Exception("strange numbers of argv");
            }
            File newPath = getFile(parseCommand[1]);
            if(!newPath.mkdir()) {
                System.err.println("mkdir: cannot create directory \'" + parseCommand[1] +
                                   "\': No such file or directory");
            }
        } else if (parseCommand[0].equals("rm")){
            if (parseCommand[1].equals("-r")) {
                File newPath = getFile(parseCommand[2]);
                if(newPath.exists()) {
                    newPath.delete();
                } else {
                    System.err.println("rm: cannot remove \'" + parseCommand[1] + "\': No such file or directory");
                }
            } else {
                File newPath = getFile(parseCommand[1]);
                if(newPath.isFile()) {
                    newPath.delete();
                } else if (newPath.exists()){
                    System.err.println("rm: cannot remove \'" + parseCommand[1] + "\': Is a directory");
                } else {
                    System.err.println("rm: cannot remove \'" + parseCommand[1] + "\': No such file or directory");
                }
            }
        } else if(parseCommand[0].equals("mv")){
            if (parseCommand.length != 3) {
                throw new Exception("strange numbers of argv");
            }
            File firstPath = getFile(parseCommand[1]);
            File secondPath = getFile(parseCommand[2]);
            if (!firstPath.exists()) {
                System.err.println("mv: cannot stat \'" + parseCommand[1] + "\': No such file or directory");
            }
            if (!firstPath.renameTo(secondPath)) {
                System.err.println("mv: cannot move \'" + parseCommand[1] + "\' to \'" + parseCommand[2]
                                   +  "\': No such file or directory");
            }
            firstPath.delete();
        } else if(parseCommand[0].equals("cp")) {
            if (parseCommand.length != 3) {
                throw new Exception("strange numbers of argv");
            }
            File firstPath = getFile(parseCommand[1]);
            File secondPath = getFile(parseCommand[2]);
            if (!firstPath.exists()) {
                System.err.println("cp: cannot stat \'" + parseCommand[1] + "\': No such file or directory");
            }
            if (!firstPath.renameTo(secondPath)) {
                System.err.println("cp: cannot create regular file \'" + parseCommand[2] +
                                   "\': No such file or directory");
            }
            firstPath.delete();
        } else {
            throw new Exception("command-not-found");
        }
    }

    static File getFile(String file) {
        String newNamePath;
        if(file.charAt(0) == '/') {
            newNamePath = file;
        } else {
            newNamePath = path.getAbsolutePath() + "/" + file;
        }
        File newPath = new File(newNamePath);
        return newPath;
    }
}
