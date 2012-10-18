package ru.fizteh.fivt.students.myhinMihail;

import java.io.*;
import java.util.Vector;

public class Shell {
    
    public static String currentPath;
    public static boolean console = true;
    
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
            System.err.println("rm: " + excpt.getMessage());
            if (!console) {
                System.exit(1);
            }
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
            byte[] buf = new byte[8000];
            while (true) {
                nLength = is.read(buf);
                if (nLength < 0) {
                    break;
                }
                os.write(buf, 0, nLength);
            }
            return true;
        } catch (Exception excpt) {
            System.err.println(command +": " + excpt.getMessage());
            if (!console) {
                System.exit(1);
            }
        } finally {
            if (is != null) {
               try {
                   is.close();
               } catch (Exception ex) {
               }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }
    
    public static boolean copy(File source, File dest, String command) {
        File dest2 = dest;
        if (source.isFile()) {
            if (!dest.isFile() && (dest.exists())) {
                dest2 = new File(dest.getAbsolutePath() + "/" + source.getName());
            }
            if (!copyFile(source, dest2, command)){
                System.err.println(command + ": Can not copy " + source + " to " + dest);
                if (!console) {
                    System.exit(1);
                }
                return false;
            }
        } else {
            dest2 = new File(dest.getAbsolutePath() + "/" + source.getName());
            if ((!dest2.exists() && !dest2.mkdirs()) || !source.exists()) {
                System.err.println(command + ": Can not copy " + source + " to " + dest);
                if (!console) {
                    System.exit(1);
                }
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
            System.err.println("mv: Can not delete " + source);
            if (!console) {
                System.exit(1);
            }
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
    
    public static boolean executeCommand(String comm) {
        switch (comm.replaceAll("\\s+", "")) {
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
                File dir = new File(currentPath + "/" + params.elementAt(1));
                try {
                    if (!dir.mkdir()) {
                        System.err.println("mkdir: Can not create " + dir);
                        if (!console) {
                            System.exit(1);
                        }
                    }
                } catch (Exception expt) {
                    System.err.println("mkdir: " + expt.getMessage());
                    if (!console) {
                        System.exit(1);
                    }
                }
                return true;
                
            case "rm":
                File file = new File(params.elementAt(1));
                if (!deleteDirectory(file)) {
                    System.err.println("rm: Can not delete " + file);
                    if (!console){
                        System.exit(1);
                    }
                }
                return true;
                
            case "cp":
                if (!checkCommandsCount(params, 3)) {
                    return false;
                }
                File src = new File(params.elementAt(1));
                File dst = new File(params.elementAt(2));
                if (!src.equals(dst)) {
                    if (!src.exists()) {
                        System.err.println("cp: \'" + src.getAbsolutePath() + "\' do not exists");
                        if (!console){
                            System.exit(1);
                        }
                    } else {
                        copy(src, dst, "cp");
                    }
                }
                return true;
                
            case "mv":
                if (!checkCommandsCount(params, 2)) {
                    return false;
                }
                File from = new File(params.elementAt(1));
                File to = new File(params.elementAt(2));
                if (!from.exists()) {
                    System.err.println("mv: \'" + from.getAbsolutePath() + "\' do not exists");
                    if (!console) {
                        System.exit(1);
                    }
                }
                
                File fullFrom = new File(from.getAbsolutePath());
                File fullTo = new File(to.getAbsolutePath());
                try {
                    if (fullFrom.getParentFile().equals(fullTo.getParentFile())) {
                        if (!from.renameTo(to)) {
                            moveFile(fullFrom, fullTo);
                        }
                    } else {
                        moveFile(fullFrom, fullTo);
                    }
                } catch (Exception expt) {
                    System.err.println("mv: Can not move " + fullFrom + " to " + fullTo);
                    if (!console) {
                        System.exit(1);
                    }
                }
                return true;
                
            case "cd":
                switch (params.elementAt(1)) {
                    case "..":
                        try {
                            currentPath = new File(currentPath).getParentFile().getAbsolutePath();
                        } catch (Exception expt) {
                            // it is root
                        }
                        break;
                    
                    case ".":
                        break;
                    
                    default:
                        File newPath = new File(currentPath + "/" + params.elementAt(1));
                        File newPath2 = new File(params.elementAt(1));
                        if (newPath.exists()) {
                            currentPath = newPath.getAbsolutePath();
                        } else {
                            if (newPath2.exists()) {
                                currentPath = newPath2.getAbsolutePath();
                            } else {
                                System.err.println("cd: path " + params.elementAt(1) + " does not exists");
                                if (!console) {
                                    System.exit(1);
                                }
                            }
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
                    String commands[] = input.readLine().split(";\\s*");
                    for (String s : commands) {
                        if (!executeCommand(s)) {
                            System.err.println("Bad command \'"+ s + "\'");
                        }
                    }
                }
            } else {
                console = false;
                StringBuilder sb = new StringBuilder();
                for (String str : args) {
                    sb.append(str).append(" ");
                }
                
                String commands[] = sb.toString().split(";\\s*");
                for (String s : commands) {
                    executeCommand(s);
                }
            }
        } catch (Exception expt) {
            System.err.println("Error: " + expt);
        }

    }

}
