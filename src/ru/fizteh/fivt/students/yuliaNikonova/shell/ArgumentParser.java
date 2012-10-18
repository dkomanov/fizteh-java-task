package ru.fizteh.fivt.students.yuliaNikonova.shell;

public class ArgumentParser {
	private String[] mArgs;
	public ArgumentParser (String[] args) {
		mArgs = args;
	}
	
	public String parse() {
		StringBuilder myCommandBuilder = new StringBuilder("");
		for (String argument : mArgs) {
			myCommandBuilder.append(argument);
			myCommandBuilder.append(" ");
		}
		return myCommandBuilder.toString();
	}

}
