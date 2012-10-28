package ru.fizteh.fivt.students.konstantinPavlov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WordCounter {

	static int settingL = 0;
	static int settingW = 0;
	static int settingNoRegister = 0;
	static int settingRegister = 0;
	static int settingA = 0;

	static int globalLinesAmount = 0;
	static int globalWordsAmount = 0;
	static Map<String, Integer> globalMap = new HashMap<String, Integer>();
	private static BufferedReader bufferedReader;

	static void analyzeFile(String path) throws IOException {

		File file = new File(path);
		if (!file.canRead()) {
			System.err.println("Error: file \"" + file.getPath()
					+ "\" can't be opened or readed");
			System.exit(1);
		}
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader streamReader = new InputStreamReader(fis);
		bufferedReader = new BufferedReader(streamReader);

		String str = new String();
		int localLinesAmount = 0;
		int localWordsAmount = 0;
		Map<String, Integer> localMap = new HashMap<String, Integer>();

		// start analyzing
		while ((str = bufferedReader.readLine()) != null) {
			// toLowerCase if register is not important
			if (settingNoRegister == 1) {
				str = str.toLowerCase();
			}

			if (settingW == 1) {
				// count words

				// parsing the string to words
				String[] arrayOfWords = str.split(" ");

				for (int i = 0; i < arrayOfWords.length; ++i) {
					// add word to our map
					if (settingA != 1) {
						if (localMap.containsKey(arrayOfWords[i])
								&& !arrayOfWords[i].equals(" ")
								&& !arrayOfWords[i].equals("\t")) {
							++localWordsAmount;
							localMap.put(arrayOfWords[i],
									localMap.get(arrayOfWords[i]) + 1);
						} else {
							if (!arrayOfWords[i].equals(" ")
									&& !arrayOfWords[i].equals("\t")) {
								++localWordsAmount;
								localMap.put(arrayOfWords[i], 1);
							}
						}
					} else {
						if (globalMap.containsKey(arrayOfWords[i])
								&& !arrayOfWords[i].equals(" ")
								&& !arrayOfWords[i].equals("\t")) {
							++globalWordsAmount;
							globalMap.put(arrayOfWords[i],
									globalMap.get(arrayOfWords[i]) + 1);
						} else {
							if (!arrayOfWords[i].equals(" ")
									&& !arrayOfWords[i].equals("\t")) {
								++globalWordsAmount;
								globalMap.put(arrayOfWords[i], 1);
							}
						}
					}
				}
			} else {
				// count lines
				if (settingNoRegister != 1 && settingRegister != 1) {
					if (settingA != 1) {
						++localLinesAmount;
					} else {
						++globalLinesAmount;
					}
				} else {
					// count lines without register
					if (settingA != 1) {
						if (localMap.containsKey(str) && !str.equals("")) {
							localMap.put(str, localMap.get(str) + 1);
						} else {
							if (!str.equals("")) {
								localMap.put(str, 1);
							}
						}
					} else {
						if (globalMap.containsKey(str) && !str.equals("")) {
							globalMap.put(str, globalMap.get(str) + 1);
						} else {
							if (!str.equals("")) {
								globalMap.put(str, 1);
							}
						}
					}
				}

			}

		}
		// finish analyzing

		// show information about analysis
		if (settingA != 1) {
			System.out.println(file.getPath() + ":");
			if ((settingW == 1 || settingL == 1)
					&& (settingRegister == 1 || settingNoRegister == 1)) {
				for (Map.Entry<String, Integer> entry : localMap.entrySet()) {
					System.out.println(entry.getKey() + " " + entry.getValue());
				}
			} else {
				if (settingW == 1) {
					System.out.println(localWordsAmount);
				} else {
					System.out.println(localLinesAmount);
				}
			}
		}

	}

	static void showAnalysisA() {
		if (settingA == 1) {
			if ((settingW == 1 || settingL == 1)
					&& (settingRegister == 1 || settingNoRegister == 1)) {
				for (Map.Entry<String, Integer> entry : globalMap.entrySet()) {
					System.out.println(entry.getKey() + " " + entry.getValue());
				}
			} else {
				if (settingW == 1) {
					System.out.println(globalWordsAmount);
				} else {
					System.out.println(globalLinesAmount);
				}
			}
		}
	}

	public static void setSettings(String[] args) {
		for (String str : args) {
			if (str.isEmpty() || str.charAt(0) != '-') {
				continue;
			} else {
				for (int i = 1; i < str.length(); ++i) {
					switch (str.charAt(i)) {
					case 'l':
						if (settingW == 1) {
							System.err.println("Error: flag w has already set");
							System.exit(1);
						}
						settingL = 1;
						break;

					case 'w':
						if (settingL == 1) {
							System.err.println("Error: flag l has already set");
							System.exit(1);
						}
						settingW = 1;
						break;

					case 'u':
						if (settingNoRegister == 1) {
							System.err.println("Error: flag U has alredy set");
							System.exit(1);
						}
						settingRegister = 1;
						break;

					case 'U':
						if (settingRegister == 1) {
							System.err.println("Error: flag u has alredy set");
							System.exit(1);
						}
						settingNoRegister = 1;
						break;

					case 'a':
						settingA = 1;
						break;

					default:
						System.err.println("Error: wrong flag: "
								+ str.charAt(i));
						System.exit(1);
						break;
					}
				}
			}
		}
		if(settingL==0 && settingW==0) settingW=0;
	}

	public static void main(String[] args) throws IOException {
		String path = new String();

		setSettings(args);

		for (int i = 0; i < args.length; ++i) {
			path = args[i];
			if (path.isEmpty() || path.charAt(0) == '-') {
				continue;
			}
			analyzeFile(path);
		}
		showAnalysisA();
	}
}