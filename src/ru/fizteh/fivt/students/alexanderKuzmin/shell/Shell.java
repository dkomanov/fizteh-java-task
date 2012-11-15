package ru.fizteh.fivt.students.alexanderKuzmin.shell;

/**
 * @author Alexander Kuzmin
 *      group 196
 *      Class Shell
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

public class Shell {

    private static String currentPath = System.getProperty("user.dir");

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            shellInteractiveExecutor();
        } else {
            shellPackageExecutor(args);
        }
    }

    public static void shellInteractiveExecutor() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        while (true) {
            System.out.print("$ ");
            String command = null;
            try {
                command = reader.readLine();
            } catch (IOException e) {
                Closers.printErrAndNoExit(e.getMessage());
            }
            if (command == null) {
                goodExit();
            }
            String[] commands = command.toString().split(";");
            for (String ex : commands) {
                try {
                    commandsExecutor(ex);
                } catch (Exception e) {
                    Closers.printErrAndNoExit(e.getMessage());
                }
            }
        }
    }

    public static void shellPackageExecutor(String[] args) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            line.append(args[i]).append(" ");
        }
        String[] commands = line.toString().split(";");
        for (String ex : commands) {
            try {
                commandsExecutor(ex);
            } catch (Exception e) {
                Closers.printErrAndExit(e.getMessage());
            }
        }
    }

    private static void commandsExecutor(String readLine) throws Exception {
        String[] commands = readLine.split("[\\s]+");
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].length() != 0) {
                if (commands[i].equals("cd")) {
                    if (commands.length == i + 2) {
                        changeDirectory(commands[i + 1]);
                        break;
                    } else {
                        throw new Exception(
                                "Invalid input. Use: cd <absolute path|relative path>");
                    }
                } else if (commands[i].equals("mkdir")) {
                    if (commands.length == i + 2) {
                        makeDirectory(commands[i + 1]);
                        break;
                    } else {
                        throw new Exception(
                                "Invalid input. Use: mkdir <dirname>");
                    }
                } else if (commands[i].equals("pwd")) {
                    if (commands.length == i + 1) {
                        printWorkingDirectory();
                        break;
                    } else {
                        throw new Exception("Invalid input. Use: pwd");
                    }
                } else if (commands[i].equals("rm")) {
                    if (commands.length == i + 2) {
                        remove(commands[i + 1]);
                        break;
                    } else {
                        throw new Exception("Invalid input. Use: rm <file|dir>");
                    }
                } else if (commands[i].equals("cp")) {
                    if (commands.length == i + 3) {
                        copy(commands[i + 1], commands[i + 2]);
                        break;
                    } else {
                        throw new Exception(
                                "Invalid input. Use: cp <source> <destination>");
                    }
                } else if (commands[i].equals("mv")) {
                    if (commands.length == i + 3) {
                        move(commands[i + 1], commands[i + 2]);
                        break;
                    } else {
                        throw new Exception(
                                "Invalid input. Use: mv <source> <destination>");
                    }
                } else if (commands[i].equals("dir")) {
                    if (commands.length == i + 1) {
                        printDirectory();
                        break;
                    } else {
                        throw new Exception("Invalid input. Use: dir");
                    }
                } else if (commands[i].equals("exit")) {
                    if (commands.length == i + 1) {
                        goodExit();
                        break;
                    } else {
                        throw new Exception("Invalid input. Use: exit");
                    }
                } else {
                    throw new Exception("Invalid input (invalid command).");
                }
            }
        }
    }

    private static void goodExit() {
        System.exit(0);
    }

    private static void printDirectory() {
        String[] files = new File(currentPath).list();
        StringBuilder sb = new StringBuilder();
        for (String fileName : files) {
            sb.append(fileName).append("\n");
        }
        System.out.print(sb);
    }

    private static void move(String source, String destination)
            throws Exception {
        File destFile = new File(currentPath + File.separator + destination);
        if (!destFile.exists()) {
            new File(currentPath + File.separator + source).renameTo(destFile);
        } else {
            copy(source, destination);
            remove(source);
        }
    }

    private static void copy(String source, String destination)
            throws Exception {
        File sourceFile = new File(source).getAbsoluteFile();
        if (!sourceFile.exists()) {
            sourceFile = new File(currentPath + File.separator + source)
                    .getAbsoluteFile();
        }
        File destinationFile = new File(destination).getAbsoluteFile();
        if (!destinationFile.exists()) {
            destinationFile = new File(currentPath + File.separator
                    + destination).getAbsoluteFile();
        }
        if (sourceFile.exists()) {
            if (destinationFile.exists()) {
                if (destinationFile.isDirectory()) {
                    if (sourceFile.isDirectory()) {
                        copyDir(sourceFile,
                                new File(destinationFile.getAbsolutePath()
                                        + File.separator + sourceFile.getName()));
                    } else if (sourceFile.isFile()) {
                        copyFile(
                                sourceFile,
                                new File(destinationFile.getAbsolutePath()
                                        + File.separator + sourceFile.getName()));
                    } else {
                        throw new Exception("cp: \'" + destination
                                + "\': can't copy to this file, it exists.");
                    }
                } else {
                    throw new Exception("cp: \'" + destination
                            + "\': can't copy this, it's a strange files.");
                }
            } else if (sourceFile.isFile()) {
                copyFile(sourceFile,
                        new File(destinationFile.getAbsolutePath()));
            } else {
                throw new Exception("cp: \'" + destination
                        + "\': No such file or directory.");
            }
        } else {
            throw new Exception("cp: \'" + source
                    + "\': No such file or directory.");
        }
    }

    private static void copyFile(File sourceFile, File destinationFile)
            throws Exception {
        FileInputStream iStream = null;
        FileOutputStream oStream = null;
        try {
            iStream = new FileInputStream(sourceFile);
            oStream = new FileOutputStream(destinationFile);
            int nLength;
            byte[] buf = new byte[8192];
            while (true) {
                nLength = iStream.read(buf);
                if (nLength < 0) {
                    break;
                }
                oStream.write(buf, 0, nLength);
            }
        } finally {
            Closers.closeStream(iStream);
            Closers.closeStream(oStream);
        }
    }

    private static void copyDir(File sourceFile, File destinationFile)
            throws Exception {
        if (!destinationFile.mkdir()) {
            throw new Exception("can't create directory \'"
                    + destinationFile.getCanonicalPath() + "\'.");
        }
        String[] files = sourceFile.list();
        for (String cur : files) {
            if (new File(sourceFile.getAbsolutePath() + File.separator + cur)
                    .isFile()) {
                copyFile(new File(sourceFile.getAbsolutePath() + File.separator
                        + cur), new File(destinationFile.getAbsolutePath()
                        + File.separator + cur));
            } else if (new File(sourceFile.getAbsolutePath() + File.separator
                    + cur).isDirectory()) {
                copyDir(new File(sourceFile.getAbsolutePath() + File.separator
                        + cur), new File(destinationFile.getAbsolutePath()
                        + File.separator + cur));
            } else {
                throw new Exception("\'" + sourceFile
                        + "\': can't copy this, it's a strange file.");
            }
        }
    }

    private static void remove(String file) throws Exception {
        File cur = new File(currentPath + File.separator + file);
        if (cur.exists()) {
            deleteObject(cur);
        } else {
            throw new Exception("rm \'" + file
                    + "\': No such file or directory");
        }
    }

    private static void deleteObject(File file) throws Exception {
        if (file.isFile()) {
            if (!file.delete()) {
                throw new Exception("\'" + file.getName()
                        + "\': can't remove the file.");
            }
        } else {
            String[] files = file.list();
            for (String cur : files) {
                deleteObject(new File(file.getAbsolutePath() + File.separator
                        + cur));
            }
            if (!file.delete()) {
                throw new Exception("\'" + file.getName()
                        + "\': can't remove the file.");
            }
        }
    }

    private static void printWorkingDirectory() {
        try {
            System.out.println((new File(currentPath).getCanonicalPath()));
        } catch (IOException e) {
            Closers.printErrAndNoExit(e.getMessage());
        }
    }

    private static void makeDirectory(String dirName) throws Exception {
        File cur = new File(currentPath + File.separator + dirName);
        if (!cur.mkdir()) {
            throw new Exception("mkdir: can't create directory \'" + dirName
                    + "\': such directory already exist.");
        }
    }

    private static void changeDirectory(String path) throws Exception {
        File newFile = new File(currentPath + File.separator + path)
                .getAbsoluteFile();
        File newShortFile = new File(path).getAbsoluteFile();
        if (newShortFile.exists() && newShortFile.isDirectory()
                && !(path.charAt(0) == '.')) {
            currentPath = newShortFile.getAbsolutePath();
        } else if (newFile.exists() && newFile.isDirectory()) {
            currentPath = newFile.getAbsolutePath();
        } else {
            throw new Exception("cd: \'" + path
                    + "\': No such file or directory");
        }
    }
}