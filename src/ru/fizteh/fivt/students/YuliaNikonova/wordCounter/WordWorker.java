package ru.fizteh.fivt.students.YuliaNikonova.wordCounter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class WordWorker {
	private static boolean mWords;
	private static boolean mLines;
	private static boolean mUniqueCaseSensitive;
	private static boolean mUniqueNotCaseSensitive;
	private static boolean mAggregation;
	private static ArrayList<String> mFilenames = new ArrayList<String>();
	
	public WordWorker(boolean word, boolean lines, boolean uniqueCaseSensitive, boolean uniqueNotCaseSensitive, boolean aggregation, ArrayList<String> filenames) {
		
		mWords = word;
		mLines = lines;
		mUniqueCaseSensitive = uniqueCaseSensitive;
		mUniqueNotCaseSensitive = uniqueNotCaseSensitive;
		mAggregation = aggregation;
		mFilenames = filenames;
		
	}
	
	public void count() {
		int all_words = 0;
		int all_lines = 0;
		HashMap<String, Integer> allUnique = new HashMap<String, Integer>();
		HashMap<String, Integer> allUniqueNotCaseSens = new HashMap<String, Integer>();
		for (int i=0; i<mFilenames.size(); i++)
		{
			try {
				FileInputStream fstream = new FileInputStream(mFilenames.get(i));
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				int lines = 0;
				int words = 0;
				HashMap<String, Integer> unique = new HashMap<String, Integer>();
				HashMap<String, Integer> uniqueNotCaseSens = new HashMap<String, Integer>();
				while ((strLine = br.readLine()) != null) {
					if (mLines) {
						if (mAggregation) {
							all_lines++;
						} else {
							lines++;
						}	
					}
					if (mWords || mUniqueNotCaseSensitive || mUniqueCaseSensitive) {
						String[] result = strLine.split("\\s");
					    for (int x = 0; x < result.length; x++) {
					    	if (result[x].length() < 1)
					    		continue;
					    	if (mWords) {
					    		if (mAggregation) {
					    			all_words++;
					    		} else {
					    			words++;
					    		}	
					    	}
					    	if (mUniqueCaseSensitive) { 
					    		if (mAggregation) {
					    			putValueInMap(allUnique, result[x]);
					    		} else {
					    			putValueInMap(unique, result[x]);
					    		}
					    	}
					    	
					    	if (mUniqueNotCaseSensitive) {
					    		if (mAggregation) {
					    			putValueInMap(allUniqueNotCaseSens,result[x].toLowerCase());
					    		} else {
						    		putValueInMap(uniqueNotCaseSens, result[x].toLowerCase());
					    		}
					    	}
					    }				        
					}			
				}
				
				in.close();
				fstream.close();
				br.close();
				if (!mAggregation) {
					System.out.println(mFilenames.get(i));
					printResults(words, lines, unique, uniqueNotCaseSens);
				}
			
			} catch (Exception e){
				System.out.println(e.getMessage());	
			}
		}
		if (mAggregation) {
			printResults(all_words, all_lines, allUnique, allUniqueNotCaseSens);
		}		
	}

	private void putValueInMap(HashMap<String, Integer> myMap, String key) {
		if (myMap.containsKey(key)) {
			myMap.put(key, myMap.get(key) + Integer.valueOf(1));
		} else {
			myMap.put(key, Integer.valueOf(1));
		}
	}
	
	private void printMap(HashMap<String, Integer> myMap) {
		for (String key: myMap.keySet())
		    System.out.println(key + " " + myMap.get(key));	
	}
	
	private void printResults(int words, int lines, HashMap<String, Integer> myMap1, HashMap<String, Integer> myMap2) {
		if (mWords) {
			System.out.println(words);
		} else if (mLines) {
			System.out.println(lines);
		}
		
		if (mUniqueCaseSensitive) {
			printMap(myMap1);
		} else if (mUniqueNotCaseSensitive) {
			printMap(myMap2);
		}	
	}
}
