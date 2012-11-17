package ru.fizteh.fivt.students.nikitaAntonov.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.fizteh.fivt.students.nikitaAntonov.utils.Utils;
import ru.fizteh.fivt.students.nikitaAntonov.utils.ConsoleApp;
import ru.fizteh.fivt.students.nikitaAntonov.utils.ConsoleAppException;
import ru.fizteh.fivt.students.nikitaAntonov.utils.IncorrectUsageException;

/**
 * Класс имитации shell`а
 * 
 * @author Антонов Никита
 */
class Shell extends ConsoleApp {

    private File workingDir;

    public Shell() throws IOException {
        workingDir = new File(".").getCanonicalFile();
    }

    public static void main(String args[]) {
        Shell shell = null;

        try {
            shell = new Shell();
        } catch (IOException e) {
            System.err.println("Can't get canonical path of working dir");
            System.exit(1);
        }

        if (shell != null)
            shell.run(args);
    }

    @Override
    protected void printPrompt() {
        System.out.print("$ ");
    }

    @Override
    protected boolean processLine(String s) throws ConsoleAppException {
        s = s.trim();
        String expressions[] = s.split("\\s*;\\s*");

        for (String expr : expressions) {
            try {
                if (execute(expr)) {
                    return true;
                }
            } catch (ConsoleAppException e) {
                System.err.println(e.getMessage());
                throw e;
            } catch (IOException e) {
                System.err.println("IO Error: " + e.getMessage());
                throw new ConsoleAppException(e);
            }
        }

        return false;
    }

    public boolean execute(String str) throws ConsoleAppException, IOException {
        str = str.trim();
        String parts[] = str.split("\\s+");

        if (parts.length == 0)
            return false;

        switch (parts[0].toLowerCase()) {
        case "cd":
            doCd(parts);
            break;
        case "mkdir":
            doMkdir(parts);
            break;
        case "pwd":
            doPwd(parts);
            break;
        case "rm":
            doRm(parts);
            break;
        case "cp":
            doCp(parts);
            break;
        case "mv":
            doMv(parts);
            break;
        case "dir":
            doDir(parts);
            break;
        case "exit":
            return true;
        default:
            throw new ConsoleAppException("Unknown command " + parts[0]);
        }

        return false;
    }

    private File getFileByName(String filename) throws IOException {
        File result = new File(filename);

        if (!result.isAbsolute()) {
            result = new File(workingDir, filename);
        }

        return result.getCanonicalFile();
    }

    private void checkForParams(String params[], String usage, int needed)
            throws ConsoleAppException {
        if (params.length != needed) {
            throw new IncorrectUsageException(usage);
        }
    }

    private void doCd(String parts[]) throws ConsoleAppException, IOException {
        String usage = "cd <absolute path|relative path>";

        checkForParams(parts, usage, 2);

        File newDir = getFileByName(parts[1]);

        if (!newDir.exists()) {
            throw new ConsoleAppException("cd: " + parts[1]
                    + ": No such file or directory");
        }

        if (!newDir.isDirectory()) {
            throw new ConsoleAppException("cd: " + parts[1]
                    + ": Not a directory");
        }

        workingDir = newDir;
    }

    private void doMkdir(String parts[]) throws ConsoleAppException,
            IOException {
        String usage = "mkdir <dirname>";
        checkForParams(parts, usage, 2);

        File newDir = getFileByName(parts[1]);

        if (newDir.exists()) {
            throw new ConsoleAppException("mkdir: cannot create directory "
                    + parts[1] + ": File exists");
        }

        if (!newDir.mkdir()) {
            throw new ConsoleAppException("mkdir: cannot create directory "
                    + parts[1] + ": No such file or directory");
        }
    }

    private void doPwd(String parts[]) throws ConsoleAppException, IOException {
        String usage = "pwd (without any params)";
        checkForParams(parts, usage, 1);

        System.out.println(workingDir.getCanonicalPath());
    }

