package ru.fizteh.fivt.proxy;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public interface ShardingProxyFactory {

    /**
     * Создаёт объект-прокси, который выполняет диспетчеризацию между
     * целями прокси.
     *
     * @param targets    Объекты, вызовы методов которых должны
     *                   диспетчеризоваться.
     * @param interfaces Набор интерфейсов, вызовы которых необходимо
     *                   логировать.
     * @return Объект-прокси.
     * @throws IllegalArgumentException Если targets или interfaces
     *                                  некорректны: null, targets пустой,
     *                                  какой-либо из targets не реализует
     *                                  один из перечисленных интерфейсов,
     *                                  массив интерфейсов пуст, у интерфейсов
     *                                  нет методов.
     * @throws IllegalStateException    Интерфейс размечены аннотациями
     *                                  некорректно.
     */
    Object createProxy(
            Object[] targets,
            Class[] interfaces
    );
}
