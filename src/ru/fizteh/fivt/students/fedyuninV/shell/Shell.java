package ru.fizteh.fivt.students.fedyuninV.shell;

import javax.security.auth.login.FailedLoginException;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.*;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */

public class Shell {
    private static String currPath;
    private static boolean interactive;

    private static <T extends Closeable> void tryClose(T stream, String comm) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) {
                error(comm + ": " + ex.getMessage());
            }
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
        if (!interactive) {
            System.exit(1);
        }
    }

    private static String setToAbsolute(String path) {
        if(path.charAt(0) != '/') {
            return (currPath + '/' + path);
        }
        return path;
    }

    private static void cd(String newPath, String comm) {
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            error("cd: '" + newFile.getName() + "': No such file or directory");
        } else {
            currPath = newPath;
        }
    }

    private static void mkdir(String dirName) {
        File newDir = new File(currPath + '/' + dirName);
        if (!newDir.exists()) {
            newDir.mkdir();
        } else {
            error("mkdir: '" + newDir.getName() + "': directory already exists");
        }
    }

    private static void rm(File target, String comm) {
        if (target.isDirectory()) {
            String[] files = target.list();
            for (String file : files) {
                rm(new File(target, file), comm);
            }
        }
        if (!target.delete()) {
            error(comm + ": cannot remove " + target.getName() + "': No such file or directory");
        }
    }

    private static void cp(File source, File destination, String comm){
        if (!source.exists()) {
            error(comm + ": '" + source.getName() + "': No such file or directory");
        }
        destination = new File(destination, source.getName());
        if (source.isDirectory()) {
            if (!destination.exists()) {
                if (!destination.mkdir()) {
                    error(comm + ": Failed to create directory " + destination.getName() + "'");
                }
            }
            String[] files = source.list();
            for (String file: files) {
                File newSource = new File(source, file);
                File newDestination = new File(destination, file);
                cp(newSource, newDestination, comm);
            }
        } else {
            FileInputStream reader = null;
            FileOutputStream writer = null;
            try {
                reader = new FileInputStream(source);
                writer = new FileOutputStream(destination);
                byte[] buffer = new byte[1024];
                int length;
                while((length = reader.read(buffer)) > 0) {
                    writer.write(buffer, 0, length);
                }
            } catch (Exception ex) {
                error(comm + ": " + ex.getMessage());
            } finally {
                tryClose(reader, comm);
                tryClose(writer, comm);
            }
        }
    }

    private static void dir() {
        File directory = new File(currPath);
        String[] fileNames = directory.list();
        for (String fileName : fileNames) {
            System.out.println(fileName);
        }
    }

    private static void run(String command) {
        String[] args = command.split("[ ]+");
        if (args[0].equals("exit")) {
            if (args.length != 1) {
                error("exit: Incorrect arguments");
            } else {
                System.exit(0);
            }
        } else if (args[0].equals("cp")) {
            if (args.length != 3) {
                error("cp: Incorrect arguments");
            } else {
                args[1] = setToAbsolute(args[1]);
                args[2] = setToAbsolute(args[2]);
                cp(new File(args[1]), new File(args[2]), args[0]);
            }
        } else if (args[0].equals("cd")) {
            if (args.length != 2) {
                error("cd: Incorrect arguments");
            } else {
                args[1] = setToAbsolute(args[1]);
                cd(args[1], args[0]);
            }
        } else if (args[0].equals("rm")) {
            if (args.length != 2) {
                error("rm: Incorrect arguments");
            } else {
                args[1] = setToAbsolute(args[1]);
                rm(new File(args[1]), command);
            }
        } else if (args[0].equals("pwd")) {
            if (args.length != 1) {
                error("pwd: Incorrect arguments");
            } else {
                System.out.println(currPath);
            }
        } else if (args[0].equals("dir")) {
            if (args.length != 1) {
                error("dir: Incorrect arguments");
            } else {
                dir();
            }
        } else if (args[0].equals("mkdir")) {
            if (args.length != 2) {
                error("mkdir: Incorrect arguments");
            } else {
                args[1] = setToAbsolute(args[1]);
                mkdir(args[1]);
            }
        } else if (args[0].equals("mv")) {
            if (args.length != 3) {
                error("mv: Incorrect arguments");
            } else {
                args[1] = setToAbsolute(args[1]);
                args[2] = setToAbsolute(args[2]);
                cp(new File(args[1]), new File(args[2]), args[0]);
                rm(new File(args[1]), args[0]);
            }
        } else {
            error("Incorrect command");
        }
    }

    public static void main(String[] args) {
        String[] commands;
        currPath = new File(".").getAbsolutePath();
        if (args.length == 0) {
            interactive = true;
            BufferedReader reader = null;
            InputStreamReader iReader = null;
            try {
                iReader = new InputStreamReader(System.in);
                reader = new BufferedReader(iReader);
                while (true) {
                    System.out.print("$ ");
                    String incomingData = reader.readLine();
                    commands = incomingData.split(";");
                    for (String command : commands) {
                        run(command);
                    }
                }
            } catch (Exception ex) {
                error(ex.getMessage());
                System.exit(1);
            } finally {
                tryClose(reader, "");
                tryClose(iReader, "");
            }
        } else {
            interactive = false;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                stringBuilder.append(args[i]);
                if (i < args.length - 1) {
                    stringBuilder.append(' ');
                }
            }
            commands = stringBuilder.toString().split(";");
            for(String command : commands) {
                run(command);
            }
        }
    }
}
