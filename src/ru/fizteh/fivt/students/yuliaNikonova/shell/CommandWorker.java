package ru.fizteh.fivt.students.yuliaNikonova.shell;

public class CommandWorker {
	public CommandWorker() {};
	public void executeCommand(String command) {
		command.replace("^\\s+", "");
		command.replace("$\\s+", "");
		
	}

}
