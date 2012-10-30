package ru.fizteh.fivt.students.myhinMihail;

import java.io.*;
import java.util.Vector;

public class Shell {
    
    public static String currentPath;
    public static boolean console = true;
    
    public static <T extends Closeable> void tryClose(T object) {
        if (object != null) {
            try {
                  object.close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static void errorAndExit(String error) {
        System.err.println(error);
        if (!console) {
            System.exit(1);
        }
    }
    
    public static boolean deleteDirectory(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (String fl : children) {
                    File f = new File(dir, fl);
                    if (!deleteDirectory(f)) {
                        return false;
                    }
                }
                return dir.delete();
            } else {
                return dir.delete();
            }
        } catch (Exception excpt) {
            errorAndExit("rm: " + excpt.getMessage());
        }
        return false;
    }
    
    public static boolean copyFile(File source, File dest, String command) {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            int nLength;
            byte[] buf = new byte[8192];
            while (true) {
                nLength = is.read(buf);
                if (nLength < 0) {
                    break;
                }
                os.write(buf, 0, nLength);
            }
            return true;
        } catch (Exception excpt) {
            errorAndExit(command +": " + excpt.getMessage());
        } finally {
              tryClose(is);
              tryClose(os);
        }
        return false;
    }
    
    public static boolean copy(File source, File dest, String command) {
        File dest2 = dest;
        if (source.isFile()) {
            if (!dest.isFile() && (dest.exists())) {
                dest2 = new File(dest.getAbsolutePath() + "/" + source.getName());
            }
            if (!copyFile(source, dest2, command)) {
                errorAndExit(command + ": Can not copy " + source + " to " + dest);
                return false;
            }
        } else {
            dest2 = new File(dest.getAbsolutePath() + "/" + source.getName());
            if ((!dest2.exists() && !dest2.mkdirs()) || !source.exists()) {
                errorAndExit(command + ": Can not copy " + source + " to " + dest);
                return false;
            }
            for (String fl : source.list()) {
                File newSource = new File(source.getAbsolutePath() + "/" + fl);
                File newDest = new File(dest2.getAbsolutePath() + "/" + fl);
                if (!copy(newSource, newDest, command)) {
                    return false;
                }
            }
            
        }
        return true;
    }
    
    public static boolean moveFile(File source, File dest) {
        if (!copy(source, dest, "mv")) {
            return false;
        }
        
        if (!deleteDirectory(source)) {
            errorAndExit("mv: Can not delete " + source);
            return false;
        }
        
        return true;
    }
    
    public static Vector<String> parseCommand(String str) {
        Vector<String> vct = new Vector<String>();
        int start = 0;
        boolean quote = false;
        
        for (int i = 0; i < str.length(); ++i) {
            switch(str.charAt(i)) {
                case '\"':
                    if (quote) {
                        vct.add(str.substring(start, i));
                    } 
                    start = i + 1;
                    quote = !quote;
                    break;
                    
                case ' ':
                    if (!quote) {
                        if (!str.substring(start, i).replaceAll("\\s", "").isEmpty()) {
                            vct.add(str.substring(start, i));
                        }
                        start = i + 1;
                    }
                    break;
            }
        } 
        if (!str.substring(start, str.length()).replaceAll("\\s", "").isEmpty()) {
            vct.add(str.substring(start, str.length()));
        }
        return vct;
    }
    
    public static boolean checkCommandsCount(Vector<String> params, int size) {
        if (params.size() < size) {
            if (!console) {
                System.err.println("Bad command");
                System.exit(1);
            }
            return false;
        }
        return true;
    }
    
    public static File makeAbsolute(String path) {
        File f = new File(path);
        if (!f.isAbsolute()) {
            f = new File(currentPath + "/" + path);
        }
        return f;
    }
    
