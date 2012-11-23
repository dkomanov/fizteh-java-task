package ru.fizteh.fivt.students.konstantinPavlov;

import java.io.Closeable;

public class Utils {

    public static void closer(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception e) {
            }
        }
    }

}
