package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.FormatterException;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterTester {

    public static void main(String[] args) {
        StringFormatter formatter = new StringFormatterFactory().create(StringFormatterFloatExtension.class.getName(),
                                                                        StringFormatterByteArrayExtension.class.getName());
        //try {
            System.out.println(formatter.format("Hello {{1}} {0:.3} world!", (float) 3.1415926));
        //} catch (FormatterException ex) {
        //    System.err.println(ex.getMessage());
        //}
    }

}
