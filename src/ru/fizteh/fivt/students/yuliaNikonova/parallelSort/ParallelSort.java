package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.ArrayList;

public class ParallelSort {
    public static void main(String[] args) {
        if (args.length == 0) {
            help();
        }

        boolean ignoreCase = false;
        boolean unique = false;
        int numthreads = 0;
        String outputFileName = "";
        ArrayList<String> fileNames = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                if (ignoreCase) {
                    exitError("too many keys");
                }

                ignoreCase = true;

            } else if (args[i].equals("-u")) {
                if (unique) {
                    exitError("too many keys");
                }
                unique = true;
            } else if (args[i].equals("-iu") || args[i].equals("-ui")) {
                if (unique || ignoreCase) {
                    exitError("too many keys");
                }

                unique = true;
                ignoreCase = true;
            } else if (args[i].equals("-o")) {
                if (i == args.length - 1) {
                    exitError("wrong use of key -o");
                }

                outputFileName = args[++i];
            } else if (args[i].equals("-t")) {
                if (i == args.length - 1) {
                    exitError("wrong use of key -t");
                }

                try {
                    numthreads = Integer.parseInt(args[++i]);
                } catch (Exception e) {
                    exitError(e.getMessage());
                }
                if (numthreads < 0) {
                    exitError("wrong number of threads");
                }
            } else {
                fileNames.add(args[i]);
            }

        }

        ControlSorter mSorter = new ControlSorter(ignoreCase, unique, numthreads, outputFileName, fileNames);
    }

    private static void help() {
        System.out.println("Usage: " + "ParallelSort [-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
        System.out.println("Keys:");
        System.out.println("-i - ignore case");
        System.out.println("-u - unique only");
        System.exit(1);
    }

    static private void exitError(String message) {
        System.err.println(message);
        System.exit(1);
    }

}
