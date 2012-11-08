package ru.fizteh.fivt.format;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public interface StringFormatter {

    /**
     * Выполняет форматирование строки в соответствии с форматом.
     *
     * @param format Строка форматирования. Формат: {0.field:pattern},
     *               {1.field.field}
     * @param args   Параметры форматирования.
     * @return Форматированная строка.
     */
    String format(String format, Object... args)
            throws FormatterException;

    /**
     * Выполняет форматирование строки в соответствии с форматом, записывая
     * результат в буфер.
     *
     * @param buffer Буфер для результата.
     * @param format Строка форматирования. Формат: {0.field:pattern},
     *               {1.field.field}
     * @param args   Параметры форматирования.
     */
    void format(StringBuilder buffer, String format, Object... args)
            throws FormatterException;
}
