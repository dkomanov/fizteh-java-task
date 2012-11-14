package ru.fizteh.fivt.students.konstantinPavlov.shell;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class Shell {

    static File pwd = new File("").getAbsoluteFile();
    static boolean isInteractiveMode = false;

    static void executeCommand(String command) throws Exception {
        String[] commands = command.split("[\\s]+");
        String action = commands[0];

        switch (action) {
        case "cd": {
            if (commands.length != 2) {
                System.err.println("cd: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            File newPath = getAbsolute(commands[1]);
            if (newPath.isDirectory()) {
                pwd = newPath;
            } else {
                System.err.println("cd: \'" + commands[1]
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            return;
        }

        case "mkdir": {
            if (commands.length != 2) {
                System.err.println("mkdir: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            File newPath = getAbsolute(commands[1]);
            if (!(newPath.exists() && newPath.isDirectory())
                    && !newPath.mkdir()) {
                System.err.println("mkdir: cannot create directory \'"
                        + commands[1] + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
            }
            return;
        }

        case "pwd": {
            if (commands.length != 1) {
                System.err.println("pwd: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            System.out.println(pwd.getCanonicalPath());
            return;
        }

        case "rm": {
            if (commands.length != 2) {
                System.err.println("rm: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            File pathToDelete = getAbsolute(commands[1]);
            if (pathToDelete.exists()) {
                try {
                    deletePath(pathToDelete);
                } catch (Exception e) {
                    System.err.println("rm:" + e.getMessage());
                    if (!isInteractiveMode) {
                        System.exit(1);
                    }
                }
            } else {
                System.err.println("rm: cannot remove \'" + commands[1]
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            return;
        }

        case "cp": {
            if (commands.length != 3) {
                System.err.println("cp: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            File src = getAbsolute(commands[1]);
            File dst = getAbsolute(commands[2]);
            if (!src.exists()) {
                System.err.println("cp: cannot stat \'" + commands[1]
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            try {
                copyPath(src, dst);
            } catch (Exception e) {
                System.err.println("cp:" + e.getMessage());
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            return;
        }

        case "mv": {
            if (commands.length != 3) {
                System.err.println("mv: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            File src = getAbsolute(commands[1]);
            File dst = getAbsolute(commands[2]);
            if (!src.exists()) {
                System.err.println("mv: cannot stat \'" + commands[1]
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
            }
            try {
                copyPath(src, dst);
                deletePath(src);
            } catch (Exception e) {
                System.err.println("mv: " + e.getMessage());
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            return;
        }

        case "dir": {
            if (commands.length != 1) {
                System.err.println("dir: invalid number of arguments");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
            for (String element : pwd.list()) {
                System.out.println(element);
            }
            return;
        }

        case "exit": {
            System.exit(0);
        }

        default:
            System.err.println("error: invalid command '" + action + "'");
            if (!isInteractiveMode) {
                System.exit(1);
            }
            return;
        }
    }

    static void runNonInteractiveMode(String[] args) throws Exception {
        StringBuilder input = new StringBuilder();
        for (String arg : args) {
            input.append(arg + " ");
        }
        String commands = input.toString();
        String[] command = commands.split("[\\s]*[;][\\s]*");
        for (String element : command) {
            try {
                executeCommand(element);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    static void runInteractiveMode() throws Exception {
        BufferedReader reader = null;
        try {
            while (true) {
                System.out.print("$ ");
                reader = new BufferedReader(new InputStreamReader(System.in));
                String commands = reader.readLine();
                String[] command = commands.split("[\\s]*[;][\\s]*");
                for (String element : command) {
                    executeCommand(element);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closer(reader);
        }
    }

    static void closer(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception expt) {
            }
        }
    }

    public static File getAbsolute(String badPath) {
        File f = new File(badPath);
        if (!f.isAbsolute()) {
            f = new File(pwd + File.separator + badPath);
        }
        return f;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            isInteractiveMode = true;
            runInteractiveMode();
        } else {
            runNonInteractiveMode(args);
        }
    }

    static void copyPath(File src, File dst) throws Exception {
        if (src.isFile()) {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dst);
                int nLength;
                byte[] buf = new byte[8000];
                while (true) {
                    nLength = in.read(buf);
                    if (nLength < 0) {
                        break;
                    }
                    out.write(buf, 0, nLength);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            } finally {
                closer(in);
                closer(out);
            }
        } else {
            if (!dst.mkdir()) {
                System.err.println("cannot create directory \'" + dst
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }

            for (String s : src.list()) {
                copyPath(
                        getAbsolute(src.getAbsoluteFile() + File.separator + s),
                        getAbsolute(dst.getAbsoluteFile() + File.separator + s));
            }
        }
    }

    static void deletePath(File newFile) throws Exception {
        if (newFile.isFile()) {
            if (!newFile.delete()) {
                System.err.println("cannot remove \'" + newFile
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
        } else {
            for (String s : newFile.list()) {
                deletePath(getAbsolute(newFile.getAbsolutePath()
                        + File.separator + s));
            }
            if (!newFile.delete()) {
                System.err.println("cannot remove \'" + newFile
                        + "\': No such file or directory");
                if (!isInteractiveMode) {
                    System.exit(1);
                }
                return;
            }
        }
    }

}