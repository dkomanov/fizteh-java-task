package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import ru.fizteh.fivt.students.yuliaNikonova.common.Utils;

public class ControlSorter {
    private boolean ignoreCase;
    private boolean unique;
    private ArrayList<String> fileNames;
    private String outputFileName;
    private int numthreads;
    private volatile ArrayList<String> str;
    private volatile ArrayList<LinkedBlockingQueue<String>> results;
    private ArrayList<Sorter> sorters;
    private ArrayList<Merger> mergers;
    private LinkedBlockingQueue<String> result;

    public ControlSorter(boolean ignoreCase, boolean unique, int numthreads, String outputFileName, ArrayList<String> fileNames) {

        this.ignoreCase = ignoreCase;
        this.unique = unique;
        this.fileNames = fileNames;
        this.outputFileName = outputFileName;
        this.numthreads = numthreads;
        this.str = new ArrayList<String>();
        this.results = new ArrayList<LinkedBlockingQueue<String>>();
        this.sorters = new ArrayList<Sorter>();
        this.mergers = new ArrayList<Merger>();
    }

    public void readStrings() {

        DataInputStream in = null;
        if (fileNames.isEmpty()) {
            readFromSource(System.in);
        } else {
            for (String fileName : fileNames) {
                FileInputStream fstream = null;
                try {
                    fstream = new FileInputStream(fileName);
                    in = new DataInputStream(fstream);
                    readFromSource(in);
                } catch (Exception e) {

                } finally {
                    Utils.close(fstream);
                    Utils.close(in);
                }
            }
        }

        Utils.close(in);
    }

    private void readFromSource(InputStream in) {
        BufferedReader br = null;
        InputStreamReader inReader = null;
        try {
            inReader = new InputStreamReader(in);
            br = new BufferedReader(inReader);
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
            if (!in.equals(System.in)) {
                Utils.close(br);
                Utils.close(inReader);
            }

        }
    }

    private void pSort() throws Exception {
        if (str.size() == 0) {
            throw new Exception("nothing to sort");
        }
        if (numthreads == 0) {
            numthreads = Runtime.getRuntime().availableProcessors() + 1;
        }

        if (numthreads > str.size()) {
            numthreads = str.size();
        }
        int length = (int) Math.ceil((double) str.size() / (double) numthreads);

        for (int i = 0; i < numthreads; i++) {
            Sorter sort;
            if (i != numthreads - 1) {
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

    private void mergeResults() throws InterruptedException {
        for (Sorter sorter : sorters) {
            LinkedBlockingQueue<String> mList = new LinkedBlockingQueue<String>();
            mList.addAll(sorter.getResult());
            results.add(mList);
        }

        while (results.size() != 1) {
            // System.out.println("Size is " + results.size());
            mergers.clear();
            Merger mMerger;
            for (int i = 0; i < results.size(); i += 2) {
                // System.out.println("Thread: " + i / 2);
                if (i + 1 == results.size()) {
                    synchronized (results) {
                        results.add(new LinkedBlockingQueue<String>());
                    }
                }
                synchronized (results) {
                    mMerger = new Merger(results.get(i), results.get(i + 1), ignoreCase, i / 2);
                }

                mergers.add(mMerger);
                mMerger.start();
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
        this.result = results.get(0);
    }

    private void printResults() throws FileNotFoundException {
        FileOutputStream fileout = null;
        if (outputFileName.isEmpty()) {
            printToDestination(System.out);
        } else {
            try {
                fileout = new FileOutputStream(new File(outputFileName));
            } catch (FileNotFoundException e) {
                throw e;
            }
            printToDestination(fileout);
        }
        Utils.close(fileout);
    }

    private void printToDestination(OutputStream out) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(out);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        for (String strLine : result) {
            pw.println(strLine);
        }
        if (!out.equals(System.out)) {
            pw.flush();
            pw.close();
        }

    }

    public void sort() throws Exception {
        this.readStrings();
        // System.out.println("I read");
        this.pSort();
        // System.out.println("I sort");
        this.mergeResults();
        // System.out.println("I merge");
        this.printResults();
    }
}
