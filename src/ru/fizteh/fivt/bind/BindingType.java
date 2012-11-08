package ru.fizteh.fivt.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, какие члены класса необходимо использовать
 * при сериализации-десериализации.
 *
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BindingType {

    MembersToBind value();
}
