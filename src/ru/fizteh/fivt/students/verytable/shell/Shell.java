package ru.fizteh.fivt.students.verytable.shell;

import java.io.*;
import java.util.StringTokenizer;
import ru.fizteh.fivt.students.verytable.IOUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Arseny
 * Date: 20.10.12
 * Time: 1:10
 * To change this template use File | Settings | File Templates.
 */

public class Shell {

    static String curPath;
    static boolean isPackageMode;

    public enum CommandType {

        CD("cd"),
        MKDIR("mkdir"),
        PWD("pwd"),
        RM("rm"),
        CP("cp"),
        MV("mv"),
        DIR("dir"),
        EXIT("exit"),
        NOC("");  //not a command

        private String typeValue;

        private CommandType(String type) {
            typeValue = type;
        }

        static public CommandType getCommandType(String pType) {
            for (CommandType type : CommandType.values()) {
                if (type.getTypeValue().equals(pType)) {
                    return type;
                }
            }
            return NOC;
        }

        public String getTypeValue() {
            return typeValue;
        }

    }

    static boolean execCd(String destinedPath) {
        File parentFile = new File(curPath).getParentFile();
        File absoluteDestinedFile = new File(destinedPath).getAbsoluteFile();
        File destinedFile = new File(curPath + File.separatorChar
                                     + destinedPath);

        if (destinedPath.equals("..")) {
            if (parentFile == null) {
                System.err.println("Unable to cd ..");
                if (isPackageMode) {
                    System.exit(1);
                }
                return false;
            }
            String parentPath = parentFile.toString();
            if (parentFile.exists()) {
                curPath = parentPath;
            } else {
                System.err.println("No such directory.");
                if (isPackageMode) {
                    System.exit(1);
                }
                return false;
            }
        } else if (!destinedPath.equals(".")) {
            if (absoluteDestinedFile.exists()) {
                curPath = absoluteDestinedFile.toString();
            } else if (destinedFile.exists()) {
                curPath = destinedFile.toString();
            } else {
                System.err.println("Cd: " + destinedPath
                                   + " - no such directory");
                if (isPackageMode) {
                    System.exit(1);
                }
                return false;
            }
        }
        return true;
    }

    static boolean execMkdir(String dirName) {
        File desiredFile = new File(curPath + File.separatorChar
                                    + dirName);

        if (desiredFile.exists()) {
            System.err.println("mkdir: " + dirName
                               + " already exists.");
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        } else try {
            if (!desiredFile.mkdir()) {
                System.err.println("mkdir: " + dirName
                                   + " can't create directory.");
                if (isPackageMode) {
                    System.exit(1);
                }
                return false;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return true;
    }

    static void execPwd() {
        System.out.println(curPath);
    }

    static boolean execRm(String fileName) {
        if (fileName.equals("..") || fileName.equals(".")) {
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }
        File fileToDelete = new File(fileName);
        if (!fileToDelete.isAbsolute()) {
            fileToDelete = new File(curPath + File.separatorChar
                                    + fileName);
        }
        if (fileToDelete.isDirectory()) {
            File[] files = fileToDelete.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    if (!execRm(files[i].toString())) {
                        return false;
                    }
                } else if (!files[i].delete()) {
                    System.err.println("Can't delete1 " + fileName);
                    if (isPackageMode) {
                        System.exit(1);
                    }
                    return false;
                }
            }
            if (!fileToDelete.delete()) {
                System.err.println("Can't delete2 " + fileName);
                if (isPackageMode) {
                    System.exit(1);
                }
                return false;
            }
        } else if (!fileToDelete.delete()) {
            System.err.println("Can't delete3 " + fileName);
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }
        return true;
    }

