package ru.fizteh.fivt.students.khusaenovTimur.shell;


import java.io.*;
import java.util.*;
import java.lang.Character;

/**
 * Created with IntelliJ IDEA.
 * User: Timur
 * Date: 16.11.12
 * Time: 23:19
 */
public class Shell {
    public static String currentPath;
    public static boolean isInteractiveMode = false;

    public static boolean checkCommandFormat(ArrayList<String> parsedCommand, int numOfParts) {
        if (parsedCommand.size() != numOfParts) {
            errorProcessing(parsedCommand.get(0) + " : wrong \"" + parsedCommand.get(0) + "\" format");
            return false;
        }
        return true;
    }

    public static ArrayList<String> parseCommand(String command) {
        ArrayList<String> parsedCommand = new ArrayList<String>();
        boolean isInQuotationMarks = false;
        int startOfNewPart = 0;
        for (int i = 0; i < command.length(); ++i) {
            if (Character.isWhitespace(command.charAt(i))) {
                if (!isInQuotationMarks) {
                    if (!command.substring(startOfNewPart, i).isEmpty()) {
                        String string = command.substring(startOfNewPart, i);
                        if (!string.replaceAll("\\s", "").isEmpty()) {
                            parsedCommand.add(string);
                        }
                        startOfNewPart = i + 1;
                    }
                }
            } else if (command.charAt(i) == '\"') {
                if (!isInQuotationMarks) {
                    startOfNewPart = i + 1;
                    isInQuotationMarks = true;
                } else {
                    parsedCommand.add(command.substring(startOfNewPart, i));
                    isInQuotationMarks = false;
                    startOfNewPart = i + 1;
                }
                break;
            }
        }
        String string = command.substring(startOfNewPart, command.length());
        if (!string.replaceAll("\\s", "").isEmpty()) {
            parsedCommand.add(string);
        }
        return parsedCommand;
    }

    public static void errorProcessing(String message) {
        System.err.println(message);
        if (isInteractiveMode) {
            return;
        }
        System.exit(1);
    }

