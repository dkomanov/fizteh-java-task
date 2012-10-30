package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.util.ArrayList;

public class ParallelSort {
    public static void main(String[] args) {
        boolean minusI = false;
        boolean minusU = false;
        int countOfThreads = 1;
        String outFileName = null;
        ArrayList<String> fileNames = new ArrayList<String>();
        for(int i = 0; i < args.length; ++i) {
            if(args[i].equals("-u")) {
                if(minusU) {
                    System.err.println("Error: big count of arguments.");
                    System.exit(1);
                }
                minusU = true;
            } else if(args[i].equals("-i")) {
                if(minusI) {
                    System.err.println("Error: big count of arguments.");
                    System.exit(1);
                }
                minusI = true;
            } else if(args[i].equals("-ui") || args[i].equals("-iu")) {
                if(minusU || minusI) {
                    System.err.println("Error: big count of arguments.");
                    System.exit(1);
                }
                minusU = true;
                minusI = true;
            } else if(args[i].equals("-t")) {
                ++i;
                try {
                    countOfThreads = Integer.parseInt(args[i]);
                } catch(Exception e) {
                    System.err.println("Error: incorrect count of threads.");
                    System.exit(1);
                }
            } else if(args[i].equals("-o")) {
                ++i;
                outFileName = args[i];
            } else if(args[i].charAt(0) == '-') {
                System.err.println("Error: unknown key.");
                System.exit(1);
            } else {
                fileNames.add(args[i]);
            }
        }
        if(countOfThreads == 0) {
            System.err.println("Error: cannot create 0 threads.");
            System.exit(1);
        }
        try {
            StringSorter.sort(fileNames, minusI, minusU, countOfThreads, outFileName);
        } catch(Exception e) {
            if(e.getMessage() != null) {
                System.err.println("Error: " + e.getMessage());
            } else {
                System.out.println("Error: unknown.");
            }
        }
    }
}