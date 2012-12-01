package ru.fizteh.fivt.students.yuliaNikonova.common;

import java.io.Closeable;

public class Utils {
    public static <T extends Closeable> void close(T object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception ex) {
            }
        }
    }
}