    public static void exit(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 1)) {
            System.exit(0);
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static void printWorkingDirectory(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 1)) {
            System.out.println(currentPath);
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static void printContentsOfDirectory(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 1)) {
            File currentDirectory = new File(currentPath);
            for (String fileName : currentDirectory.list()) {
                System.out.println(fileName);
            }
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static File getAbsolutePathsFile(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(currentPath, path);
        }
        return file;
    }

    public static void makeDirectory(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 2)) {
            File directory = getAbsolutePathsFile(parsedCommand.get(1));
            try {
                if (!directory.mkdirs()) {
                    errorProcessing("mkdir: cannot make \'" + directory.getAbsolutePath() + "\'");
                }
            } catch (Exception exception) {
                errorProcessing("mkdir: " + exception.getMessage());
            }
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static void changeDirectory(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 2)) {
            String newPath = parsedCommand.get(1);
            File newFile = getAbsolutePathsFile(newPath);
            if (!newFile.exists()) {
                errorProcessing("cd: '" + newFile.getName() + "': No such file or directory");
            } else {
                try {
                    currentPath = newFile.getCanonicalPath();
                } catch (Exception exception) {
                    errorProcessing("cd: " + exception.getMessage());
                }
            }
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static boolean deleteFile(File file) {
        try {
            if (file.isDirectory()) {
                for (String fileName : file.list()) {
                    File subFile = new File(file, fileName);
                    if (!deleteFile(subFile)) {
                        return false;
                    }
                }
                return file.delete();
            } else {
                return file.delete();
            }
        } catch (Exception exception) {
            errorProcessing("rm: " + exception.getMessage());
        }
        return false;
    }

    public static void remove(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 2)) {
            File file = getAbsolutePathsFile(parsedCommand.get(1));
            if (!deleteFile(file)) {
                errorProcessing("rm: cannot delete \'" + file.getAbsolutePath() + "\'");
            }
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static boolean copyFile(File from, File to) throws FileNotFoundException {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        if (!from.equals(to)) {
            try {
                fileInputStream = new FileInputStream(from);
                fileOutputStream = new FileOutputStream(to);
                byte[] buffer = new byte[2048];
                int bufferLength;
                while ((bufferLength = fileInputStream.read(buffer)) >= 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                return true;
            } catch (Exception exception) {
                errorProcessing(exception.toString());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        } else {
            errorProcessing("cannot copy.");
        }
        return false;
    }

    public static void copy(ArrayList<String> parsedCommand) throws FileNotFoundException {
        if (checkCommandFormat(parsedCommand, 3)) {
            File from = getAbsolutePathsFile(parsedCommand.get(1));
            if (!from.exists()) {
                errorProcessing(parsedCommand.get(0) + ": file \'" + from.getAbsolutePath() + "\' does not exist");
                throw new RuntimeException("incorrect command.");
            }
            File to = getAbsolutePathsFile(parsedCommand.get(2));
            File finalTo = to;
            if (from.isFile()) {
                if (to.isDirectory() && to.exists()) {
                    finalTo = new File(to.getAbsolutePath() + File.separator + from.getName());
                }
                if (!copyFile(from, to)) {
                    errorProcessing(parsedCommand.get(0) + ": cannot copy file \'" + from.getAbsolutePath() + "\' to \'" + to.getAbsolutePath() + "\'");
                    throw new RuntimeException("incorrect command.");
                }
            } else {
                finalTo = new File(to.getAbsolutePath() + File.separator + from.getName());
                if (!finalTo.exists() && !finalTo.mkdirs()) {
                    errorProcessing(parsedCommand.get(0) + ": cannot copy file \'" + from.getAbsolutePath() + "\' to \'" + to.getAbsolutePath() + "\'");
                    throw new RuntimeException("incorrect command.");
                }
                for (String fileName : from.list()) {
                    ArrayList<String> arrayList = new ArrayList<String>(3);
                    arrayList.add(parsedCommand.get(0));
                    arrayList.add(new File(from.getAbsolutePath() + File.separator + fileName).getAbsolutePath());
                    arrayList.add(new File(to.getAbsolutePath() + File.separator + fileName).getAbsolutePath());
                    copy(arrayList);
                }
            }
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static void move(ArrayList<String> parsedCommand) {
        if (checkCommandFormat(parsedCommand, 3)) {
            File from = getAbsolutePathsFile(parsedCommand.get(1));
            File to = getAbsolutePathsFile(parsedCommand.get(2));
            if (!from.exists()) {
                errorProcessing("mv: \'" + parsedCommand.get(1) + "\': No such file or directory");
            }
            try {
                if (from.getParentFile().equals(to.getParentFile())) {
                    if (!from.renameTo(to)) {
                        copyFile(from, to);
                        deleteFile(from);
                    }
                } else {
                    ArrayList<String> newCommands = new ArrayList<String>(3);
                    newCommands.add(parsedCommand.get(0));
                    newCommands.add(from.getAbsolutePath());
                    newCommands.add(to.getAbsolutePath());
                    copy(newCommands);
                    if (deleteFile(from)) {
                        errorProcessing("mv: unsuccessful deleting of " + from.getAbsolutePath());
                    }

                }
            } catch (Exception exception) {
                errorProcessing("mv: " + exception.getMessage());
            }
        } else {
            throw new RuntimeException("incorrect command.");
        }
    }

    public static void executeCommand(String command) throws FileNotFoundException {
        ArrayList<String> parsedCommand = parseCommand(command);
        String s = parsedCommand.get(0);
        if (s.equals("cd")) {
            changeDirectory(parsedCommand);

        } else if (s.equals("mkdir")) {
            makeDirectory(parsedCommand);

        } else if (s.equals("pwd")) {
            printWorkingDirectory(parsedCommand);

        } else if (s.equals("rm")) {
            remove(parsedCommand);

        } else if (s.equals("cp")) {
            copy(parsedCommand);

        } else if (s.equals("mv")) {
            move(parsedCommand);

        } else if (s.equals("dir")) {
            printContentsOfDirectory(parsedCommand);

        } else if (s.equals("exit")) {
            exit(parsedCommand);

        } else {
            errorProcessing("Unknown command");
            throw new RuntimeException();
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            isInteractiveMode = true;
        }
        currentPath = new File("").getAbsolutePath();
        /*  if (isInteractiveMode) {
            System.out.print(currentPath);
        }*/
        if (!isInteractiveMode) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                stringBuilder.append(args[i]).append(" ");
            }
            stringBuilder.append(";");
            String commandsList[] = stringBuilder.toString().split("\\s*;\\s*");
            try {
                for (String command : commandsList) {
                    if (command.length() > 0) {
                        executeCommand(command);
                    }
                }
            } catch (Throwable t) {
                //t.printStackTrace();
                System.exit(1);
            }
        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader input = new BufferedReader(inputStreamReader);
            while (true) {
                System.out.print(currentPath + "& ");
                StringBuilder stringBuilder = new StringBuilder();
                String incomingCommands = input.readLine();
                if (incomingCommands != null) {
                    stringBuilder.append(incomingCommands).append(" ; ");
                    String commandsList[] = stringBuilder.toString().split("\\s*;\\s*");
                    try {
                        for (String command : commandsList) {
                            executeCommand(command);
                        }
                    } catch (Throwable t) {
                    }
                } else {
                    System.exit(0);
                }
            }
        }
    }
}
