/*
 * Shell.java
 * Oct 6, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.shell;

import java.io.*;
import java.util.*;

public class Shell {
    private File current;

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

    public Shell() {
        current = new File(System.getProperty("user.dir"));
    }

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

    public void executeCommand(String multicmd) {
        StringTokenizer tok = new StringTokenizer(multicmd, ";");
        while (tok.hasMoreTokens()) {
            String cmd = tok.nextToken().trim();
            executeSingleCommand(cmd);
        }
    } 

    private void executeSingleCommand(String cmd) {
        ListIterator<String> parsed = splitCommand(cmd);
        String operation = parsed.next();
        if (operation.equals("cd")) {
            cd(parsed);
        } else if (operation.equals("mkdir")) {
            mkdir(parsed);
        } else if (operation.equals("pwd")) {
            pwd();
        } else if (operation.equals("rm")) {
            rm(parsed);
        } else if (operation.equals("cp")) {

        } else if (operation.equals("mv")) {

        } else if (operation.equals("dir")) {
            dir(parsed);
        } else if (operation.equals("exit")) {
            exit();
        } else {

        }
    }

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

    private void dir(ListIterator<String> args) {
        for (String child : current.list()) {
            System.out.println(child);
        }
    }

    private void exit() {
        System.exit(0);
    }

    private static ListIterator<String> splitCommand(String cmd) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(cmd);
        while (tok.hasMoreTokens()) {
            result.add(tok.nextToken());
        }
        return result.listIterator();
    }
}