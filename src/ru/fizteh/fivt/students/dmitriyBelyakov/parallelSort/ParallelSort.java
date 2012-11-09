package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.util.ArrayList;

public class ParallelSort {
    public static void main(String[] args) {
        boolean ignoreCase = false;
        boolean uniqueOnly = false;
        int countOfThreads = 1;
        String outFileName = null;
        ArrayList<String> fileNames = new ArrayList<>();
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-u")) {
                if (uniqueOnly) {
                    exitWithErrorMessage("Error: big count of arguments.");
                }
                uniqueOnly = true;
            } else if (args[i].equals("-i")) {
                if (ignoreCase) {
                    exitWithErrorMessage("Error: big count of arguments.");
                }
                ignoreCase = true;
            } else if (args[i].equals("-ui") || args[i].equals("-iu")) {
                if (uniqueOnly || ignoreCase) {
                    exitWithErrorMessage("Error: big count of arguments.");
                }
                uniqueOnly = true;
                ignoreCase = true;
            } else if (args[i].equals("-t")) {
                if (i == args.length - 1) {
                    exitWithErrorMessage("Error: incorrect keys.");
                }
                try {
                    countOfThreads = Integer.parseInt(args[++i]);
                } catch (Exception e) {
                    exitWithErrorMessage("Error: incorrect count of threads.");
                }
            } else if (args[i].equals("-o")) {
                if (i == args.length - 1) {
                    exitWithErrorMessage("Error: incorrect keys.");
                }
                outFileName = args[++i];
            } else if (args[i].charAt(0) == '-') {
                exitWithErrorMessage("Error: unknown key.");
            } else {
                fileNames.add(args[i]);
            }
        }
        if (countOfThreads <= 0) {
            exitWithErrorMessage("Error: cannot create <= 0 threads.");
        }
        try {
            StringSorter sorter = new StringSorter();
            if (outFileName == null) {
                sorter.sortStrings(fileNames, ignoreCase, uniqueOnly, countOfThreads);
            } else {
                sorter.sortStrings(fileNames, ignoreCase, uniqueOnly, countOfThreads, outFileName);
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exitWithErrorMessage("Error: " + e.getMessage());
            } else {
                exitWithErrorMessage("Error: unknown.");
            }
        }
    }

    static private void exitWithErrorMessage(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
