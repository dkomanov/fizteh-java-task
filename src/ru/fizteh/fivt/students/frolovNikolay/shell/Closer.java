package ru.fizteh.fivt.students.frolovNikolay.shell;

import java.io.Closeable;

/*
 *  Класс отвечающий за
 *  освобождение ресурсов.
 */
public class Closer {
    public static void close(Closeable resourse) {
        if (resourse != null) {
            try {
                resourse.close();
            } catch (Exception crush) {
                System.err.println(crush.getMessage());
            }
        }
    }
}
