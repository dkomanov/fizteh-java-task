package ru.fizteh.fivt.kogemrka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Класс имитации shell`а выражения
 * 
 * @author Антонов Никита
 */
public class SuperShell {
	
	public static void main(String args[]) {
		try {
			if (args.length > 0) {
				runWithParams(args);
			} else {
				runInteractive();
			}
		} catch (IOException e) {
			System.err.println("Unknown IO error");
			System.exit(1);
		}
	}
	
	public static void runWithParams(String args[]) throws IOException {
		String str = concat(args);

		if (str.trim().isEmpty()) {
			runInteractive();
			return;
		}
		
		/* TODO: Make executor method */
		execute(str);	
	}
	
	public static void runInteractive() throws IOException {
		System.out.println("Write arithmethic expression to calc or 'quit' to exit");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = in.readLine();
		
		while (!s.equals("quit")) {
						
			if (s.trim().isEmpty()) {
				s = in.readLine();
				continue;
			}
		
			execute(s);
			
			s = in.readLine();
		}
	}
	
	private static String concat(String args[]) {
		StringBuilder result = new StringBuilder();

		for (String s : args) {
			result.append(s);
		}

		return result.toString();
	}	
	
	public static void execute(String str) throws Exception {
		String parts[] = str.split(" ");
		
		if (parts.length == 0)
			return;
		
		switch (parts[0].toLowerCase())
		{
		case "cd":
			doCd(parts);
			break;
		case "mkdir":
			doMkdir(parts);
			break;
		case "pwd":
			doPwd();
			break;
		case "rm":
			doRm(parts);
			break;
		case "cp":
			doCp(parts);
			break;
		case "mv":
			doMv(parts);
			break;
		case "dir":
			doDir(parts);
			break;
		case "exit":
			System.exit(0);
			Arrays.sor`
			break;
		default:
			throw new Exception("Unknown command " + parts[0]);	
		}
	}
	
	private static void doCd(String parts[]) {
		
	}
	
	private static void doMkdir(String parts[]) {
		
	}
	
	private static void doPwd() {
		System.out.println(System.getProperty("user.dir"));
	}

	private static void doRm(String parts[]) {
		
	}
	
	private static void doCp(String parts[]) {
	}
	
	private static void doMv(String parts[]) {
		
	}
	
	private static void doDir(String parts[]) {
		
	}

}
