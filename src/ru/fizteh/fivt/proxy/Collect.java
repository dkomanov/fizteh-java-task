package ru.fizteh.fivt.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Показывает, что метод должен быть вызван для всех объектов,
 * а результат выполнения должен быть общим. Поддереживаются возвращаемые
 * типы: void, int, long и List.
 *
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Collect {
}
