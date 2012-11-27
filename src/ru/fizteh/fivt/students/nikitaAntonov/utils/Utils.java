package ru.fizteh.fivt.students.nikitaAntonov.utils;

import java.io.Closeable;

public class Utils {

    public static String concat(String list[]) {
        StringBuilder result = new StringBuilder();

        for (String s : list) {
            result.append(s);
            result.append(" ");
        }

        return result.toString();
    }

    public static void closeResource(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception expt) {
            }
        }
    }
}