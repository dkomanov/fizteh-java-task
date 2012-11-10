package ru.fizteh.fivt.students.fedyuninV;

import java.io.Closeable;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class IOUtils {

    public static <T extends Closeable> void tryClose(T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
