package ru.fizteh.fivt.students.nikitaAntonov.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.fizteh.fivt.students.nikitaAntonov.utils.Utils;

/**
 * Класс имитации shell`а
 * 
 * @author Антонов Никита
 */
class Shell extends ConsoleApp {

    private File workingDir;
    
    public Shell() {
        workingDir = (new File(".")).getAbsoluteFile();
    }
    
    public static void main(String args[]) {
        Shell shell = new Shell();

        shell.run(args);
    }

    public boolean execute(String str) throws ConsoleAppException, IOException {
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
    
    private File getFileByName(String filename) {
        File result = new File(filename);
        
        if (!result.isAbsolute()) {
            result = new File(workingDir.getAbsolutePath() + File.separator + filename);
        }
        
        return result.getAbsoluteFile();
    }
    
    private void checkForParams(String params[], String usage, int needed) throws ConsoleAppException {
        if (params.length > needed) {
            throw new IncorrectUsageException(usage);
        }
    }
    
    private void deleteFile (File subject, String producer) throws ConsoleAppException {
        if (subject.isDirectory()) {
            for (String filename : subject.list()) {
                deleteFile (new File(subject.getAbsolutePath() + File.separator + filename).getAbsoluteFile(), producer);
            }
        }
        
        if (!subject.delete()) {
            throw new ConsoleAppException(producer + ": cannot remove " + subject.getPath() + ": Unknown error");
        }
    }
     
    private void copy(String producer, String srcFilename, String dstFilename) throws ConsoleAppException {
        File source = getFileByName(srcFilename);
        File destination = getFileByName(dstFilename);
        
        if (!source.exists()) {
            throw new ConsoleAppException(producer + ": cannot stat " + srcFilename + ": No such file or directory");
        }
        
        if (source.isDirectory()) {
            copyDirs(producer, source, destination);
        } else {
            copyFiles(producer, source, destination);
        }
    }
    
    private void copyDirs(String producer, File source, File destination) throws ConsoleAppException {
        if (!destination.mkdir() && !destination.exists()) {
            throw new ConsoleAppException(producer + ": cannot create directory " + destination.getAbsolutePath() + ": No such file or directory");
        }
        
        for (String filename : source.list()) {
            copy(producer, source.getAbsolutePath() + File.separator + filename, destination.getAbsolutePath() + File.separator + filename);
        }
    }
    
    private void copyFiles(String producer, File source, File destination) throws ConsoleAppException {
        if (source.equals(destination)) {
            throw new ConsoleAppException(producer + ": source and destinaction are the same file");
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
            throw new ConsoleAppException(producer + ": io-error occured: " + e.getMessage());
        } finally {
            Utils.closeResource(in);
            Utils.closeResource(out);
        }
    }
    
    private void doCd(String parts[]) throws ConsoleAppException {
        String usage = "cd <absolute path|relative path>";
        
        checkForParams(parts, usage, 2);
        
        File newDir = getFileByName(parts[1]);
        
        if (!newDir.exists()) {
            throw new ConsoleAppException("cd: " + parts[1] + ": No such file or directory");
        }
        
        if (!newDir.isDirectory()) {
            throw new ConsoleAppException("cd: " + parts[1] + ": Not a directory");
        }
        
        workingDir = newDir;
    }

    private void doMkdir(String parts[]) throws ConsoleAppException, IOException{
        String usage = "mkdir <dirname>";
        checkForParams(parts, usage, 2);
        
        File newDir = getFileByName(parts[1]);
        
        if (newDir.exists()) {
            throw new ConsoleAppException("mkdir: cannot create directory " + parts[1] + ": File exists");
        }
        
        if (!newDir.mkdir()) {
            throw new ConsoleAppException("mkdir: cannot create directory " + parts[1] + ": No such file or directory");
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
            throw new ConsoleAppException("rm: cannot remove " + parts[1] + ": No such file or directory");
        }
        
        deleteFile(subject, "rm");
    }

    private void doCp(String parts[]) throws ConsoleAppException {
        String usage = "cp <source> <destination>";
        checkForParams(parts, usage, 3);
        
        copy("cp", parts[1], parts[2]);
    }

    private void doMv(String parts[]) throws ConsoleAppException {
        String usage = "mv <source> <destination>";
        checkForParams(parts, usage, 3);
        
        copy("mv", parts[1], parts[2]);
        deleteFile(getFileByName(parts[1]), "mv");
    }

    private void doDir(String parts[]) throws ConsoleAppException {
        String usage = "dir";
        checkForParams(parts, usage, 1);
        
        for (String filename : workingDir.list()) {
            System.out.println(filename);
        }
    }

    @Override
    protected boolean processLine(String s) throws ConsoleAppException {
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

    @Override
    protected void printPrompt() {
        System.out.print("$ ");
    }

}

/**
 * Класс, представляющий абстракцию простого консольного приложения, получающего
 * что-то либо из параметров, либо из stdin (в случае отсутствия параметров)
 * 
 * Введён как средство избежать копипасты из калькулятора
 * 
 * @author Антонов Никита
 */
abstract class ConsoleApp {

    public void run(String args[]) {
        try {
            if (args.length > 0) {
                runWithParams(args);
            } else {
                runInteractive();
            }
        } catch (IOException e) {
            System.err.println("Unknown IO error");
            System.exit(1);
        }
    }

    public void runWithParams(String args[]) throws IOException {
        String str = Utils.concat(args);

        if (str.trim().isEmpty()) {
            runInteractive();
            return;
        }

        try {
            processLine(str);
        } catch (ConsoleAppException e) {
            System.exit(1);
        }
    }

    public void runInteractive() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String s = getLine(in);
        boolean isComplete = false;

        while (!(s == null || isComplete)) {

            if (s.trim().isEmpty()) {
                s = getLine(in);
                continue;
            }

            try {
                isComplete = processLine(s);
            } catch (ConsoleAppException e) {
            }
            
            if (!isComplete) {
                s = getLine(in);
            }
        }

        Utils.closeResource(in);
    }

    private String getLine(BufferedReader in) throws IOException {
        printPrompt();
        return in.readLine();
    }

    /* Должен вернуть true в случае необходимости завершить работу */
    protected abstract boolean processLine(String s) throws ConsoleAppException;
    protected abstract void printPrompt();

}

class ConsoleAppException extends Exception {

    private static final long serialVersionUID = -5154101410931907193L;

    public ConsoleAppException(String message) {
        super(message);
    }
    
    public ConsoleAppException(Throwable cause) {
        super(cause);
    }
    
    public ConsoleAppException(String message, Throwable cause) {
        super(message, cause);
    }
}

class IncorrectUsageException extends ConsoleAppException {

    private static final long serialVersionUID = 3825224109701172462L;
    private static final String prefix = "Incorrect number of parameters\n" +
                                  "Usage: ";

    public IncorrectUsageException(String message) {
        super(prefix + message);
    }
}