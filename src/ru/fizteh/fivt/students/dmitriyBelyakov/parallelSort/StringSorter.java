package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.io.*;
import java.util.ArrayList;

public class StringSorter {
    static private void readStrings(ArrayList<String> valuesForSorting, String fileName, boolean minusI) throws IOException {
        BufferedReader reader = null;
        FileReader fReader = null;
        try {
            InputStreamReader iSReader;
            if (fileName == null) {
                iSReader = new InputStreamReader(System.in);
                reader = new BufferedReader(iSReader);
            } else {
                fReader = new FileReader(new File(fileName));
                reader = new BufferedReader(fReader);
            }
            String str;
            while ((str = reader.readLine()) != null) {
                if (minusI) {
                    str = str.toLowerCase();
                }
                valuesForSorting.add(str);
            }
        } finally {
            IoUtils.close(reader);
            IoUtils.close(fReader);
        }
    }

    public static void sort(ArrayList<String> fileNames, boolean minusI, boolean minusU, int countOfThreads, String outFileName) throws Exception {
        ArrayList<String> valuesForSorting = new ArrayList<>();
        if (fileNames.size() == 0) {
            readStrings(valuesForSorting, null, minusI);
        } else {
            for (String fileName : fileNames) {
                readStrings(valuesForSorting, fileName, minusI);
            }
        }
        ArrayList<SimpleSorter> sorters = new ArrayList<>();
        countOfThreads = countOfThreads > valuesForSorting.size() ? valuesForSorting.size() : countOfThreads;
        int length = valuesForSorting.size() / countOfThreads;
        for (int i = 0; i < countOfThreads; ++i) {
            SimpleSorter tmpSorter;
            if (i != countOfThreads - 1) {
                tmpSorter = new SimpleSorter(valuesForSorting.subList(i * length, (i + 1) * length));
            } else {
                tmpSorter = new SimpleSorter(valuesForSorting.subList(i * length, valuesForSorting.size()));
            }
            sorters.add(tmpSorter);
            tmpSorter.start();
        }
        for (SimpleSorter s : sorters) {
            s.join();
        }
        FileWriter writer = null;
        try {
            if (outFileName != null) {
                writer = new FileWriter(outFileName);
            }
            int countOfValues = valuesForSorting.size();
            String last = new String();
            for (int i = 0; i < countOfValues; ++i) {
                String min = null;
                SimpleSorter sorter = null;
                for (int j = 0; j < sorters.size(); ++j) {
                    if (sorters.get(j).hasValue()) {
                        String tmp = sorters.get(j).currentValue();
                        if (min == null || min.compareTo(tmp) > 0) {
                            min = tmp;
                            sorter = sorters.get(j);
                        }
                    }
                }
                if (!minusU || !last.equals(min)) {
                    if (outFileName == null) {
                        System.out.println(min);
                    } else {
                        writer.write(min);
                        writer.write(System.lineSeparator());
                    }
                }
                last = min;
                sorter.nextValue();
            }
        } finally {
            IoUtils.close(writer);
        }
    }
}