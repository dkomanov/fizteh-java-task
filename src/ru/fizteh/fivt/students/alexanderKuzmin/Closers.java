package ru.fizteh.fivt.students.alexanderKuzmin;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Alexander Kuzmin group 196 Class Closers
 * 
 */

public class Closers {

    public static <T extends Closeable> void closeStream(T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                printErrAndExit(e.getMessage());
            }
        }
    }

    public static void printErrAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void printErrAndNoExit(String message) {
        System.err.println(message);
    }

}
