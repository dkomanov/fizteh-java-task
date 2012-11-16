package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ControlSorter {
    private boolean ignoreCase;
    private boolean unique;
    private ArrayList<String> fileNames;
    private String outputFileName;
    private int numthreads;
    private volatile ArrayList<String> str;
    private volatile ArrayList<List<String>> results = new ArrayList<List<String>>();
    private ArrayList<Sorter> sorters;
    private ArrayList<Merger> mergers;

    public ControlSorter(boolean ignoreCase, boolean unique, int numthreads, String outputFileName, ArrayList<String> fileNames) {

        this.ignoreCase = ignoreCase;
        this.unique = unique;
        this.fileNames = fileNames;
        this.outputFileName = outputFileName;
        this.numthreads = numthreads;
    }

    public void readStrings() {
        InputStream in = null;
        if (outputFileName.isEmpty()) {
            readFromSource(System.in);
        } else {
            for (String fileName : fileNames) {
                try {
                    in = new FileInputStream(fileName);
                    readFromSource(in);
                } catch (FileNotFoundException e) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
    }

    private void readFromSource(InputStream in) {
        InputStreamReader input = null; // new InputStreamReader(fstream);
        BufferedReader br = null;
        try {
            input = new InputStreamReader(in);
            br = new BufferedReader(input);
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (unique) {
                    if (!str.contains(strLine)) {
                        str.add(strLine);
                    }
                } else {
                    str.add(strLine);
                }
            }
        } catch (Exception e) {

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

        }

    }

    public void pSort() throws Exception {
        if (str.size() == 0) {
            throw new Exception("nothing to sort");
        }
        if (numthreads == 0) {
            numthreads = Runtime.getRuntime().availableProcessors() + 1;
        }

        if (numthreads > str.size()) {
            numthreads = str.size();
        }

        int length = str.size() / numthreads;

        for (int i = 0; i <= numthreads; i++) {
            Sorter sort;
            if (i == numthreads) {
                sort = new Sorter(str.subList(i * length, (i + 1) * length), ignoreCase);
            } else {
                sort = new Sorter(str.subList(i * length, str.size()), ignoreCase);
            }
            sorters.add(sort);
            sort.start();
        }

        for (Sorter sorter : sorters) {
            sorter.join();
        }

    }

    public void mergeResults() throws InterruptedException {
        for (Sorter sorter : sorters) {
            results.add(sorter.getResult());
        }

        while (results.size() != 1) {
            mergers.clear();
            for (int i = 0; i < results.size() - 1; i += 2) {
                if (i + 2 < results.size()) {
                    synchronized (results) {
                        Merger merger = new Merger(results.get(i), results.get(i + 1), ignoreCase);
                        merger.start();
                    }
                } else {
                    synchronized (results) {
                        List<String> emptyList = new ArrayList<String>();
                        Merger merger = new Merger(results.get(i), emptyList, ignoreCase);
                        merger.start();
                    }
                }
            }

            for (Merger merger : mergers) {
                merger.join();
            }
            synchronized (results) {
                results.clear();
            }
            for (Merger merger : mergers) {
                results.add(merger.getResult());
            }

        }

        List<String> result = results.get(0);

        for (String strAns : result) {
            System.out.println(strAns);
        }
    }
}
