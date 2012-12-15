package ru.fizteh.fivt.students.altimin.sorter;

import ru.fizteh.fivt.students.altimin.ArgumentsParser;

import java.io.*;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * User: altimin
 * Date: 11/21/12
 * Time: 5:46 AM
 */

public class ParallelSorterRunner {

    private static final int MAX_THREADS = 1000;

    public static void main(String[] args) throws IOException {
        ArgumentsParser argumentsParser = new ArgumentsParser();
        argumentsParser.addKey("i");
        argumentsParser.addKey("u");
        argumentsParser.addKey("t", true);
        argumentsParser.addKey("o", true);
        ArgumentsParser.ParseResult parsedArgs = null;
        try {
            parsedArgs = argumentsParser.parse(args);
        } catch (KeyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        PrintWriter printer = null;
        boolean hasOpenedFile = false;
        try {
            if (!parsedArgs.hasProperty("o")) {
                printer = new PrintWriter(System.out);
            } else {
                printer = new PrintWriter(new FileWriter(parsedArgs.getProperty("o")));
                hasOpenedFile = true;
            }
            List<String> array = new ArrayList<String>();
            if (parsedArgs.other.length == 0) {
                Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
                try {
                    while (scanner.hasNext()) {
                        array.add(scanner.next());
                    }
                } finally {
                    try {
                        scanner.close();
                    } catch (Exception e){
                    }
                }
            } else {
                for (String fileName: parsedArgs.other) {
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(new BufferedReader(new FileReader(new File(fileName))));
                        while (scanner.hasNext()) {
                            array.add(scanner.next());
                        }
                    } catch (FileNotFoundException e){
                        System.err.println(e.toString());
                        System.exit(1);
                    } finally {
                        try {
                            scanner.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
            Comparator<String> comparator;
            if (!parsedArgs.hasProperty("i")) {
                comparator = new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                };
            } else {
                comparator = String.CASE_INSENSITIVE_ORDER;
            }

            ParallelSorter<String> parallelSorter = new ParallelSorter<String>(comparator, String.class);
            int value = MAX_THREADS;
            if (parsedArgs.hasProperty("t")) {
                try {
                    value = Integer.parseInt(parsedArgs.getProperty("t"));
                } catch (NumberFormatException e) {
                    System.err.println(e.toString());
                    System.exit(1);
                }
            }
            String[] result = parallelSorter.sort(array.toArray(new String[0]), value);
            boolean printUnique = parsedArgs.hasProperty("u");
            String prevValue = null;
            for (int i = 0; i < result.length; i ++) {
                String curValue = result[i];
                if (i == 0 || (comparator.compare(prevValue, curValue) != 0 || !printUnique)) {
                    printer.println(curValue);
                }
                prevValue = curValue;
            }
        } finally {
            if (hasOpenedFile) {
                printer.flush();
                try {
                    printer.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
