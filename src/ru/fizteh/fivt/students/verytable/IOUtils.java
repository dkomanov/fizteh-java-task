package ru.fizteh.fivt.students.verytable;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    
    
    public static boolean closeFile(String fileName,
                             Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
                return true;
            }
        } catch (IOException ex) {
            System.err.println(fileName + " failed to close.");
            return false;
        }
        return false;
    }
}
