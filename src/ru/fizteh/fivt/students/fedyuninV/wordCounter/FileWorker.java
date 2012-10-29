package ru.fizteh.fivt.students.fedyuninV.wordCounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Scanner;


/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class FileWorker {

        public ResultContainer run(String fileName, boolean ignoreCase,
                                   boolean readLines, boolean agregate, boolean unique) {
        ResultContainer result = new ResultContainer(ignoreCase);
        String incomingData;
        FileReader fReader = null;
        BufferedReader reader = null;
        try {
            fReader = new FileReader(fileName);
            reader = new BufferedReader(fReader);
            while ((incomingData = reader.readLine()) != null) {
                if (!readLines) {
                    String[] tokens = incomingData.split("[ \t\n.!?,:;]+");
                    for (int i = 0; i < tokens.length; i++) {
                        result.add(tokens[i]);
                    }
                } else {
                    result.add(incomingData);
                }
            }
            if (!agregate) {
                System.out.println(fileName);
                result.print(unique);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        } finally {
            if (fReader != null) {
                try {
                    fReader.close();
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return result;
    }
}
