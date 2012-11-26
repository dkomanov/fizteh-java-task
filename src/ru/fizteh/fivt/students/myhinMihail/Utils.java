package ru.fizteh.fivt.students.myhinMihail;

import java.io.Closeable;

public class Utils {
    
    public static class Pair<T, U> {
        public T first;
        public U second;
        
        public Pair(T f, U s) {
            first = f;
            second = s;
        }
        
    }
    
    public static <T extends Closeable> void tryClose(T object) {
        if (object != null) {
            try {
                  object.close();
            } catch (Exception ex) {
            }
        }
    }

    public static void printErrorAndExit(String error) {
        System.err.println(error);
        System.exit(1);
    }
    
}
