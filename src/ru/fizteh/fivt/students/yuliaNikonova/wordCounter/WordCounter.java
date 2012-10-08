package ru.fizteh.fivt.students.yuliaNikonova.wordCounter;

import java.util.ArrayList;

public class WordCounter {
	private static boolean word;
	private static boolean lines;
	private static boolean uniqueCaseSensitive;
	private static boolean uniqueNotCaseSensitive;
	private static boolean aggregation;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			help();
			System.exit(0);
		}
		
		boolean keys_end = false;
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("-w");
		keys.add("-l");
		keys.add("-U");
		keys.add("-u");
		keys.add("-a");
		word = false;
		lines = false;
		uniqueCaseSensitive = false;
		uniqueNotCaseSensitive = false;
		aggregation = false;
		for (String arg:args)
		{
			if (keys.contains(arg) || (arg.length() > 1 && arg.charAt(0) == '-' && arg.indexOf('.') < 0)) {
				//System.out.println("jhgffj");
				if (keys_end) {
					System.out.println("Wrong usage");
					help();
					System.exit(0);
				}
				try {
					if (arg.length() == 2) {
						//System.out.println("lalala");
						parseKey(arg);
					} else {
						for (int i = 1; i < arg.length(); i++) {
							parseKey(String.valueOf(arg.charAt(i)));
						}
							
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.exit(1);
				}
					
			} else {
				keys_end = true;
				filenames.add(arg);
			}
		}
		
		if ((word && lines) || (uniqueCaseSensitive && uniqueNotCaseSensitive)) {
			System.out.println("can't combine these key");
			System.exit(0);
		}
		
		if (!(word || lines ||  uniqueCaseSensitive || uniqueNotCaseSensitive)) {
			word=true;
		}
			
		WordWorker myCounter = new WordWorker(word, lines, uniqueCaseSensitive, uniqueNotCaseSensitive, aggregation, filenames);
			
		try {
			myCounter.count();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	public static void help()
	{
		System.out.println("Usage: java WordCounter [keys] FILE1 FILE2 ...");
		System.out.println("Keys:");
		System.out.println("-w (default) - number of words");
		System.out.println("-l - number of lines");
		System.out.println("-u - unique elements, register is important");
		System.out.println("-U - unique elements, register isn't important");
		System.out.println("-a - aggregation of information");
	}
	
	public static void parseKey(String arg) throws Exception
	{
		if (arg.equals("-w") || arg.equals("w")) {
			word = true;
		} else if (arg.equals("-l") || arg.equals("l")) {
			lines = true;
		} else if (arg.equals("-a") || arg.equals("a")) {
			aggregation = true;
		} else if (arg.equals("-u") || arg.equals("u")) {
			uniqueCaseSensitive = true;	
		} else if (arg.equals("-U") || arg.equals("U")) {
			uniqueNotCaseSensitive = true;
		} else {
			throw new Exception("bad symbol in keys");
		}
	}

}
