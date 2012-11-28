package ru.fizteh.fivt.students.tolyapro.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import ru.fizteh.fivt.students.tolyapro.wordCounter.BufferCloser;

public class ShellCommandsExecutor {

    public static String separator = File.separator;

    public static void showDir(String currPath) {
        String[] children = (new File(currPath).list());
        for (String child : children) {
            System.out.println(child);
        }
    }

    public static void printWorkingDirectory(String currPath) throws Exception {
        System.out.println(new File(currPath).getCanonicalPath());
    }

    public static String changeDirectory(String newDir, String currPath)
            throws Exception {
        File relNewDir = new File(currPath + separator + newDir); // relative
        File absNewDir = new File(newDir); // absolute path
        if (absNewDir.exists() && absNewDir.isDirectory()
                && absNewDir.isAbsolute()) {
            currPath = newDir;
        } else {
            if (relNewDir.exists() && relNewDir.isDirectory()) {
                currPath = currPath + separator + newDir;
            } else {
                throw new Exception("No such directory: " + newDir);
            }
        }
        return currPath;
    }

    public static void makeDirectory(String currPath, String newDir)
            throws Exception {
        boolean success = new File(currPath + separator + newDir).mkdir();
        if (!success) {
            throw new Exception("Error creating new directtory: " + newDir);
        }
    }

    public static void removeDirectory(String targetDir, String currPath)
            throws Exception {
        File relDir = new File(currPath + separator + targetDir);
        File absDir = new File(targetDir);
        if (relDir.exists()) {
            if (relDir.isDirectory()) {
                String[] children = relDir.list();
                for (String child : children) {
                    removeDirectory(child, currPath + separator + targetDir);
                }
            }
            if (!relDir.delete()) {
                throw new Exception("Can't delete: " + targetDir);
            }

        } else {
            if (absDir.exists()) {
                if (absDir.isDirectory()) {
                    String[] children = absDir.list();
                    for (String child : children) {
                        removeDirectory(targetDir + separator + child, currPath);
                    }
                }
                if (!absDir.delete()) {
                    throw new Exception("Can't delete: " + targetDir);
                }
            } else {
                throw new Exception("No such directory: " + targetDir);
            }
        }
    }

    public static void copy(String source, String destination, String currPath)
            throws Exception {
        File target = new File(currPath + separator + source);
        if (!target.exists()) {
            target = new File(source);
            if (!target.exists()) {
                throw new Exception("No such file or directory:" + source);
            }
        }
        if (target.isDirectory()) {
            File tmp = new File(currPath + separator + destination + separator
                    + source);
            if (!tmp.mkdir()) {
                throw new Exception("Can't copy to: " + currPath + separator
                        + destination + separator + source);
            }
            String[] children = target.list();
            for (String child : children) {
                copy(source + separator + child, destination, currPath);
            }
        } else {
            File destFile;
            if (new File(currPath + separator + destination).exists()) {
                destFile = new File(currPath + separator + destination
                        + separator + source);
            } else {
                destFile = new File(currPath + separator + destination);
            }
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(target);
                out = new FileOutputStream(destFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            } finally {
                BufferCloser.close(in);
                BufferCloser.close(out);
            }

        }
    }

    public static void move(String from, String to, String currPath)
            throws Exception {
        if (!(new File(to).exists())
                || !(new File(currPath + separator + to).exists())) {
            File tmp = new File(currPath + separator + to);
            tmp.mkdir();
        }
        copy(from, to, currPath);
        removeDirectory(from, currPath);
    }

    public String execute(String command, String currPath) throws Exception {
        String[] tokens = command.split("\\s");
        if (tokens[0].equals("dir")) {
            if (tokens.length == 1) {
                showDir(currPath);
            } else {
                throw new Exception("Usage: dir");
            }
        } else if (tokens[0].equals("cd")) {
            if (tokens.length == 2) {
                currPath = changeDirectory(tokens[1], currPath);
            } else {
                throw new Exception("Usage: cd <absolute path|relative path>");
            }
        } else if (tokens[0].equals("pwd")) {
            if (tokens.length == 1) {
                printWorkingDirectory(currPath);
            } else {
                throw new Exception("Usage: pwd");
            }
        } else if (tokens[0].equals("rm")) {
            if (tokens.length == 2) {
                removeDirectory(tokens[1], currPath);
            } else {
                throw new Exception("Usage: rm <file|dir>");
            }
        } else if (tokens[0].equals("cp")) {
            if (tokens.length == 3) {
                copy(tokens[1], tokens[2], currPath);
            } else {
                throw new Exception("Usage: cp <source> <destination>");
            }
        } else if (tokens[0].equals("mkdir")) {
            if (tokens.length == 2) {
                makeDirectory(currPath, tokens[1]);
            } else {
                throw new Exception("Usage: mkdir <dirname>");
            }
        } else if (tokens[0].equals("mv")) {
            if (tokens.length == 3) {
                move(tokens[1], tokens[2], currPath);
            } else {
                throw new Exception("Usage: mv <source> <destination>");
            }
        } else if (tokens[0].equals("exit")) {
            System.exit(0);
        } else {
            throw new Exception("Unknown command: " + tokens[0]);
        }
        return currPath;
    }
}
