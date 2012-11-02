package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Reader implements Runnable{

    BufferedReader reader = null;
    ThreadPool sorters = null;
    boolean ignoreCase;
    ResultContainer finish;
    int blockSize = 30;

    public Reader(String fileName, ThreadPool sorters,
                    boolean ignoreCase, ResultContainer finish) {
        this.finish = finish;
        this.ignoreCase = ignoreCase;
        this.sorters = sorters;
        InputStreamReader iStreamReader = null;
        FileReader fReader = null;
        try {
            if (fileName == null) {
                iStreamReader = new InputStreamReader(System.in);
                reader = new BufferedReader(iStreamReader);
            } else {
                fReader = new FileReader(fileName);
                reader = new BufferedReader(fReader);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            try {
                if (iStreamReader != null) {
                    iStreamReader.close();
                }
                if (fReader != null) {
                    fReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
            System.exit(1);
        }
    }

    public void run() {
        String incomingData;
        //System.out.println("her");
        int currNum = 0;
        try {
            ArrayList <String> container = new ArrayList<String>();
            while ((incomingData = reader.readLine()) != null) {
                //System.out.println(incomingData);
                container.add(incomingData);
                currNum++;
                if (currNum == blockSize) {
                    currNum = 0;
                    sorters.add(new Sorter(finish, (ArrayList<String>) container.clone(), ignoreCase));
                    container.clear();
                }
            }
            if (container.size() != 0) {
                sorters.add(new Sorter(finish, (ArrayList<String>) container.clone(), ignoreCase));
            }
            //System.out.println("OKay");
            reader.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
