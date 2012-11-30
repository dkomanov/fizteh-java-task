package ru.fizteh.fivt.students.mysinYurii;

import java.io.Closeable;

public class ObjectCloser {
    public static void close(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Throwable e) {
                return;
            }
        }
    }
}
