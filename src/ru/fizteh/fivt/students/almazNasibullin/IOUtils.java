package ru.fizteh.fivt.students.almazNasibullin;

import java.io.Closeable;
import java.io.IOException;

/**
 * 29.10.12
 * @author almaz
 */

public class IOUtils {

    public static void printErrorAndExit(String error) {
        System.err.println(error);
        System.exit(1);
    }

    public static void closeOrExit(Closeable cl) {
        try {
            if (cl != null) {
                cl.close();
            }
        } catch (IOException e) {
            printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }
}
