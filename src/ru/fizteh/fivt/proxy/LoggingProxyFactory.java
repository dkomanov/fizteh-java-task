package ru.fizteh.fivt.proxy;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public interface LoggingProxyFactory {

    /**
     * Создаёт объект-прокси, который логирует в указанный writer вызовы
     * всех методов.
     *
     * @param target     Объект, вызовы методов которого должны логироваться.
     * @param writer     Поток, в который производится запись.
     * @param interfaces Набор интерфейсов, вызовы которых необходимо
     *                   логировать.
     * @return Объект-прокси.
     * @throws IllegalArgumentException Если target, writer или interfaces
     *                                  некорректны: null, target не реализует
     *                                  один из перечисленных интерфейсов,
     *                                  массив интерфейсов пуст, у интерфейсов
     *                                  нет методов.
     */
    Object createProxy(
            Object target,
            Appendable writer,
            Class... interfaces
    );
}
