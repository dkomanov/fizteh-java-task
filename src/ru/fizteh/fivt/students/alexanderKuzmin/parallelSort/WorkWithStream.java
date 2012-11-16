package ru.fizteh.fivt.students.alexanderKuzmin.parallelSort;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

/**
 * @author Alexander Kuzmin group 196 Class WorkWithStream
 * 
 */
 
public class WorkWithStream {

    public static <T extends Closeable> void closeStream(T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Closers.printErrAndExit(e.getMessage());
            }
        }
    }

    public static void printToStream(String str, PrintStream stream) {
        stream.println(str);
    }

}
