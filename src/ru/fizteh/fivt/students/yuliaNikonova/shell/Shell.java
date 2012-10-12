package ru.fizteh.fivt.students.yuliaNikonova.shell;

public class Shell {
	public static void main(String [] args) {
		ArgumentParser argParser = new ArgumentParser(args);
		if (args.length>0) {
			String commandLine = argParser.parse();
			String[] commands = commandLine.split(";");
			CommandWorker worker = new CommandWorker();
			for (String command:commands) {
				worker.executeCommand(command);
			}
		}
	}

}
