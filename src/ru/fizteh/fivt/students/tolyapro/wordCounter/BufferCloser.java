package ru.fizteh.fivt.students.tolyapro.wordCounter;

import java.io.Closeable;

public class BufferCloser {
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
