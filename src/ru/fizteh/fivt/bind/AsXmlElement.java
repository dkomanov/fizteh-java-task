package ru.fizteh.fivt.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Показывает, что поле/метод должно сериализоваться как элемент.
 *
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsXmlElement {

    /**
     * Если задано, используется в качестве имени элемента вместо имени поля.
     */
    String name() default "";
}
