package ru.fizteh.fivt.students.levshinNikolay;

import java.io.Closeable;

/**
 * Levshin Nikolay
 * FIVT 196
 */
public class Utils {
    public static <T extends Closeable> void tryClose(T object){
        if(object!=null){
            try{
                object.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
