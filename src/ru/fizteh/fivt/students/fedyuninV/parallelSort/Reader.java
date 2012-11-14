package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import ru.fizteh.fivt.students.fedyuninV.IOUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Reader implements Runnable{

    BufferedReader reader = null;
    ExecutorService sorters = null;
    boolean ignoreCase;
    ResultContainer finish[];
    int fileNum;
    int blockSize = 1024 * 128;


    public Reader(String fileName, int fileNum, ExecutorService sorters,
                    boolean ignoreCase, ResultContainer[] finish) {
        this.finish = finish;
        this.ignoreCase = ignoreCase;
        this.sorters = sorters;
        this.fileNum = fileNum;
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
                IOUtils.tryClose(iStreamReader);
                IOUtils.tryClose(fReader);
                IOUtils.tryClose(reader);
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
            System.exit(1);
        }
    }

    public void run() {
        Random random = new Random();
        String incomingData;
        int currNum = 0;
        try {
            List <StringContainer> container = new ArrayList<StringContainer>();
            while ((incomingData = reader.readLine()) != null) {
                container.add(new StringContainer(incomingData, currNum, fileNum));
                currNum++;
                if (currNum % blockSize == 0) {
                    sorters.execute(new Sorter(finish[random.nextInt(finish.length)], container, ignoreCase));
                    container = new ArrayList<StringContainer>();
                }
            }
            if (container.size() != 0) {
                sorters.execute(new Sorter(finish[random.nextInt(finish.length)], container, ignoreCase));
            }
            reader.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
