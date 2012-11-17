package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControlSorter {
    private boolean ignoreCase;
    private boolean unique;
    private ArrayList<String> fileNames;
    private String outputFileName;
    private int numthreads;
    private volatile ArrayList<String> str;
    private volatile ArrayList<List<String>> results;
    private ArrayList<Sorter> sorters;
    private ArrayList<Merger> mergers;

    public ControlSorter(boolean ignoreCase, boolean unique, int numthreads, String outputFileName, ArrayList<String> fileNames) {

        this.ignoreCase = ignoreCase;
        this.unique = unique;
        this.fileNames = fileNames;
        this.outputFileName = outputFileName;
        this.numthreads = numthreads;
        this.str=new ArrayList<String>();
        this.results = new ArrayList<List<String>>();
        this.sorters=new ArrayList<Sorter>();
        this.mergers=new ArrayList<Merger>();
    }

    public void readStrings() {
        InputStream in = null;
        if (fileNames.isEmpty()) {
            readFromSource(System.in);
        } else {
            for (String fileName : fileNames) {
                try {
                    //System.out.println("Filename: "+fileName);
                    readFromFile(fileName);
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
    
    private void readFromFile(String fileName) {
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        try {
            fstream = new FileInputStream(fileName);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
            while ((strLine = br.readLine()) != null) {
               //System.out.println(strLine);
                if (unique) {
                    if (!str.contains(strLine)) {
                        str.add(strLine);
                        
                    } 
                } else {
                    str.add(strLine);
                    
                }
                
                }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
            }
            if (fstream != null) {
                try {
                    fstream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                   // e.printStackTrace();
                }
            }
        }
        
    }

    private void readFromSource(InputStream in) {
        DataInputStream input = null; // new InputStreamReader(fstream);
        BufferedReader br = null;
        try {
            input = new DataInputStream(in);
            br = new BufferedReader(new InputStreamReader(input));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                System.out.println(strLine);
                if (unique) {
                    if (!str.contains(strLine)) {
                        str.add(strLine);
                    }
                } else {
                    str.add(strLine);
                }
            }
            
            
            for (String strLi:str) {
                System.out.println(strLi);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        /*for (String strLine:str) {
            System.out.println(strLine);
        }
        System.out.println("PSort");
        System.out.println("Size: "+str.size());*/
        if (str.size() == 0) {
            throw new Exception("nothing to sort");
        }
        if (numthreads == 0) {
            numthreads = Runtime.getRuntime().availableProcessors() + 1;
        }

        if (numthreads > str.size()) {
            numthreads = str.size();
        }

        int length = (int)Math.ceil(str.size() / numthreads);
        System.out.println("Length: "+length);

        for (int i = 0; i < numthreads; i++) {
            Sorter sort;
            System.out.println("Count of threads: "+numthreads);
            if (i != numthreads-1) {
                System.out.println(i * length+" "+(i + 1) * length);
                sort = new Sorter(str.subList(i * length, (i + 1) * length), ignoreCase);
            } else {
                sort = new Sorter(str.subList(i * length, str.size()), ignoreCase);
                System.out.println(i * length+" "+str.size());
            }
            sorters.add(sort);
            sort.start();
        }

        for (Sorter sorter : sorters) {
            sorter.join();
        }
        /*for (Sorter sorter: sorters) {
            sorter.showResults();
        }*/

    }

    public void mergeResults() throws InterruptedException {
        System.out.println("Merge");

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

    }
    
    public void sort() throws Exception {
        this.readStrings();
        System.out.println("read");
        try {
        this.pSort();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //this.mergeResults();
    }
}
