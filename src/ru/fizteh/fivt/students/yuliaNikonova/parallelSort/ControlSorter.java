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
import java.util.Collections;
import java.util.Comparator;
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
    private volatile ArrayList<String> inputStrings;
    private volatile LinkedBlockingQueue<LinkedBlockingQueue<String>> results;
    private ArrayList<Sorter> sorters;
    private ArrayList<Merger> mergers;
    private ArrayList<String> result;
    private Comparator<String> stringComp;
    private ArrayList<String> res1;
    private int size;

    public ControlSorter(boolean ignoreCase, boolean unique, int numthreads, String outputFileName, ArrayList<String> fileNames) {

        this.ignoreCase = ignoreCase;
        this.unique = unique;
        this.fileNames = fileNames;
        this.outputFileName = outputFileName;
        this.numthreads = numthreads;
        this.inputStrings = new ArrayList<String>();
        this.results = new LinkedBlockingQueue<LinkedBlockingQueue<String>>();
        this.sorters = new ArrayList<Sorter>();
        this.mergers = new ArrayList<Merger>();
        this.result = new ArrayList<String>();
        if (ignoreCase) {
        this.stringComp = String.CASE_INSENSITIVE_ORDER;
        } else {
            this.stringComp=new StringComparator();
        }
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
                    System.err.println(fileName+": "+e.getMessage());
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
                inputStrings.add(strLine);

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
        if (numthreads == 0) {
            numthreads = Runtime.getRuntime().availableProcessors() + 1;
        }

        if (numthreads > size) {
            numthreads = size;
        }
        int length = size / numthreads;

        for (int i = 0; i < numthreads; i++) {
            Sorter sort;
            if (i != numthreads - 1) {
                sort = new Sorter(inputStrings.subList(i * length, (i + 1) * length), stringComp);
            } else {
                sort = new Sorter(inputStrings.subList(i * length, size), stringComp);
            }
            sorters.add(sort);
            sort.start();
        }

        for (Sorter sorter : sorters) {
            sorter.join();
        }
        for (Sorter sorter : sorters) {
            results.put(new LinkedBlockingQueue<String>(sorter.getResult()));
        }

    }

    private void mergeResults() throws InterruptedException {
        while (results.size() != 1) {
            mergers.clear();
            Merger mMerger;
            for (int i = 0; i < results.size(); i += 2) {
                if (i + 1 == results.size()) {
                    results.put(new LinkedBlockingQueue<String>());
                }

                mMerger = new Merger(results.take(), results.take(), stringComp);

                mergers.add(mMerger);
                mMerger.start();
            }
            for (Merger merger : mergers) {
                merger.join();
            }
            for (Merger merger : mergers) {
                results.put(merger.getResult());
            }
        }
    }

    /* private void mergeRes() {
     * 
     * int length = (int) Math.ceil((double) size / (double) numthreads);
     * 
     * for (int i = 0; i < numthreads; i++) {
     * if (i != numthreads - 1) {
     * merge(0, i * length, (i + 1) * length);
     * } else {
     * merge(0, i * length, size);
     * }
     * }
     * } */

    private void printResults() throws FileNotFoundException, InterruptedException {
        FileOutputStream fileout = null;
        this.result.addAll(results.take());
        if (outputFileName.isEmpty()) {
            printToDestination(System.out);
        } else {
            try {
                fileout = new FileOutputStream(new File(outputFileName));
                printToDestination(fileout);
            } catch (FileNotFoundException e) {
                throw e;
            } finally {
                Utils.close(fileout);
            }

        }

    }

    private void printToDestination(OutputStream out) {
        String prevValue="";
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(out);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        int size = res1.size();

        for (int i = 0; i < size; i++) {
            if (unique) {
                String curValue=result.get(i);
                if (ignoreCase) {
                    
                    if ((i == 0) || (i > 0 && String.CASE_INSENSITIVE_ORDER.compare(curValue, prevValue) != 0)) {
                        pw.println(curValue);
                    }

                } else {
                    if ((i == 0) || (i > 0 && !curValue.equals(prevValue))) {
                        pw.println(curValue);
                    }
                }
                
                prevValue=result.get(i);
            } else {
                pw.println(result.get(i));
            }
        }
        pw.flush();
        if (!out.equals(System.out)) {
            pw.close();
        }

    }
    
    public void mergeRes() {
        res1=new ArrayList<String>();
        System.out.println(size);
            while (res1.size()!=size) {
                String min=null;
                int minIndex=-1;
                System.out.println("Size is "+res1.size());
                for (int i=0; i<sorters.size(); i++) {
                    System.out.println("Sorter: "+i);
                    if (sorters.get(i).hasNext()) {
                        System.out.println(i+" has next value");
                    String val=sorters.get(i).getNext();
                    System.out.println(val);
                    if ((min==null) || (min!=null && stringComp.compare(val, min)<0)) {
                        min=val;
                        minIndex=i;
                    }
                    }
                }
                System.out.println("Min string is "+min);
                res1.add(min);
                if (minIndex>-1) {
                sorters.get(minIndex).pop();
                }
                
            }
        }
    

    public void sort() throws Exception {
        this.readStrings();
        size = inputStrings.size();
        // long time = 0;
        // ArrayList<String> testArray = new ArrayList<String>();
        // testArray.addAll(inputStrings);
        // long start = System.nanoTime();

        // Collections.sort(testArray, stringComp);
        // long finish = System.nanoTime();
        // System.out.println("Simple sort: " + (finish - start));
        // start = System.nanoTime();
        this.pSort();
        // finish = System.nanoTime();
        // System.out.println("Psort: " + (finish - start));
        // time += finish - start;
        /* for (String strLine : inputStrings) {
         * System.out.println(strLine);
         * } */
        // start = System.nanoTime();
        //this.mergeResults();
        // finish = System.nanoTime();
        // System.out.println("Old merge: " + (finish - start));
        // System.out.println(time + finish - start);
     long start = System.nanoTime();
        this.mergeRes();
        long finish = System.nanoTime();
        System.out.println("New merge: " + (finish - start));
        // System.out.println(time + finish - start);
        this.printResults();
        /* (String strLine : inputStrings) {
         * System.out.println(strLine);
         * } */
    }

}