    private void doRm(String parts[]) throws ConsoleAppException, IOException {
        String usage = "rm <file|dir>";
        checkForParams(parts, usage, 2);

        File subject = getFileByName(parts[1]);
        if (!subject.exists()) {
            throw new ConsoleAppException("rm: cannot remove " + parts[1]
                    + ": No such file or directory");
        }

        deleteFile(subject, "rm");
    }

    private void deleteFile(File subject, String producer)
            throws ConsoleAppException, IOException {
        if (subject.isDirectory()) {
            for (String filename : subject.list()) {
                deleteFile(new File(subject, filename).getCanonicalFile(),
                        producer);
            }
        }

        if (!subject.delete()) {
            throw new ConsoleAppException(producer + ": cannot remove "
                    + subject.getPath() + ": Unknown error");
        }
    }

    private void doCp(String parts[]) throws ConsoleAppException, IOException {
        String usage = "cp <source> <destination>";
        checkForParams(parts, usage, 3);

        startCopying("cp", parts[1], parts[2]);
    }

    private void doMv(String parts[]) throws ConsoleAppException, IOException {
        String usage = "mv <source> <destination>";
        checkForParams(parts, usage, 3);

        startCopying("mv", parts[1], parts[2]);
        deleteFile(getFileByName(parts[1]), "mv");
    }

    private void startCopying(String producer, String source, String destination)
            throws ConsoleAppException, IOException {
        destination = transformCopyDst(source, destination);
        copy(producer, source, destination);
    }

    private String transformCopyDst(String source, String destination)
            throws IOException {
        File src = getFileByName(source);
        File dst = getFileByName(destination);

        if (src.equals(dst)) {
            return destination;
        }

        if (dst.exists() && dst.isDirectory()) {
            return dst.getAbsolutePath() + File.separator + src.getName();
        }

        return destination;
    }

    private void copy(String producer, String srcFilename, String dstFilename)
            throws ConsoleAppException, IOException {
        File source = getFileByName(srcFilename);
        File destination = getFileByName(dstFilename);

        if (!source.exists()) {
            throw new ConsoleAppException(producer + ": cannot stat "
                    + srcFilename + ": No such file or directory");
        }

        if (source.isDirectory()) {
            copyDirs(producer, source, destination);
        } else {
            copyFiles(producer, source, destination);
        }
    }

    private void copyDirs(String producer, File source, File destination)
            throws ConsoleAppException, IOException {
        if (!destination.mkdir()) {
            if (!destination.exists()) {
                throw new ConsoleAppException(producer
                        + ": cannot create directory "
                        + destination.getAbsolutePath()
                        + ": No such file or directory");
            } else if (source.isDirectory()) {
                System.err.println("Warning! Directories " + source.getName()
                        + " and " + destination.getName() + " will be merged");
            }
        }

        for (String filename : source.list()) {
            copy(producer, new File(source, filename).getCanonicalPath(),
                    new File(source, filename).getCanonicalPath());
        }
    }

    private void copyFiles(String producer, File source, File destination)
            throws ConsoleAppException {
        if (source.equals(destination)) {
            throw new ConsoleAppException(producer
                    + ": source and destinaction are the same file");
        }

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(destination);

            int bytesCopied;
            byte[] buf = new byte[8192];

            while (true) {
                bytesCopied = in.read(buf);
                if (bytesCopied < 0) {
                    break;
                }
                out.write(buf, 0, bytesCopied);
            }

        } catch (IOException e) {
            throw new ConsoleAppException(producer + ": io-error occured: "
                    + e.getMessage());
        } finally {
            Utils.closeResource(in);
            Utils.closeResource(out);
        }
    }

    private void doDir(String parts[]) throws ConsoleAppException {
        String usage = "dir";
        checkForParams(parts, usage, 1);

        for (String filename : workingDir.list()) {
            System.out.println(filename);
        }
    }

}
