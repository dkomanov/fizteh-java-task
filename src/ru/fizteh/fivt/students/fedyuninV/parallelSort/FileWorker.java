package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.io.*;
import java.util.ArrayList;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class FileWorker implements Runnable{

    private BufferedReader reader = null;
    private String fileName;
    private ArrayList<ArrayList<String>> container;
    private int maxSorters;
    private ArrayList<Object> sortersFinishSem;
    private ArrayList<Object> sortersStartSem;
    private Object startSem;
    private Object finishSem;
    private int blockSize = 30;

    public FileWorker(String fileName, ArrayList<ArrayList<String>> container, Object startSem,
                      Object finishSem, ArrayList<Object> sortersStartSem,
                      ArrayList<Object> sortersFinishSem) {

        this.fileName = fileName;
        this.container = container;
        this.startSem = startSem;
        this.finishSem = finishSem;
        this.sortersStartSem = sortersStartSem;
        this.sortersFinishSem = sortersFinishSem;
        maxSorters = sortersFinishSem.size();
    }


    public void run() {
        FileReader fReader = null;
        InputStreamReader iStreamReader = null;
        boolean fileUsed = false;
        while (!Thread.currentThread().isInterrupted()) {
            try {

                finishSem.notify();
                startSem.wait();
                synchronized (fileName) {
                    if (fileName != null) {
                        fileUsed = true;
                        fReader = new FileReader(fileName);
                        reader = new BufferedReader(fReader);
                    } else {
                        fileUsed = false;
                        iStreamReader = new InputStreamReader(System.in);
                        reader = new BufferedReader(iStreamReader);
                    }
                }
                String incomingData;
                ArrayList<String> currContainer = container.get(0);
                int containerIndex = 0;
                int lineNum = 0;
                while((incomingData = reader.readLine()) != null) {
                    System.out.println(incomingData);
                    synchronized (currContainer) {
                        currContainer.add(incomingData);
                    }
                    lineNum++;
                    if (lineNum == blockSize) {
                        sortersStartSem.get(containerIndex).notify();
                        lineNum = 0;
                        containerIndex++;
                        if (containerIndex == maxSorters) {
                            containerIndex = 0;
                        }
                        sortersFinishSem.get(containerIndex).wait();
                    }
                }
                reader.close();
                if (fileUsed) {
                    fReader.close();
                } else {
                    iStreamReader.close();
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                try {
                    if (fReader != null) {
                        fReader.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (iStreamReader != null) {
                        iStreamReader.close();
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.exit(1);
            }
        }
    }
}
