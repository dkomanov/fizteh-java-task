package ru.fizteh.fivt.examples.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dmitriy Komanov (spacelord)
 */
public class JavaConfExample {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(Config.class);
        Algorithm algorithm = (Algorithm) applicationContext.getBean("algorithm");
        algorithm.performAction(args.length == 0 ? "abc" : args[0]);
    }

    @Configuration
    public static class Config {
        @Bean
        public Algorithm algorithm() {
            return new SimpleAlgorithm();
        }

        @Bean
        public Printer printer() {
            return new ConsolePrinter();
        }
    }
}
