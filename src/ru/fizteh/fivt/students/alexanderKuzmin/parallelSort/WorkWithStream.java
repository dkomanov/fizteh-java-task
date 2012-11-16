package ru.fizteh.fivt.students.alexanderKuzmin.parallelSort;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

/**
 * @author Alexander Kuzmin group 196 Class WorkWithStream
 * 
 */

public class WorkWithStream {

    public static void readerFromStream(ArrayList<String> answer,
            InputStream stream) throws IOException {
        BufferedReader bufReader = null;
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(stream);
            bufReader = new BufferedReader(inputReader);
            String str;
            while ((str = bufReader.readLine()) != null) {
                answer.add(str);
            }
        } catch (Exception e) {
            Closers.printErrAndExit(e.getMessage());
        } finally {
            Closers.closeStream(bufReader);
        }
    }

    public static void printToStream(String str, PrintStream stream) {
        stream.println(str);
    }
    
    public static <T extends Closeable> void closeStream(T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Closers.printErrAndExit(e.getMessage());
            }
        }
    }

}
