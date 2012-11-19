package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.util.*;

public class ParallelSort {
    public static void main(String[] args) {
        IOHandler io = new IOHandler();

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        boolean uniqueMode = false;

        int threadCount = Runtime.getRuntime().availableProcessors();

        int start = 0;
        for (; start < args.length && args[start].charAt(0) == '-'; start++) {
            for (int i = 1; i < args[start].length(); i++) {
                char c = args[start].charAt(i);
                switch (c) {
                    case 'i':
                        comparator = String.CASE_INSENSITIVE_ORDER;
                        break;
                    case 'u':
                        uniqueMode = true;
                        break;
                    case 't':
                        if (args[start].length() > 2) {
                            System.err.println("Wrong syntax: " + args[start]);
                            System.exit(1);
                        }
                        try {
                            threadCount = Integer.parseInt(args[++start]);
                        } catch (Exception e) {
                            System.err.println("Wrong syntax");
                            System.exit(1);
                        }
                        i = args[start].length();
                        break;
                    case 'o':
                        if (args[start].length() > 2 || start + 1 >= args.length) {
                            System.err.println("Wrong syntax: " + args[start]);
                            System.exit(1);
                        }
                        io.setOutputFile(args[++start]);
                        i = args[start].length();
                        break;
                    default:
                        System.err.println("Unrecognized key: " + c);
                        System.exit(1);
                        break;
                }
            }
        }
        for (; start < args.length; start++) {
            io.addInputFile(args[start]);
        }

        ArrayList<String> strings = new ArrayList<String>();
        HashSet<String> uniqueStrings = new HashSet<String>();
        while (true) {
            String s = io.readLine();
            if (s == null) {
                break;
            }
            if (uniqueMode) {
                uniqueStrings.add(s);
            } else {
                strings.add(s);
            }
        }
        if (uniqueMode) {
            strings.addAll(uniqueStrings);
        }

        System.out.println("Finished reading");

        long startTime = System.nanoTime();
        new MasterSorter().sort(strings, comparator, threadCount);
        long executionTime = System.nanoTime() - startTime;

        for (String s : strings) {
            io.println(s);
        }

        System.out.println("Execution time");
        System.out.printf("%.2f seconds \n", executionTime / 1e9);

        io.close();
    }
}