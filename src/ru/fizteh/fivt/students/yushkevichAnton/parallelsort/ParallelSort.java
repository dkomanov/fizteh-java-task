package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.util.*;

public class ParallelSort {
    static boolean preFiltering = false;

    private IOHandler io           = new IOHandler();
    private boolean   uniqueMode   = false;
    private boolean   ignoringCase = false;

    private int threadCount = Runtime.getRuntime().availableProcessors();

    private Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public static void main(String[] args) {
        new ParallelSort().run(args);
    }

    private void processArguments(String[] args) {
        int start = 0;
        for (; start < args.length && args[start].charAt(0) == '-'; start++) {
            for (int i = 1; i < args[start].length(); i++) {
                char c = args[start].charAt(i);
                switch (c) {
                    case 'i':
                        comparator = String.CASE_INSENSITIVE_ORDER;
                        ignoringCase = true;
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
    }

    private void run(String[] args) {
        processArguments(args);

        ArrayList<String> strings = new ArrayList<String>();
        // a try to rehabilitate preFiltering method
        Set<String> uniqueStrings = new CaseInsensitiveStringHashSet();
        // fail
        // postFiltering is better (80 against 180), is now default
        while (true) {
            String s = io.readLine();
            if (s == null) {
                break;
            }
            if (uniqueMode && preFiltering) {
                if (ignoringCase) {
                    String lowerCase = myToLowercase(s);
                    if (!uniqueStrings.contains(lowerCase)) {
                        uniqueStrings.add(lowerCase);
                        strings.add(s);
                    }
                } else {
                    if (!uniqueStrings.contains(s)) {
                        uniqueStrings.add(s);
                        strings.add(s);
                    }
                }
            } else {
                strings.add(s);
            }
        }

        new MasterSorter().sort(strings, comparator, threadCount);

        if (uniqueMode && !preFiltering) {
            ArrayList<String> newStrings = new ArrayList<String>();
            for (int i = 0; i < strings.size(); i++) {
                if (i > 0 && comparator.compare(strings.get(i), strings.get(i - 1)) == 0) {
                    continue;
                }
                newStrings.add(strings.get(i));
            }
            strings = newStrings;
        }

        for (String s : strings) {
            io.println(s);
        }

        io.close();
    }

    public static String myToLowercase(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : s.toCharArray()) {
            stringBuilder.append(Character.toLowerCase(c));
        }
        return stringBuilder.toString();
    }
}