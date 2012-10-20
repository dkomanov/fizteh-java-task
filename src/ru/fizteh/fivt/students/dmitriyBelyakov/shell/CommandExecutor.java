package ru.fizteh.fivt.students.dmitriyBelyakov.shell;

import java.io.*;
import java.util.ArrayList;

import ru.fizteh.fivt.students.dmitriyBelyakov.shell.*;

public class CommandExecutor {
    private static final String fileSeparator = System.getProperty("file.separator");

    public static void executeCommands(String cmds, ArrayList<String> curDirPath, boolean interactive) {
        String[] commands = cmds.split("\\s*;\\s*");
        for (String cmd : commands) {
            cmd = cmd.replaceAll("\\(^\\s+\\)|\\(\\s+$\\)", "");
            try {
                String res = executeCommand(cmd, curDirPath);
                if (!res.equals("")) {
                    System.out.println(res);
                }
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    System.out.println(cmd.split("\\s+")[0] + ": " + e.getMessage());
                } else {
                    System.out.println(cmd.split("\\s+")[0] + ": unknown error");
                }
                if (!interactive) {
                    System.exit(1);
                }
            }
        }
    }

    static String executeCommand(String cmd, ArrayList<String> curDirPath) throws Exception {
        String[] command = cmd.split("\\s+");
        String result = new String();
        if (command[0].equals("exit")) {
            System.exit(0);
        } else if (command[0].equals("pwd")) {
            result = curDirPath.get(0);
        } else if (command[0].equals("mkdir")) {
            if (command.length != 2) {
                throw new RuntimeException("use: mkdir <dirname>.");
            }
            mkDir(curDirPath.get(0), command[1]);
        } else if (command[0].equals("cd")) {
            if (command.length != 2) {
                throw new RuntimeException("use: cd <absolute path|relative path>.");
            }
            File newCurDir;
            if ((newCurDir = new File(curDirPath.get(0) + fileSeparator + command[1])).exists()) {
                if (!newCurDir.isDirectory()) {
                    throw new RuntimeException("'" + curDirPath.get(0) + fileSeparator + command[1]
                            + "': isn't a directory.");
                }
                curDirPath.add(newCurDir.getCanonicalPath());
                curDirPath.remove(0);
            } else if ((newCurDir = new File(command[1])).exists()) {
                if (!newCurDir.isDirectory()) {
                    throw new RuntimeException("'" + curDirPath.get(0) + fileSeparator + command[1]
                            + "': isn't a directory.");
                }
                curDirPath.remove(0);
                curDirPath.add(new File(command[1]).getCanonicalPath());
            } else {
                throw new RuntimeException(command[1] + ": no such file or directory.");
            }
        } else if (command[0].equals("rm")) {
            if (command.length != 2) {
                throw new RuntimeException("use: rm <path>.");
            }
            remove(curDirPath.get(0), command[1]);
        } else if (command[0].equals("dir")) {
            File file = new File(curDirPath.get(0));
            String[] children = file.list();
            StringBuilder builder = new StringBuilder();
            for (String s : children) {
                builder.append(s);
                if (s != children[children.length - 1]) {
                    builder.append(System.getProperty("line.separator"));
                }
            }
            result = builder.toString();
        } else if (command[0].equals("cp")) {
            if (command.length != 3) {
                throw new RuntimeException("use: cp <source> <destination>.");
            }
            copy(curDirPath.get(0), command[1], command[2]);
        } else if (command[0].equals("mv")) {
            if (command.length != 3) {
                throw new RuntimeException("use: mv <source> <destination>.");
            }
            move(curDirPath.get(0), command[1], command[2]);
        } else {
            throw new RuntimeException("unknown command.");
        }
        return result;
    }

    static void mkDir(String curDirPath, String dirName) {
        String newDir = curDirPath + fileSeparator + dirName;
        if (!(new File(newDir).mkdir())) {
            throw new RuntimeException("cannot create directory '" + dirName + "'.");
        }
    }

    static void remove(String curDirPath, String name) throws Exception {
        File delFile;
        if ((delFile = new File(curDirPath + fileSeparator + name)).exists()) {
            if (delFile.isDirectory()) {
                String[] children = delFile.list();
                for (String s : children) {
                    remove(curDirPath + fileSeparator + name, s);
                }
            }
            if (!delFile.delete()) {
                throw new RuntimeException("cannot remove file or directory");
            }
        } else if ((delFile = new File(name)).exists()) {
            if (delFile.isDirectory()) {
                String[] children = delFile.list();
                for (String s : children) {
                    remove(curDirPath, name + fileSeparator + s);
                }
            }
            if (!delFile.delete()) {
                throw new RuntimeException("cannot remove file or directory");
            }
        } else {
            throw new RuntimeException(name + ": no such file or directory");
        }
    }

    static void copy(String curDirPath, String name, String to) {
        File file;
        file = new File(curDirPath + fileSeparator + name);
        if (!file.exists()) {
            file = new File(name);
        }
        if (!file.exists()) {
            throw new RuntimeException(name + ": cannot find file or directory.");
        }
        if (file.isDirectory()) {
            File dirTo = new File(curDirPath + fileSeparator + to + fileSeparator + name);
            if (!dirTo.mkdir()) {
                throw new RuntimeException(to + ": cannot create directory.");
            }
            String[] children = file.list();
            for (String s : children) {
                copy(curDirPath, name + fileSeparator + s, to);
            }
        } else {
            File fileTo = new File(curDirPath + fileSeparator + to);
            if (fileTo.exists() && fileTo.isDirectory()) {
                fileTo = new File(curDirPath + fileSeparator + to + fileSeparator + name);
            }
            FileInputStream reader = null;
            FileOutputStream writer = null;
            try {
                reader = new FileInputStream(file);
                writer = new FileOutputStream(fileTo);
                byte[] buf = new byte[1024];
                int bytesLength;
                while ((bytesLength = reader.read(buf)) >= 0) {
                    writer.write(buf, 0, bytesLength);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                IoUtils.close(reader);
                IoUtils.close(writer);
            }
        }
    }

    static void move(String curDirPath, String name, String to) throws Exception {
        copy(curDirPath, name, to);
        remove(curDirPath, name);
    }
}
