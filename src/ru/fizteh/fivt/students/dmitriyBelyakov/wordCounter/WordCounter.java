package ru.fizteh.fivt.students.dmitriyBelyakov.wordCounter;

import java.util.*;
import java.io.*;

enum Mode {NONE, MINUS_L, MINUS_W}
enum ModeUnique {NONE, MINUS_U, MINUS_BIG_U}

public class WordCounter {
	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Use: java WordCounter [keys] FILE1 FILE2 ...");
			System.exit(1);
		}
		int returnCode = 0;
		Mode mode = Mode.NONE;
		ModeUnique unique = ModeUnique.NONE;
		boolean minus_a = false;
		ArrayList<String> fileNames = new ArrayList<String>();
		for(String s: args) {
			if(s.charAt(0) == '-') {
				for(int i = 1; i < s.length(); ++i) {
					char c = s.charAt(i);
					if(c == 'w') {
						if(mode != Mode.NONE) {
							System.err.println("Error: conflict of flags.");
							System.exit(-1);
						}
						mode = Mode.MINUS_W;
					} else if(c == 'l') {
						if(mode != Mode.NONE) {
							System.err.println("Error: conflict of flags.");
							System.exit(-1);
						}
						mode = Mode.MINUS_L;
					} else if(c == 'u') {
						unique = ModeUnique.MINUS_U;
					} else if(c == 'U') {
						unique = ModeUnique.MINUS_BIG_U;
					} else if(c == 'a') {
						minus_a = true;
					}
				}
			} else {
				fileNames.add(s);
			}
		}
		if(mode == Mode.NONE) {
			mode = Mode.MINUS_W;
		}
		ArrayList<String> strings = new ArrayList<String>();
		if(minus_a) {
			StringBuilder builder = new StringBuilder();
			for(String fileName: fileNames) {
				FileInputStream fileIn = null;
				try{
					fileIn = new FileInputStream(fileName);
				} catch(Exception e) {
					System.err.println("Error: " + e.getMessage());
					System.exit(-1);
				}
				int i = 0;
				try {
					while((i = fileIn.read()) != -1) {
						builder.append((char)i);
					}
					fileIn.close();
				} catch(IOException e) {
					System.err.println("Error: " + e.getMessage());
					System.exit(-1);
				}
				builder.append(" ");
			}
			strings.add(builder.toString());
		} else {
			for(int i = 0; i < fileNames.size(); ) {
				FileInputStream fileIn = null;
				StringBuilder builder = new StringBuilder();
				try{
					fileIn = new FileInputStream(fileNames.get(i));
				} catch(Exception e) {
					System.err.println("Error: " + e.getMessage());
					fileNames.remove(i);
					returnCode = -1;
					continue;
				}
				int ic = 0;
				try {
					while((ic = fileIn.read()) != -1) {
						builder.append((char)ic);
					}
					fileIn.close();
				} catch(IOException e) {
					System.err.println("Error: " + e.getMessage());
					fileNames.remove(i);
					returnCode = -1;
					continue;
				}
				strings.add(builder.toString());
				++i;
			}
		}
		if(fileNames.isEmpty()) {
			System.err.println("Error: list of files is empty.");
			System.exit(-1);
		}
		System.out.print(makeCalculations(fileNames, strings, mode, unique, minus_a));
		if(returnCode != 0) {
			System.exit(returnCode);
		}
	}

	static String makeCalculations(ArrayList<String> fileNames, ArrayList<String> strings, Mode mode, ModeUnique unique, boolean minus_a) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < strings.size(); ++i) {
			if(!minus_a) {
				builder.append(fileNames.get(i));
				builder.append(":\n");
			}
			String s = strings.get(i);
			String[] words;
			if(mode == Mode.MINUS_W) {
				words = s.split("\\s+");
			} else {
				words = s.split("(\\n)|(\\r\\n)");
			}
			if(unique == ModeUnique.NONE) {
				builder.append(words.length);
				builder.append("\n");
			} else if(unique == ModeUnique.MINUS_U) {
				HashMap<String, Integer> countOfWords = new HashMap<String, Integer>();
				for(String word: words) {
					word = word.replaceAll("(^\\s+)|(\\s+$)", "");
					if(countOfWords.containsKey(word)) {
						countOfWords.put(word, countOfWords.get(word) + 1);
					} else {
						countOfWords.put(word, 1);
					}
				}
				Set<String> keys = countOfWords.keySet();
				for(String key: keys) {
					builder.append(key);
					builder.append(" ");
					builder.append(countOfWords.get(key));
					builder.append("\n");
				}
			} else if(unique == ModeUnique.MINUS_BIG_U) {
				HashMap<String, Integer> countOfWords = new HashMap<String, Integer>();
				for(String word: words) {
					word = word.replaceAll("(^\\s+)|(\\s+^)", "");
					if(countOfWords.containsKey(word.toLowerCase())) {
						countOfWords.put(word.toLowerCase(), countOfWords.get(word.toLowerCase()) + 1);
					} else {
						countOfWords.put(word.toLowerCase(), 1);
					}
				}
				Set<String> keys = countOfWords.keySet();
				for(String key: keys) {
					builder.append(key);
					builder.append(" ");
					builder.append(countOfWords.get(key));
					builder.append("\n");
				}
			}
		}
		return builder.toString();
	}
}