    static boolean execCp(String source, String destination) {
        File from = new File(source);
        File to = new File(destination);

        if (!from.isAbsolute()) {
            from = new File(curPath + File.separatorChar + source);
        }
        if (!to.isAbsolute()) {
            to = new File(curPath + File.separatorChar + destination);
        }
        if (!from.exists()) {
            System.err.println("cp: " + from + " doesn't exist");
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }
        if (!to.exists()) {
            System.err.println("cp: " + to + " doesn't exist.");
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        String fromStr = from.toString();
        String toStr = to.toString();
        int fromLen = fromStr.length();
        int toLen = toStr.length();
        String relativeSourceFile = fromStr.substring(fromStr.lastIndexOf(File.separatorChar) + 1);

        if (fromStr.equals(toStr.substring(0, Math.min(fromLen, toLen)))) {
            System.err.println("cp: unable to copy from " + from
                               + " to it's subdirectory " + to);
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }
        if (from.isDirectory()) {
            File directoryCopy = new File(to.toString()
                                          + File.separatorChar
                                          + relativeSourceFile);
            if (!directoryCopy.exists()) {
                if (!directoryCopy.mkdir()) {
                    return false;
                }
            }
            File[] files = from.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (!execCp(files[i].toString(),
                            directoryCopy.toString())) {
                    return false;
                }
            }
        } else {
            File fileToCopy = new File(to.toString()
                                       + File.separatorChar
                                       + relativeSourceFile);
            try {
                fileToCopy.createNewFile();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
            try {
                File fromFile = new File(to.toString()
                                         + File.separatorChar
                                         + relativeSourceFile);
                fis = new FileInputStream(fromFile);
                fos = new FileOutputStream(fileToCopy);
                byte[] buffer = new byte[1024];
                int dataSize;
                while ((dataSize = fis.read(buffer)) >= 0) {
                    fos.write(buffer, 0, dataSize);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            } finally {
                IOUtils.closeFile(from.toString(), fis);
                IOUtils.closeFile(to.toString(), fos);
            }
        }
        return true;
    }

    static boolean execMv(String source, String destination) {
        File from = new File(source);
        File to = new File(destination);

        if (!from.isAbsolute()) {
            from = new File(curPath + File.separatorChar + source);
        }
        if (!to.isAbsolute()) {
            to = new File(curPath + File.separatorChar + destination);
        }
        if (!from.exists()) {
            System.err.println("cp: " + from + " doesn't exist");
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }
        if (!to.exists()) {
            System.err.println("cp: " + to + " doesn't exist.");
            if (isPackageMode) {
                System.exit(1);
            }
            return false;
        }

        if (from.equals(to)) {
            StringBuilder sb = new StringBuilder("new");
            File renamedFile = new File(from.toString() + sb);
            while (renamedFile.exists()) {
                sb.append("new");
                renamedFile = new File(renamedFile.toString() + sb);
            }
            from.renameTo(renamedFile);
        } else {
            if (!execCp(source, destination)) {
                return false;
            }
            if (!execRm(from.toString())) {
                return false;
            }
        }
        return true;
    }

    static void execDir() {
        File curDir = new File(curPath);
        File[] fileList = curDir.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            System.out.println(fileList[i]);
        }
    }

    static void execExit() {
        System.exit(0);
    }

    static void exec(String commandLine) {
        StringTokenizer tokenizer = new StringTokenizer(commandLine, " \t");
        CommandType command = CommandType.getCommandType(tokenizer.nextToken());

        switch (command) {
            case CD:
                if (tokenizer.countTokens() == 1) {
                    execCd(tokenizer.nextToken());
                } else {
                    System.err.println("Cd usage: "
                                       + "cd <absolute path | relative path>");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case MKDIR:
                if (tokenizer.countTokens() == 1) {
                    execMkdir(tokenizer.nextToken());
                } else {
                    System.err.println("Mkdir usage: mkdir <dirname>");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case PWD:
                if (!tokenizer.hasMoreTokens()) {
                    execPwd();
                } else {
                    System.err.println("Pwd usage: pwd");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case RM:
                if (tokenizer.countTokens() == 1) {
                    execRm(tokenizer.nextToken());
                } else {
                    System.err.println("Rm usage: rm <file | dir>");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case CP:
                if (tokenizer.countTokens() == 2) {
                    String source = tokenizer.nextToken();
                    String destination = tokenizer.nextToken();
                    execCp(source, destination);
                } else {
                    System.err.println("Cp usage: <source> <destination>");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case MV:
                if (tokenizer.countTokens() == 2) {
                    String source = tokenizer.nextToken();
                    String destination = tokenizer.nextToken();
                    execMv(source, destination);
                } else {
                    System.err.println("Mv usage: mv <source> <destination>");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case DIR:
                if (!tokenizer.hasMoreTokens()) {
                    execDir();
                } else {
                    System.err.println("Dir usage: dir");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case EXIT:
                if (!tokenizer.hasMoreTokens()) {
                    execExit();
                } else {
                    System.err.println("Exit usage: exit");
                    if (isPackageMode) {
                        System.exit(1);
                    }
                }
                break;
            case NOC:
                System.err.println("Unknown command. Possible commands: "
                                   + "cd, mkdir, pwd, rm, cp, mv, dir, exit.");
                if (isPackageMode) {
                    System.exit(1);
                }
        }
    }


    public static void main(String args[]) {

        curPath = new File("").getAbsolutePath();
        InputStreamReader isr;
        BufferedReader br;
        String curArgLine = null;
        String curToken;
        StringTokenizer tokenizer;
        StringBuilder sb = null;

        try {
            if (args.length == 0) {
                isPackageMode = false;
                isr = new InputStreamReader(System.in);
                br = new BufferedReader(isr);
                while (true) {
                    System.out.print("$ ");
                    try {
                        curArgLine = br.readLine();
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                        System.exit(1);
                    }
                    if (curArgLine == null) {
                        System.err.println("Empty input");
                        System.exit(1);
                    }
                    tokenizer = new StringTokenizer(curArgLine, ";");
                    while (tokenizer.hasMoreTokens()) {
                        curToken = tokenizer.nextToken();
                        exec(curToken);
                    }
                }
            } else {
                isPackageMode = true;
                for (int i = 0; i < args.length; ++i) {
                    sb.append(args[i]);
                    sb.append(" ");
                }
                tokenizer = new StringTokenizer(curArgLine, ";");
                while (tokenizer.hasMoreTokens()) {
                    curToken = tokenizer.nextToken();
                    exec(curToken);
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

}

