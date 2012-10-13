/*
 * Shell.java
 * Oct 6, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.shell;

import java.io.*;
import java.util.*;
import java.nio.file.*;

/*
 * Minimal shell
 */
public class Shell {
    /* Current directory */
    private File current;

    /* Start the shell */
    public static void main(String[] args) {
        Shell jsh = new Shell();
        if (args.length == 0) {
            jsh.startInteractive();
        } else {
            for(String cmd : args) {
                jsh.executeCommand(cmd);
            }
        }
    }

    /* Init current directory */
    public Shell() {
        current = new File(System.getProperty("user.dir"));
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
                System.out.println("console: i/o error: " + ioEx.getMessage());
                System.exit(1);
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
        String operation = parsed.next();
        try {
            if (operation.equals("cd")) {
                cd(parsed);
            } else if (operation.equals("mkdir")) {
                mkdir(parsed);
            } else if (operation.equals("pwd")) {
                pwd();
            } else if (operation.equals("rm")) {
                rm(parsed);
            } else if (operation.equals("cp")) {
                cp(parsed);
            } else if (operation.equals("mv")) {
                mv(parsed);
            } else if (operation.equals("dir")) {
                dir(parsed);
            } else if (operation.equals("exit")) {
                exit();
            } else {
                System.out.println("console: " + operation + ": wrong operation");
                while (parsed.hasNext()) {
                    parsed.next();
                }
            }
            if (parsed.hasNext()) {
                System.out.println("console: " + operation + ": too many arguments");
            }
        } catch (NoSuchElementException noArg) {
            System.out.println("console: " + operation + ": too few arguments");
        }
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
        File dest = getFile(filename);
        if (!dest.exists()) {
            System.out.println("cd: '" + filename + "': no such directory");
        } else if (!dest.isDirectory()) {
            System.out.println("cd: '" + filename + "': not a directory");
        } else {
            current = dest;
        }
        try {
            current = current.getCanonicalFile();
        } catch (IOException ioEx) {
            System.out.println("console: i/o error: " + ioEx.getMessage());
            System.exit(1);
        }
    }

    private void mkdir(ListIterator<String> args) {
        String filename = args.next();
        File dest = getFile(filename);
        if (!dest.mkdir()) {
            System.out.println("mkdir: cannot create the folder");
        }
    }

    private void pwd() {
        try {
            System.out.println(current.getCanonicalPath());
        } catch (IOException ioEx) {
            System.out.println("console: i/o error: " + ioEx.getMessage());
            System.exit(1);
        }
    }

    private void rm(ListIterator<String> args) {
        String filename = args.next();
        File dest = getFile(filename);
        if (!dest.exists()) {
            System.out.println("rm: '" + filename + "': no such file or directory");
            return;
        }
        if (dest.isDirectory()) {
            for (File child : dest.listFiles()) {
                if (!child.delete()) {
                    System.out.println("rm: '" + filename + File.separator +
                        child.getName() + "': cannot remove the file");
                    return;
                }
            }
        }
        if (!dest.delete()) {
            System.out.println("rm: '" + filename + "': cannot remove the file");
        }
    }

    /* Get a file to move or copy to */
    private File getPair(File from, String filename2, String command) {
        if (!from.exists()) {
            System.out.println(command + ": '" + from.getAbsolutePath() + "': no such file or directory");
            return null;
        }
        File to = getFile(filename2);
        if (to.exists() && !to.isDirectory() && from.isDirectory()) {
            System.out.println(command + ": cannot override non-directory with directory");
            return null;
        }
        if (to.exists() && to.isDirectory()) {
            to = new File(to, from.getName());
        }
        return to;
    }

    private void cp(ListIterator<String> args) {
        String filename1 = args.next();
        File from = getFile(filename1);
        String filename2 = args.next();
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
                System.err.println("mv: i/o error");
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
        File from = getFile(filename1);
        String filename2 = args.next();
        File to = getPair(from, filename2, "mv");
        if (to != null) {
            from.renameTo(to);
        }
    }

    private void dir(ListIterator<String> args) {
        for (String child : current.list()) {
            System.out.println(child);
        }
    }

    private void exit() {
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