    public static boolean executeCommand(String comm) {
        switch (comm) {
            case "exit":
                System.exit(0);
                return true;
                
            case "pwd":
                System.out.println(currentPath);
                return true;
                
            case "dir":
                File dir = new File(currentPath);
                for (String fl: dir.list()) {
                    System.out.println(fl);
                }
                return true;
        }
        
        Vector<String> params = parseCommand(comm);
        if (!checkCommandsCount(params, 2)) {
            return false;
        }
        
        switch (params.elementAt(0)) {
            case "mkdir":
                File dir = makeAbsolute(params.elementAt(1));
                try {
                    if (!dir.mkdir()) {
                        errorAndExit("mkdir: Can not create " + dir);
                    }
                } catch (Exception expt) {
                    errorAndExit("mkdir: " + expt.getMessage());
                }
                return true;
                
            case "rm":
                File file = makeAbsolute(params.elementAt(1));
                if (!deleteDirectory(file)) {
                    errorAndExit("rm: Can not delete " + file);
                }
                return true;
                
            case "cp":
                if (!checkCommandsCount(params, 3)) {
                    return false;
                }
                File src = makeAbsolute(params.elementAt(1));
                File dst = makeAbsolute(params.elementAt(2));
                
                if (!src.equals(dst)) {
                    if (!src.exists()) {
                        errorAndExit("cp: \'" + src.getAbsolutePath() + "\' do not exists");
                    } else {
                        copy(src, dst, "cp");
                    }
                }
                return true;
                
            case "mv":
                if (!checkCommandsCount(params, 2)) {
                    return false;
                }
                File from = makeAbsolute(params.elementAt(1));
                File to = makeAbsolute(params.elementAt(2));

                if (!from.exists()) {
                    errorAndExit("mv: \'" + from.getAbsolutePath() + "\' do not exists");
                    return true;
                }
                
                try {
                    if (from.getParentFile().equals(to.getParentFile())) {
                        if (!from.renameTo(to)) {
                            moveFile(from, to);
                        }
                    } else {
                        moveFile(from, to);
                    }
                } catch (Exception expt) {
                    errorAndExit("mv: Can not move " + from + " to " + to);
                }
                return true;
                
            case "cd":
                switch (params.elementAt(1)) {
                    case "..":
                        try {
                            currentPath = new File(currentPath).getParentFile().getAbsolutePath();
                        } catch (Exception expt) {
                            System.err.println("cd: it is root");
                        }
                        break;
                    
                    case ".":
                        break;
                    
                    default:
                        File newPath = makeAbsolute(params.elementAt(1));
                        if (params.elementAt(1).replaceAll("([A-Z][:][\\\\][\u002E]{2})|([/][\u002E]{2})", "!").equals("!")) {
                            System.err.println("cd: it is root");
                            return true;
                        }
                        if (newPath.exists()) {
                            try {
                                currentPath = newPath.getCanonicalPath();
                            } catch (Exception expt) {
                                System.err.println("Error: " + expt.getMessage());
                            }
                        } else {
                            errorAndExit("cd: \'" + params.elementAt(1) + "\' do not exists");
                        }
                            
                        break;
                }
                return true;
                
            default:
                if (!console) {
                    System.exit(1);
                }
                return false;
        }
    }

    public static void main(String[] args) throws Exception {
        currentPath =  new File("").getAbsolutePath();
        try {
            if (args.length == 0) {
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.print("$ ");
                    StringBuilder sb = new StringBuilder();
                    sb.append(input.readLine()).append(" ; ");
                    
                    String commands[] = sb.toString().split("\\s*;\\s*");
                    for (String s : commands) {
                        if (!executeCommand(s)) {
                            System.err.println("Bad command \'"+ s + "\'");
                            break;
                        }
                    }
                }
            } else {
                console = false;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length - 1; ++i) {
                    sb.append(args[i]).append(" ");
                }
                sb.append(args[args.length - 1]);
                sb.append(" ; ");
                
                String commands[] = sb.toString().split("\\s*;\\s*");
                for (String s : commands) {
                    executeCommand(s);
                }
            }
        } catch (Exception expt) {
            System.err.println("Error: " + expt);
            System.exit(1);
        }

    }

}
