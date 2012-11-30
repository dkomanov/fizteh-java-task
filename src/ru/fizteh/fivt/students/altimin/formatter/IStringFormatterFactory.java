package ru.fizteh.fivt.students.altimin.formatter;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */

public interface IStringFormatterFactory {

    /**
     * Экземпляр форматтера, поддерживающий указанные расширения.
     *
     * @param extensionClassNames Полные имена классов-наследников
     *                            {@link StringFormatterExtension}.
     * @return Форматтер.
     * @throws FormatterException Если указаны классы-дубликаты, либо
     *                            указанный класс не может быть создан, либо
     *                            класс не найден, либо класс не является
     *                            наследником {@link StringFormatterExtension}.
     */
    IStringFormatter create(String... extensionClassNames)
            throws FormatterException;
}