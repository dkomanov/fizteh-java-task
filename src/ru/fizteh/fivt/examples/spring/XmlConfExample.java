package ru.fizteh.fivt.examples.spring;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dmitriy Komanov (spacelord)
 */
public class XmlConfExample {
    public static void main(String[] args) {
        AbstractXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("/ru/fizteh/fivt/examples/spring/sample.xml");
        applicationContext.refresh();
        Algorithm algorithm = (Algorithm) applicationContext.getBean("algorithm");
        algorithm.performAction(args.length == 0 ? "abc" : args[0]);
    }
}
