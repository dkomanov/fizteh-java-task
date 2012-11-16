/*
 * Shell.java
 * Oct 6, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.shell;

import java.io.*;
import java.util.*;

/*
 * Minimal shell
 */
public class Shell {
    /* Current directory */
    private File current;
    /* True if shell terminates on error */
    private boolean strict = false;

    /* Start the shell */
    public static void main(String[] args) {
        Shell jsh = new Shell();
        if (args.length == 0) {
            jsh.startInteractive();
        } else {
            jsh.strict = true;
            StringBuilder all = new StringBuilder();
            for (String cmd : args) {
                all.append(cmd);
                all.append(" ");
            }
            jsh.executeCommand(all.toString());
        }
    }

    /* Init current directory */
    public Shell() {
        current = new File(System.getProperty("user.dir"));
    }

    /* Print error message and exit if neccessary */
    private void panic(String message) {
        System.err.println(message);
        if (strict) {
            System.exit(1);
        }
    }

    /* Start reading commands from stdin */
    public void startInteractive() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("$ ");
            String cmd = null;
            try {
                cmd = input.readLine();
            } catch (IOException ioEx) {
                panic("console: i/o error: " + ioEx.getMessage());
            }
            if (cmd == null) {
                System.exit(0);
            }
            executeCommand(cmd);
        }
    }

    /* Execute commands separated by semicolons */
    public void executeCommand(String multicmd) {
        StringTokenizer tok = new StringTokenizer(multicmd, ";");
        while (tok.hasMoreTokens()) {
            String cmd = tok.nextToken().trim();
            executeSingleCommand(cmd);
        }
    } 

    /* Execute a single command */
    private void executeSingleCommand(String cmd) {
        ListIterator<String> parsed = splitCommand(cmd);
        if (!parsed.hasNext()) {
            return;
        }
        String operation = parsed.next();
        try {
            if (operation.equals("cd")) {
                cd(parsed);
            } else if (operation.equals("mkdir")) {
                mkdir(parsed);
            } else if (operation.equals("pwd")) {
                pwd(parsed);
            } else if (operation.equals("rm")) {
                rm(parsed);
            } else if (operation.equals("cp")) {
                cp(parsed);
            } else if (operation.equals("mv")) {
                mv(parsed);
            } else if (operation.equals("dir")) {
                dir(parsed);
            } else if (operation.equals("exit")) {
                exit(parsed);
            } else {
                panic("console: " + operation + ": wrong operation");
            }
        } catch (NoSuchElementException noArg) {
            panic("console: " + operation + ": too few arguments");
        } catch (Exception ex) {
            panic("console: internal error: " + ex.getMessage());
        }
    }

    /* True if too many arguments were provided */
    private boolean argOverflow(ListIterator<String> args, String operation) {
        if (args.hasNext()) {
            panic("console: " + operation + ": too many arguments");
            return true;
        }
        return false;
    }

    /* Utility to get file from its path */
    private File getFile(String dest) {
        File result = null;
        File undefined = new File(dest);
        if (undefined.isAbsolute()) {
            result = undefined;
        } else {
            result = new File(current, dest);
        }
        return result;
    }

    /* Shell commands */

    private void cd(ListIterator<String> args) {
        String filename = args.next();
        if (argOverflow(args, "cd")) {
            return;
        }
        File dest = getFile(filename);
        if (!dest.exists()) {
            panic("cd: '" + filename + "': no such directory");
        } else if (!dest.isDirectory()) {
            panic("cd: '" + filename + "': not a directory");
        } else {
            current = dest;
        }
        try {
            current = current.getCanonicalFile();
        } catch (IOException ioEx) {
            panic("console: i/o error: " + ioEx.getMessage());
        }
    }

    private void mkdir(ListIterator<String> args) {
        String filename = args.next();
        if (argOverflow(args, "mkdir")) {
            return;
        }
        File dest = getFile(filename);
        if (dest.exists()) {
            panic("mkdir: file already exists");
        } else if (!dest.mkdir()) {
            panic("mkdir: cannot create the folder");
        }
    }

    private void pwd(ListIterator<String> args) {
        if (argOverflow(args, "pwd")) {
            return;
        }
        try {
            System.out.println(current.getCanonicalPath());
        } catch (IOException ioEx) {
            panic("console: i/o error: " + ioEx.getMessage());
        }
    }

    /* Utility to remove file or folder recursively */
    private boolean recursiveRm(File file) {
        if (!file.exists()) {
            panic("rm: '" + file.getAbsolutePath() + "': no such file or directory");
            return false;
        }
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                if (!recursiveRm(child)) {
                    return false;
                }
            }
        }
        boolean ok = file.delete();
        if (!ok) {
            panic("rm: '" + file.getAbsolutePath() + "': cannot remove the file");
        }
        return ok;
    }

    private void rm(ListIterator<String> args) {
        String filename = args.next();
        if (argOverflow(args, "rm")) {
            return;
        }
        File dest = getFile(filename);
        try {
            if (current.getCanonicalPath().startsWith(dest.getCanonicalPath())) {
                panic("rm: cannot remove the root of current directory");
                return;
            }
        } catch (IOException ioEx) {
            panic("console: i/o error: " + ioEx.getMessage());
        }
        recursiveRm(dest);
    }

    /* Get a file to move or to copy to */
    private File getPair(File from, String filename2, String command) {
        if (!from.exists()) {
            panic(command + ": '" + from.getAbsolutePath() + "': no such file or directory");
            return null;
        }
        File to = getFile(filename2);
        if (to.exists() && !to.isDirectory() && from.isDirectory()) {
            panic(command + ": cannot override non-directory with directory");
            return null;
        }
        if (to.exists() && to.isDirectory()) {
            to = new File(to, from.getName());
        }
        return to;
    }

    private void cp(ListIterator<String> args) {
        String filename1 = args.next();
        String filename2 = args.next();
        if (argOverflow(args, "cp")) {
            return;
        }
        File from = getFile(filename1);
        File to = getPair(from, filename2, "cp");
        if (to != null) {
            FileReader read = null;
            FileWriter write = null;
            try {
                read = new FileReader(from);
                write = new FileWriter(to);
                char[] buffer = new char[4096];
                while(true) {
                    int size = read.read(buffer);
                    if (size == -1) {
                        break;
                    }
                    write.write(buffer, 0, size);
                }
            } catch (IOException ioEx) {
                panic("mv: i/o error");
            } finally {
                try {
                    read.close();
                    write.close();
                } catch (Exception ex) {}
            }
        }
    }

    private void mv(ListIterator<String> args) {
        String filename1 = args.next();
        String filename2 = args.next();
        if (argOverflow(args, "mv")) {
            return;
        }
        File from = getFile(filename1);
        File to = getPair(from, filename2, "mv");
        if (to != null) {
            from.renameTo(to);
        }
    }

    private void dir(ListIterator<String> args) {
        if (argOverflow(args, "dir")) {
            return;
        }
        for (String child : current.list()) {
            System.out.println(child);
        }
    }

    private void exit(ListIterator<String> args) {
        if (argOverflow(args, "exit")) {
            return;
        }
        System.exit(0);
    }

    /* Split a command into words */
    private static ListIterator<String> splitCommand(String cmd) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(cmd);
        while (tok.hasMoreTokens()) {
            result.add(tok.nextToken());
        }
        return result.listIterator();
    }
}