package ru.fizteh.fivt.students.mysinYurii.stringFormatter;

import java.util.Date;

import ru.fizteh.fivt.format.FormatterException;

public class UnitTest {
    static StringFormatter formatter;
    
    UnitTest() {
        formatter = new StringFormatter();
    }
    
    class Tester {
        public Integer integerValue = new Integer(10);
        public Integer nullInteger = null;
        public Date nullDate = null;
        public Date dateValue = new Date();
    }
    
    public void test2() {
        try {
            String str = formatter.format("{0.something}", new Tester());
            System.out.println(str.equals(""));
        } catch (FormatterException e) {
            System.out.println("test fail");
        }
    }
    
    public void exceptionTest1() {
        try {
            formatter.format("{+0}", new Integer(10));
        } catch (FormatterException e) {
            System.out.println(e.getMessage().equals("Index of argument is invalid: +0"));
        }
    }
    
    public void test1() {
        try {
            String str = formatter.format("a{1}a", new Integer(10));
            System.out.println(str.equals("aa"));
        } catch (FormatterException e) {
            System.out.println(e.getMessage() + " is fail");
        }
    }
    
    public void exceptionTest2() {
        String str = formatter.format("{0:strangepattern}", new Date());
        System.out.println(str.equals(""));
    }
    
    public void test3() {
        String str = formatter.format("empty{0.nullInteger}", new Tester());
        System.out.println(str.equals("empty"));
    }
    
    public void exceptionTest3() {
        try {
            String str = formatter.format("{{{{0}", new Tester());
            System.out.println("fail");
        } catch (FormatterException e) {
            System.out.println("success");
        }
        
    }
}
