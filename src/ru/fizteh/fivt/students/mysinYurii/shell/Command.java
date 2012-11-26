package ru.fizteh.fivt.students.mysinYurii.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Command {
    public String path;
    
    Command() {
        File cwd = new File("");
        path = new String(cwd.getAbsolutePath());
    }
    
    public String toAbsolutePath(String s) throws IOException {
        StringBuilder sFormat = new StringBuilder();
        sFormat.append(s);
        File newPath = new File(sFormat.toString());
        if (newPath.isAbsolute()) {
            return newPath.getCanonicalPath();
        } else {
            StringBuilder newPathName = new StringBuilder();
            newPathName.append(path);
            newPathName.append(File.separator);
            newPathName.append(sFormat.toString());
            newPath = new File(newPathName.toString());
            return newPath.getCanonicalPath();
        }
    }

    public boolean ifExist(String s) {
        File checkPath = new File(s);
        if (checkPath.isAbsolute()) {
            return checkPath.exists();
        } else {
            StringBuilder newPath = new StringBuilder();
            newPath.append(path);
            newPath.append(s);
            File whereToGo = new File(newPath.toString());
            return whereToGo.exists();
        }
    }
    
    public void copy(String from, String to) throws ShellException {
        String task = "cp";
        try {
            from = toAbsolutePath(from);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            to = toAbsolutePath(to);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        File fromFile = new File(from);
        if (!fromFile.exists()) {
            throw new ShellException(task, "File or directory doesn't exist: " + fromFile.getAbsolutePath());
        }
        if (fromFile.isFile()) {
            copySourceTo(from, to);
        } else {
            File toFile = new File(to);
            if (!toFile.exists()) {
                mkdir(toFile.getAbsolutePath());
            }
            toFile = new File(toFile.getAbsolutePath() + File.separator + fromFile.getName());
            if (!toFile.exists()) {
                mkdir(toFile.getAbsolutePath());
            }
            copySourceTo(from, toFile.getAbsolutePath());
        }
    }
    
    private void copySourceTo(String from, String to) throws ShellException {
        String task = "cp";
        String fromPath = null;
        try {
            fromPath = toAbsolutePath(from);
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
        String toPath = null;
        try {
            toPath = toAbsolutePath(to);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            throw new ShellException(task, "File doesn't exist: " + fromFile.getAbsolutePath());
        } 
        if (!toFile.exists()) {
            if (!toFile.mkdir()) {
                throw new ShellException(task, "Can't create folder: " + toFile.getAbsolutePath());
            }
        } 
        if (!toFile.isDirectory()) {
            throw new ShellException(task, "Not a directory: " + toFile.getAbsolutePath());
        } else {
            if (fromFile.isFile()) {
                try {
                    copyOneFile(fromFile, toFile);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                File[] toCopyList = fromFile.listFiles();
                for (File iter : toCopyList) {
                    if (iter.isFile()) {
                        try {
                            copyOneFile(iter, new File(toPath));
                        } catch (IOException e) {
                            throw new ShellException(task, e.getMessage());
                        }
                    } else {
                        StringBuilder toPathFile = new StringBuilder();
                        toPathFile.append(toFile.getAbsolutePath());
                        toPathFile.append(File.separator);
                        toPathFile.append(iter.getName());
                        copySourceTo(iter.getAbsolutePath(), toPathFile.toString());
                    }
                }
            }
        }
    }
    
    public void mv(String from, String to) throws ShellException {
        copy(from, to);
        rm(from);
    }
    
    private void copyOneFile(File fromFile, File toFile) throws IOException {
        StringBuilder newFilePath = new StringBuilder();
        newFilePath.append(toFile.getAbsolutePath());
        newFilePath.append(File.separator);
        newFilePath.append(fromFile.getName());
        toFile = new File(newFilePath.toString());
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = new FileInputStream(fromFile).getChannel();
            toChannel = new FileOutputStream(toFile).getChannel();
            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
            fromChannel.close();
            toChannel.close();
        } catch(IOException e) {
            if (fromChannel != null) {
                fromChannel.close();
            }
            if (toChannel != null) {
                toChannel.close();
            }
            throw e;
        }
    }

    public void cd(String s) throws ShellException {
        String task = "cd";
        s.trim();
        String newPath = null;
        try {
            newPath = toAbsolutePath(s);
        } catch (IOException e) {
            throw new ShellException(task, e.getMessage());
        }
        File whereToGo = new File(newPath);
        if (whereToGo.exists()) {
            if (whereToGo.isDirectory()) {
                path = newPath;
            } else {
                throw new ShellException(task, "Not a directory: " + newPath);
            }
        } else {
            throw new ShellException(task, "Directory doesn't exist: " + newPath);
        }
    }
    
    //возвращает true если директория уже есть или была создана
    public void mkdir(String s) throws ShellException {
        String task = "mkdir";
        String newPath = null;
        try {
            newPath = toAbsolutePath(s);
        } catch (IOException e) {
            throw new ShellException(task, e.getMessage());
        }
        File newDir = new File(newPath);
        if (newDir.exists()) {
            throw new ShellException(task, "Directory exists: " + s);
        } else {
            if (!newDir.mkdir()) {
                throw new ShellException(task, "Can't make directory: " + s);
            }
        }
    }
    
    public void rm(String s) throws ShellException {
        String task = "rm";
        String removePath = null;
        try {
            removePath = toAbsolutePath(s);
        } catch (IOException e) {
            throw new ShellException(task, e.getMessage());
        }
        File removeFile = new File(removePath);
        if (removeFile.exists()) {
            if (removeFile.isFile()) {
                if (!removeFile.delete()) {
                    throw  new ShellException(task, "Can't delete file: " + removePath);
                }
            } else {
                File[] directoryFiles = removeFile.listFiles();
                for (int i = 0; i < directoryFiles.length; ++i) {
                    rm(directoryFiles[i].getAbsolutePath());
                }
                removeFile.delete();
            }
        } else {
            throw new ShellException(task, "File not found: " + removePath);
        }
    }
    
    public void pwd() {
        System.out.println(path);
    }
    
    public void dir() {
        File currDir = new File(path);
        File[] filesInDir = currDir.listFiles();
        for (int i = 0; i < filesInDir.length; ++i) {
            System.out.println(filesInDir[i].getName());
        }
    }
}
