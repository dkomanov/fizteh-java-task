package ru.fizteh.fivt.students.levshinNikolay.shell;




import ru.fizteh.fivt.students.levshinNikolay.Utils;

import java.io.*;

/*
* Levshin Nikolay
* FIVT 196
*/
public class Shell {

    public static String currPath;
    public static boolean console;

    public static void error(String message) {
        System.err.println(message);
        if(!console){
            System.exit(1);
        }
    }

    public static void cd(String path){
        File newFile = new File(path);
        if(!newFile.exists()){
            error("cd: '" + newFile.getName() + ": No such file or directory.");
        }
        currPath = path;
    }

    public static void mkdir(String name){
        File newDir = new File(name);
        if(!newDir.mkdir()){
            error("mkdir: '" + newDir.getName() + ": Failed to create new directory.");
        }
    }

    public static String createAbsolute(String path){
        File curFile = new File(path);
        if(!curFile.isAbsolute()){
            path = currPath + '/' + path;
        }
        return path;
    }

    public static void rm(File rmfile, String mode){
        if (rmfile.isDirectory()){
            String[] cont = rmfile.list();
            for(String file: cont){
                rm(new File(rmfile, file),mode);
            }
        }
        if (!rmfile.delete()){
            error(mode + ": '" + rmfile.getName() + "': No such file or directory.");
        }
    }
    public  static void cp(File source,File destination,String mode){
        if (!source.exists()){
            error(mode + ": '" + source.getName() + "': No such file or directory.");
        }
        if (source.isDirectory()){
            if (!destination.exists()){
                if (!destination.mkdir()) {
                    error(mode + ": '" + destination.getName() + "': Failed to create directory.");
                }
            }
            String[] files = source.list();
            for(String file: files){
                File newSource = new File(source,file);
                File newDestination = new File(destination,file);
                cp(newSource,newDestination,mode);
            }


        } else {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);
                byte[] buf = new byte[1024];
                int length = 0;
                while(length>=0){
                    length = in.read(buf);
                    out.write(buf,0,length);
                }
            }  catch(Exception ex) {
                error("cp: '" + ex.getMessage());
            }  finally {
                Utils.tryClose(in);
                Utils.tryClose(out);
            }
        }
    }

    public static void dir(){
        File dir = new File(currPath);
        String[] files = dir.list();
        for (String file:files){
            System.out.println(file);
        }
    }

    public static void exec(String command){
        String[] args = command.split("\\s+");
        switch (args[0]){
            case "cd":
                if (args.length != 2){
                    error("cd: Incorrect arguments.");
                }
                args[1] = createAbsolute(args[1]);
                cd(args[1]);
                currPath = createAbsolute(currPath);
                break;

            case "mkdir":
                if (args.length != 2){
                    error("mkdir: Incorrect arguments.");
                }
                args[1] = createAbsolute(args[1]);
                mkdir(args[1]);
                currPath = createAbsolute(currPath);
                break;

            case "pwd":
                if (args.length != 1){
                    error("pwd: Incorrect arguments.");
                }
                try {
                    System.out.println(new File(currPath).getCanonicalPath());
                } catch (Exception ex) {
                    error("pwd: " + ex.getMessage());
                }
                break;

            case "rm":
                if (args.length != 2){
                    error("rm: Incorrect arguments.");
                }
                args[1] = createAbsolute(args[1]);
                File rmFile = new File(args[1]);
                rm(rmFile,args[0]);
                break;

            case "cp":
                if (args.length != 3) {
                    error("cp: Incorrect arguments.");
                }
                args[1] = createAbsolute(args[1]);
                args[2] = createAbsolute(args[1]);
                cp(new File(args[1]),new File(args[2]),args[0]);
                break;

            case "mv":
                if (args.length != 3){
                    error("mv: Incorrect arguments.");
                }
                args[1] = createAbsolute(args[1]);
                args[2] = createAbsolute(args[2]);
                cp(new File(args[1]),new File(args[2]),args[0]);
                rm(new File(args[1]),args[0]);
                break;

            case "dir":
                if (args.length != 1){
                    error("dir: Incorrect arguments.");
                }
                dir();
                break;

            case "exit":
                if (args.length != 1){
                    error("exit: Incorrect arguments.");
                }
                System.exit(0);
                break;

            default:
                error("Incorrect command.");
        }
    }

    public static void main(String[] args) {
        String[] commands;
        currPath = new File("").getAbsolutePath();
        if (args.length == 0){
            console = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try{
                while (true){
                    System.out.print("$ ");
                    String incomingCommands = reader.readLine();
                    commands = incomingCommands.split(";");
                    for(String command:commands){
                        exec(command);
                    }
                }
            } catch (Exception ex) {
                error(ex.getMessage());
                System.exit(1);
            } finally {
                Utils.tryClose(reader);
            }
        } else {
            console = false;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; ++i ) {
                sb.append(args[i]).append(' ');
            }
            commands = sb.toString().split("\\s*;\\s*");
            for(String command:commands){
                exec(command);
            }
        }
    }
}

