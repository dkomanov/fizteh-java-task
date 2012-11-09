package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.io.*;
import java.util.ArrayList;

public class StringSorter {
    private ArrayList<SimpleSorter> sorters;
    private volatile ArrayList<String> valuesForSorting;

    StringSorter() {
        sorters = new ArrayList<>();
        valuesForSorting = new ArrayList<>();
    }

    private void readStrings(ArrayList<String> valuesForSorting, String fileName) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(fileName);
            readStringsFromStream(valuesForSorting, stream);
        } finally {
            IoUtils.close(stream);
        }
    }

    private void readStrings(ArrayList<String> valuesForSorting) throws IOException {
        readStringsFromStream(valuesForSorting, System.in);
    }

    private void readStringsFromStream(ArrayList<String> valuesForSorting, InputStream stream) throws IOException{
        BufferedReader bufReader = null;
        InputStreamReader iSReader = null;
        try {
            iSReader = new InputStreamReader(stream);
            bufReader = new BufferedReader(iSReader);
            String str;
            while ((str = bufReader.readLine()) != null) {
                valuesForSorting.add(str);
            }
        } finally {
            IoUtils.close(bufReader);
            IoUtils.close(iSReader);
        }
    }

    private void makeSortersForMerge(ArrayList<String> valuesForSorting, boolean ignoreCase, int countOfThreads) {
        sorters.clear();
        if (valuesForSorting.size() == 0) {
            throw new RuntimeException("nothing found for sorting.");
        }
        countOfThreads = countOfThreads > valuesForSorting.size() ? valuesForSorting.size() : countOfThreads;
        int length = valuesForSorting.size() / countOfThreads;
        for (int i = 0; i < countOfThreads; ++i) {
            SimpleSorter tmpSorter;
            if (i != countOfThreads - 1) {
                tmpSorter = new SimpleSorter(valuesForSorting.subList(i * length, (i + 1) * length), ignoreCase);
            } else {
                tmpSorter = new SimpleSorter(valuesForSorting.subList(i * length, valuesForSorting.size()), ignoreCase);
            }
            sorters.add(tmpSorter);
            tmpSorter.start();
        }
        for (SimpleSorter s : sorters) {
            try {
                s.join();
            } catch (Throwable t) {
            }
        }
    }

    private String nextValueForMerge(boolean ignoreCase) {
        String min = null;
        SimpleSorter sorter = null;
        for (int j = 0; j < sorters.size(); ++j) {
            if (sorters.get(j).hasValue()) {
                String tmp = sorters.get(j).currentValue();
                if (min == null || (ignoreCase && min.compareToIgnoreCase(tmp) > 0)
                        || ((!ignoreCase && min.compareTo(tmp) > 0))) {
                    min = tmp;
                    sorter = sorters.get(j);
                }
            }
        }
        sorter.nextValue();
        if (min == null) {
            throw new RuntimeException("values not found.");
        }
        return min;
    }

    private void printSortedValues(boolean uniqueOnly, boolean ignoreCase) {
        printSortedValuesToStream(uniqueOnly, ignoreCase, System.out);
    }

    private void printSortedValues(boolean uniqueOnly, boolean ignoreCase, String outFileName) {
        PrintStream stream = null;
        try {
            stream = new PrintStream(outFileName);
            printSortedValuesToStream(uniqueOnly, ignoreCase, stream);
        } catch (Throwable t) {
            throw new RuntimeException("cannot open file for write.");
        } finally {
            IoUtils.close(stream);
        }
    }

    private void printSortedValuesToStream(boolean uniqueOnly, boolean ignoreCase, PrintStream stream) {
        int countOfValues = valuesForSorting.size();
        String last = new String();
        for (int i = 0; i < countOfValues; ++i) {
            String nextValue = nextValueForMerge(ignoreCase);
            if (!uniqueOnly || (ignoreCase && !last.equalsIgnoreCase(nextValue)) || (!ignoreCase && !last.equals(nextValue))) {
                stream.println(nextValue);
            }
            last = nextValue;
        }
    }

    private void readStringsFromListOfFiles(ArrayList<String> fileNames) throws Exception {
        if (fileNames.size() == 0) {
            readStrings(valuesForSorting);
        } else {
            for (String fileName : fileNames) {
                readStrings(valuesForSorting, fileName);
            }
        }
    }

    public void sortStrings(ArrayList<String> fileNames, boolean ignoreCase, boolean uniqueOnly, int countOfThreads) throws Exception {
        readStringsFromListOfFiles(fileNames);
        makeSortersForMerge(valuesForSorting, ignoreCase, countOfThreads);
        printSortedValues(uniqueOnly, ignoreCase);
    }

    public void sortStrings(ArrayList<String> fileNames, boolean ignoreCase, boolean uniqueOnly, int countOfThreads, String outFileName) throws Exception {
        readStringsFromListOfFiles(fileNames);
        makeSortersForMerge(valuesForSorting, ignoreCase, countOfThreads);
        printSortedValues(uniqueOnly, ignoreCase, outFileName);
    }
}
