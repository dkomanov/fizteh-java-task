package ru.fizteh.fivt.students.verytable.shell;

import java.io.*;
import java.util.StringTokenizer;
import ru.fizteh.fivt.students.verytable.IOUtils;

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

    static void reportError(String errorMessage) {
        System.err.println(errorMessage);
        if (isPackageMode) {
            System.exit(1);
        }
    }

    static boolean execCd(String destinedPath) {
        File destinedFile = new File(destinedPath);
        if (!destinedFile.isAbsolute()) {
            destinedFile = new File(curPath + File.separatorChar
                                    + destinedPath);
        }

        if (!destinedFile.isDirectory()) {
            reportError("Cd: No such directory " + destinedFile);
            return false;
        }

        if(!destinedFile.exists()) {
            reportError("Cd: " + destinedPath + " - doesn't exist.");
            return false;
        }

        try {
            curPath = destinedFile.getCanonicalPath();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        return true;
    }

    static boolean execMkdir(String dirName) {
        File desiredFile = null;
        try {
            desiredFile = new File(curPath + File.separatorChar
                                   + dirName).getCanonicalFile();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        if (desiredFile.exists()) {
             reportError("mkdir: " + dirName
                         + " already exists.");
            return false;
        } else try {
            if (!desiredFile.mkdir()) {
                reportError("mkdir: " + dirName
                            + " can't create directory.");
                return false;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        return true;
    }

    static void execPwd() {
        System.out.println(curPath);
    }

    static boolean execRm(String fileName) {
        if (fileName.equals("..") || fileName.equals(".")) {
            reportError("Unable to remove " + fileName);
            return false;
        }
        File fileToDelete = new File(fileName);
        if (!fileToDelete.isAbsolute()) {
            fileToDelete = new File(curPath + File.separatorChar
                                    + fileName);
        }
        try {
            fileToDelete = fileToDelete.getCanonicalFile();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        if (fileToDelete.isDirectory()) {
            File[] files = fileToDelete.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    if (!execRm(files[i].toString())) {
                        return false;
                    }
                } else if (!files[i].delete()) {
                    reportError("Unable to remove " + fileName);
                    return false;
                }
            }
            if (!fileToDelete.delete()) {
                reportError("Unable to remove " + fileName);
                return false;
            }
        } else if (!fileToDelete.delete()) {
            reportError("Unable to remove " + fileName);
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
            reportError("cp: " + from + " doesn't exist");
            return false;
        }
        if (!to.exists()) {
            reportError("cp: " + to + " doesn't exist.");
            return false;
        }
        try {
            from = from.getCanonicalFile();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        try {
            to = to.getCanonicalFile();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        String fromStr = from.toString();
        String toStr = to.toString();
        int fromLen = fromStr.length();
        int toLen = toStr.length();

        if (fromStr.equals(toStr.substring(0, Math.min(fromLen, toLen)))) {
            reportError("cp: unable to copy from " + from
                        + " to it's subdirectory " + to);
            return false;
        }
        String relativeSourceFile = fromStr.substring(fromStr.lastIndexOf(File.separatorChar) + 1);
        if (from.isDirectory()) {
            File directoryCopy = new File(toStr + File.separatorChar
                                          + relativeSourceFile);
            if (directoryCopy.exists() && !directoryCopy.isDirectory()) {
                reportError("Cp: Unable to copy non file - " + fromStr
                            + "to file - " + toStr);
                return false;
            } else if (!directoryCopy.exists()) {
                if (!directoryCopy.mkdir()) {
                    reportError("Cp: unable to create " + directoryCopy);
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
            File fileWhereToCopy = new File(toStr);
            if (to.isDirectory()) {
                fileWhereToCopy = new File(toStr + File.separatorChar
                                      + relativeSourceFile);
            }
            try {
                if (!fileWhereToCopy.exists()) {
                    fileWhereToCopy.createNewFile();
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream(from);
                fos = new FileOutputStream(fileWhereToCopy);
                byte[] buffer = new byte[1024];
                int dataSize;
                while ((dataSize = fis.read(buffer)) >= 0) {
                    fos.write(buffer, 0, dataSize);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            } finally {
                IOUtils.closeFile(fromStr, fis);
                IOUtils.closeFile(toStr, fos);
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
            reportError("cp: " + from + " doesn't exist");
            return false;
        }
        if (!to.exists()) {
            reportError("cp: " + to + " doesn't exist.");
            return false;
        }
        try {
            from = from.getCanonicalFile();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        try {
            to = to.getCanonicalFile();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
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
                reportError("Mv: unable to execute cp "
                            + source + " " + destination);
                return false;
            }
            if (!execRm(from.toString())) {
                reportError("Mv: unable to execute rm " + from.toString());
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
                    reportError("Cd usage: "
                                + "cd <absolute path | relative path>");
                }
                break;
            case MKDIR:
                if (tokenizer.countTokens() == 1) {
                    execMkdir(tokenizer.nextToken());
                } else {
                    reportError("Mkdir usage: mkdir <dirname>");
                }
                break;
            case PWD:
                if (!tokenizer.hasMoreTokens()) {
                    execPwd();
                } else {
                    reportError("Pwd usage: pwd");
                }
                break;
            case RM:
                if (tokenizer.countTokens() == 1) {
                    execRm(tokenizer.nextToken());
                } else {
                    reportError("Rm usage: rm <file | dir>");
                }
                break;
            case CP:
                if (tokenizer.countTokens() == 2) {
                    String source = tokenizer.nextToken();
                    String destination = tokenizer.nextToken();
                    execCp(source, destination);
                } else {
                    reportError("Cp usage: <source> <destination>");
                }
                break;
            case MV:
                if (tokenizer.countTokens() == 2) {
                    String source = tokenizer.nextToken();
                    String destination = tokenizer.nextToken();
                    execMv(source, destination);
                } else {
                    reportError("Mv usage: mv <source> <destination>");
                }
                break;
            case DIR:
                if (!tokenizer.hasMoreTokens()) {
                    execDir();
                } else {
                    reportError("Dir usage: dir");
                }
                break;
            case EXIT:
                if (!tokenizer.hasMoreTokens()) {
                    execExit();
                } else {
                    reportError("Exit usage: exit");
                }
                break;
            case NOC:
                reportError("Unknown command. Possible commands: "
                            + "cd, mkdir, pwd, rm, cp, mv, dir, exit.");
        }
    }


    public static void main(String args[]) {

        curPath = new File("").getAbsolutePath();

        try {
            if (args.length == 0) {
                isPackageMode = false;
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);
                String curArgLine = null;
                while (true) {
                    System.out.print("$ ");
                    try {
                        curArgLine = br.readLine();
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                        System.exit(1);
                    }
                    StringTokenizer tokenizer = new StringTokenizer(curArgLine, ";");
                    String curToken;
                    while (tokenizer.hasMoreTokens()) {
                        curToken = tokenizer.nextToken();
                        exec(curToken);
                    }
                }
            } else {
                isPackageMode = true;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; ++i) {
                    sb.append(args[i]);
                    sb.append(" ");
                }
                StringTokenizer tokenizer = new StringTokenizer(sb.toString(), ";");
                String curToken;
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

