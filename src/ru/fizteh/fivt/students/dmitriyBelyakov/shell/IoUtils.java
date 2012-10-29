package ru.fizteh.fivt.students.dmitriyBelyakov.shell;

import java.io.Closeable;

public class IoUtils {
    static public void close(Closeable object) {
        try {
            if (object != null) {
                object.close();
            }
        } catch (Exception e) {
        }
    }
}
