package ru.fizteh.fivt.students.yuliaNikonova.shell;

import java.util.Scanner;

public class Shell {
	public static void main(String [] args) {
		ArgumentParser argParser = new ArgumentParser(args);
		if (args.length > 0) {
			String commandLine = argParser.parse();
			System.out.println(commandLine);
			execute(commandLine);
		} else {
			boolean work = true;
			Scanner in = new Scanner(System.in);
			while (work) {
				System.out.print("$ ");
				String commandLine = in.nextLine();
				execute(commandLine);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				
			}
		}
	}
	
	private static void execute(String commandLine) {
		boolean err = false;
		String[] commands = commandLine.split(";");
		CommandWorker worker = new CommandWorker();
		for (String command:commands) {
			command = command.replaceAll("^\\s+", "");
			command = command.replaceAll("$\\s+", "");
			if (command.equals("exit")) {
				if (err) {
					System.exit(1);
				} else {
					System.exit(0);
				}
			}
			try {
				worker.executeCommand(command);
			} catch (Exception e) {
				err = true;
				System.err.println(e.getMessage());
			}
		}
	}
}
