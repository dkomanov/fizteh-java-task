package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class StringSorter {
    static private void readStrings(ArrayList<String> valuesForSorting, String fileName, boolean minusU, HashSet<String> usedValues) throws IOException {
        InputStreamReader iSReader = null;
        BufferedReader reader = null;
        FileReader fReader = null;
        try {
            if(fileName == null) {
                iSReader = new InputStreamReader(System.in);
                reader = new BufferedReader(iSReader);
            } else {
                fReader = new FileReader(new File(fileName));
                reader = new BufferedReader(fReader);
            }
            String str;
            while ((str = reader.readLine()) != null) {
                if (!minusU || !usedValues.contains(str)) {
                    valuesForSorting.add(str);
                    if (minusU) {
                        usedValues.add(str);
                    }
                }
            }
        } finally {
            IoUtils.close(reader);
            IoUtils.close(iSReader);
            IoUtils.close(fReader);
        }
    }

    public static String sort(ArrayList<String> fileNames, boolean minusI, boolean minusU, int countOfThreads, String outFileName) throws Exception {
        HashSet<String> usedValues = new HashSet<String>();
        ArrayList<String> valuesForSorting = new ArrayList<String>();
        if(fileNames.size() == 0) {
            readStrings(valuesForSorting, null, minusU, usedValues);
        } else {
            for(String fileName: fileNames) {
                readStrings(valuesForSorting, fileName, minusU, usedValues);
            }
        }
        ArrayList<SimpleSorter> sorters = new ArrayList<SimpleSorter>();
        countOfThreads = countOfThreads > valuesForSorting.size() ? valuesForSorting.size() : countOfThreads;
        int leng = valuesForSorting.size() / countOfThreads;
        System.out.println(valuesForSorting);
        for(int i = 0; i < countOfThreads; ++i) {
            SimpleSorter tmpSorter;
            if(i != countOfThreads - 1) {
                tmpSorter = new SimpleSorter(valuesForSorting.subList(i * leng, (i + 1) * leng));
            } else {
                tmpSorter = new SimpleSorter(valuesForSorting.subList(i * leng, valuesForSorting.size()));
            }
            sorters.add(tmpSorter);
            tmpSorter.start();
        }
        for(SimpleSorter s: sorters) {
            s.join();
        }
        int countOfValues = valuesForSorting.size();
        ArrayList<String> sortedValues = new ArrayList<String>();
        //Thread t = Thread.currentThread();
        //t.join(); Cool :)
        for(int i = 0; i < countOfValues; ++i) {
            String min = null;
            SimpleSorter sorter = null;
            for(int j = 0; j < sorters.size(); ++j) {
                if(sorters.get(j).hasValue()) {
                    String tmp = sorters.get(j).currentValue();
                    if(min == null || min.compareTo(tmp) > 0) {
                        min = tmp;
                        sorter = sorters.get(j);
                    }
                }
            }
            sortedValues.add(min);
            sorter.nextValue();
        }
        System.out.println(sortedValues);
        return null;
    }
}