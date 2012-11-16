package ru.fizteh.fivt.students.frolovNikolay.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * Класс отвечающий
 * за выполнение команд.
 */
public class Executer {
    final private static String separator = System.getProperty("file.separator");
    
    public static String execute(String[] commandArray, String currentDir) throws Exception {
        if (commandArray[0].equals("cd")) {
            if (commandArray.length != 2) {
                throw new Exception("Usage: cd <absolute path|relative path>");
            } else {
                String newCurDir = getRealAddress(currentDir, commandArray[1]);
                File existTest = new File(newCurDir);
                if (existTest.exists() && existTest.isDirectory()) {
                    currentDir = newCurDir;
                } else {
                    throw new Exception("cd: '" + commandArray[1] + "': No such directory.");
                }
            }
        } else if (commandArray[0].equals("mkdir")) {
            if (commandArray.length != 2) {
                throw new Exception("Usage : mkdir <dirname>");
            } else {
                String newDir = getRealAddress(currentDir, commandArray[1]);
                File creator = new File(newDir);
                if (!creator.mkdir()) {
                    throw new Exception("mkdir: '" + commandArray[1] + "': Can not create directory.");
                }
            }
        } else if (commandArray[0].equals("pwd")) {
            if (commandArray.length != 1) {
                throw new Exception("Usage : pwd.");
            } else {
                System.out.println(currentDir);
            }
        } else if (commandArray[0].equals("rm")) {
            if (commandArray.length != 2) {
                throw new Exception("Usage : rm <file|dir>");
            } else {
                String fileName = getRealAddress(currentDir, commandArray[1]);
                File fileForDel = new File(fileName);
                if (fileForDel.exists()) {
                    remove(fileForDel);
                } else {
                    throw new Exception("rm: '" + commandArray[1] + "': No such file or directory.");
                }
            }
        } else if (commandArray[0].equals("cp")) {
            if (commandArray.length != 3) {
                throw new Exception("Usage : cp <source> <destination>");
            } else {
                String sourceAddress = getRealAddress(currentDir, commandArray[1]);
                String destAddress = getRealAddress(currentDir, commandArray[2]);
                File source = new File(sourceAddress);
                File destination = new File(destAddress);
                if (!source.exists()) {
                    throw new Exception("cp: '" + commandArray[1] + "': no such file or directory.");
                } else if (destination.exists()) {
                    throw new Exception("cp: '" + commandArray[2] + "': file already exists.");
                } else if (!destination.getParentFile().exists()) {
                    throw new Exception("cp: Incorrect destination.");
                }
                copy(source, destination);
            }
        } else if (commandArray[0].equals("mv")) {
            if (commandArray.length != 3) {
                throw new Exception("Usage : mv <source> <destination>");
            } else {
                String src = getRealAddress(currentDir, commandArray[1]);
                String dest = getRealAddress(currentDir, commandArray[2]);
                File source = new File(src);
                File destination = new File(dest);
                if (!source.exists()) {
                    throw new Exception("mv: '" + commandArray[1] + "': No such file or directory.");
                } else if (destination.exists()) {
                    throw new Exception("mv: '" + commandArray[2] + "': File already exists.");
                } else if (!destination.getParentFile().exists()) {
                    throw new Exception("mv: Incorrect destination.");
                }
                copy(source, destination);
                remove(source);
            }
            
        } else if (commandArray[0].equals("dir")) {
            if (commandArray.length != 1) {
                throw new Exception("Usage : dir");
            } else {
                File directory = new File(currentDir);
                String[] files = directory.list();
                for (String iter : files) {
                    System.out.println(iter);
                }
            }
        } else if (commandArray[0].equals("exit")) {
            if (commandArray.length != 1) {
                throw new Exception("Usage : exit");
            } else {
                return null;
            }
        } else {
            throw new Exception("Error! Unknown command.");
        }
        return currentDir;
    }
    
    private static void remove(File forDelete) throws Exception {
        if (forDelete.isDirectory()) {
            String[] files = forDelete.list();
            for (String iter : files) {
                File child = new File(forDelete.getAbsolutePath() + separator + iter);
                remove(child);
            }
            if (!forDelete.delete()) {
                throw new Exception("Error! Can not delete file.");
            }
        } else if (!forDelete.delete()) {
            throw new Exception("Error! Can not delete file.");
        }
    }
    
    private static void copy(File source, File destination) throws Exception {
        if (source.isDirectory()) {
            if (!destination.mkdir()) {
                throw new Exception("Error! Can not copy.");
            } else {
                File[] files = source.listFiles();
                for (File srcIter : files) {
                    File newDest = new File(destination.getCanonicalPath() + separator + srcIter.getName());
                    copy(srcIter, newDest);
                }
            }
        } else {
            if (!destination.createNewFile()) {
                throw new Exception("Error! Can not copy.");
            } else {
                copyFiles(source, destination);
            }
        }
    }
    
    private static String getRealAddress(String currentDir, String mayBeAbsolute) throws Exception {
        File absoluteAddr = new File(mayBeAbsolute);
        if (absoluteAddr.isAbsolute()) {
            return absoluteAddr.getCanonicalPath();
        } else {
            File nonAbsoluteAddr = new File(currentDir + separator + mayBeAbsolute);
            return nonAbsoluteAddr.getCanonicalPath();
        }
    }
    
    private static void copyFiles(File source, File destination) throws Exception {
        FileInputStream iStream = null;
        FileOutputStream oStream = null;
        try {
            iStream = new FileInputStream(source);
            oStream = new FileOutputStream(destination);
            int readLength;
            byte[] buffer = new byte[1024];
            while (true) {
                readLength = iStream.read(buffer);
                if (readLength < 0) {
                    break;
                } else {
                    oStream.write(buffer, 0, readLength);
                }
            }
        } finally {
            Closer.close(iStream);
            Closer.close(oStream);
        }
    }
}