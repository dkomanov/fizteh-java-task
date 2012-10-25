package ru.fizteh.fivt.students.yuliaNikonova.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.*;

public class CommandWorker {
    static private File mPath = new File(".");
    public CommandWorker() {};
	public void executeCommand(String command) throws Exception {
		
		String commandArguments[] = command.split("\\s+");
		String commandName = commandArguments[0];
		if (commandName.equals("exit")) {
			throw new Exception("wrong number of arguments");
		} else if (commandName.equals("cd")) {
			changeDir(commandArguments);
		} else if (commandName.equals("mkdir")) {
			createDir(commandArguments);
		} else if (commandName.equals("pwd")) {
			printwd(commandArguments);
		} else if (commandName.equals("rm")) {
			removePF(commandArguments);
		} else if (commandName.equals("dir")) {
			printDir(commandArguments);
		} else if (commandName.equals("mv")) {
			moveDirectoryFile(commandArguments);
		} else if (commandName.equals("cp")) {
			copyFileorDir(commandArguments);
		} else {
			throw new Exception(commandName+": unknown command");
		}	
	}
	private void copyFileorDir(String[] commandArgs) throws Exception {
		if (commandArgs.length != 3) {
			throw new Exception("cp: wrong number of args");
		} else {
			File fileDir = getFile(commandArgs[1]);
			File destDir = getFile(commandArgs[2]);
			if (!destDir.isDirectory()) {
				throw new Exception("cp: cannot copy to \'"+commandArgs[2]+"\' No such directory");
			} else {
				if (fileDir.isFile()) {
					copyFile(commandArgs[1], commandArgs[2]);
				} else if (fileDir.isDirectory()) {
					copyDir(fileDir, destDir);
				} else {
					throw new Exception("cp: cannot copy \'" + commandArgs[1] + "\': No such file or directory");
				}
			}
		}	
	}
	
	private void copyDir(File original, File dest) throws Exception {
		
		String name=original.getName();
		File destination = new File(dest.getCanonicalPath()+File.separator+name);
		if (destination.exists()) {
			deleteDirectory(destination);
		}
		Thread.sleep(100);
		boolean rr = destination.mkdir();
		if (!rr) {
			throw new Exception("cp: cannot copy \'" + name + "\'");
		} else {
			String[] files = original.list();
			for (String file:files) {
				String fullName=original.getCanonicalPath()+ File.separator + file;
				File fileToCopy = getFile(fullName); 
				if (fileToCopy.isFile()) {
					copyFile(fullName, destination.getCanonicalPath());
				} 
				if (fileToCopy.getCanonicalFile().isDirectory()) {
					copyDir(fileToCopy, destination);
				}
			}
		}
	}
	private static void copyFile(String srFile, String dtFolder) throws Exception {
		InputStream in = null;
		OutputStream out = null;
		try {
			File inFile = getFile(srFile);
			File newFolder = getFile(dtFolder);
			File outFile = new File(newFolder, inFile.getName());
			outFile.createNewFile();
			
			in = new FileInputStream(inFile);
			
			out = new FileOutputStream(outFile);
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			
		} catch(FileNotFoundException ex) {
			throw new Exception("cp: \'"+srFile+"\'"+ex.getMessage());
		} catch(IOException e) {
			throw new Exception("cp: \'"+srFile+"\'"+e.getMessage());  
		} finally {
			if (in != null) {
				in.close();
			}
			
			if (out != null) {
				out.close();
			}
		}
	}
		
	
	private void moveDirectoryFile(String[] comms) throws Exception {
		if (comms.length != 3) {
			throw new Exception("mv: wrong number of args");
		} else {
			File file = getFile(comms[1]);
			
			File dir = getFile(comms[2]);

			boolean success = file.renameTo(new File(dir, file.getName()));
			if (!success) {
				throw new Exception("mv: cannot move \'" + comms[1] + "\'");
			}
		}
		
	}
	private void printDir(String[] comms) throws Exception {
		if (comms.length != 1) {
			throw new Exception("pwd: wrong number of args");
		} else {
			String [] pathFiles = mPath.list();
			for (String name:pathFiles) {
				System.out.println(name);
			}
		}
		
	}
	private void removePF(String[] comms) throws Exception {
		if (comms.length != 2) {
			throw new Exception("rm: wrong number of args");
		} else {
			boolean success = deleteDirectory(getFile(comms[1]));
			if (!success) {
				throw new Exception("rm: cannot remove \'" + comms[1] + "\'");
			}
		}	
	}
	public boolean deleteDirectory(File dir) {
		boolean success = true;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                success = success & deleteDirectory(f);
            }
            success = success & dir.delete();
        } else {
        	success = success & dir.delete();
        }
        return success;
    }
	
	private void printwd(String[] comms) throws Exception {
		if (comms.length != 1) {
			throw new Exception("pwd: wrong number of args");
		} else {
			System.out.println(mPath.getCanonicalPath());
		}
		
	}
	private void createDir(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			throw new Exception("mkdir: wrong number of args");
		} else {
			File newDir = getFile(commandArgs[1]);
			boolean success = newDir.mkdir();
			if (!success) {
				throw new Exception("mkdir: can't create \'"+commandArgs[1]+"\'");
			}
		}
	}
	private void changeDir(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			throw new Exception("cd: wrong number of args");
		} else {
			File newPath = getFile(commandArgs[1]);
			if (newPath.isDirectory()) {
				mPath = newPath;
			} else {
				throw new Exception("cd: \'" + commandArgs[1] + "\': No such file or directory");
			}
		}
		
	}
	
	private static File getFile(String name) throws IOException {
		File mfile = new File(name);
		if (name.equals(".")) {
			return mPath;
		} else if (name.equals("..")) {
			if (mPath.getName().equals(".")) {
				File path = new File(mPath.getCanonicalPath());
				return new File(path.getAbsoluteFile().getParent());
			} else {
				return new File(mPath.getAbsoluteFile().getParent());
			}
		}
		if (name.charAt(name.length()-1)==File.separatorChar) {
			name=name.substring(0, name.length()-1);
		}
		
		if (name.equals(mfile.getCanonicalPath())) {
			return mfile;
		} else {
			return new File(mPath, name);
		}
		
	}
	
}
