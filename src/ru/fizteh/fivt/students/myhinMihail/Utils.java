package ru.fizteh.fivt.students.myhinMihail;

import java.io.Closeable;

public class Utils {
	
    public static <T extends Closeable> void tryClose(T object) {
        if (object != null) {
            try {
                  object.close();
            } catch (Exception ex) {
            }
        }
    }
    
}
