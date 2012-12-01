package ru.fizteh.fivt.students.mysinYurii.stringFormatter;

import java.util.Date;
import ru.fizteh.fivt.format.*;

public class UnitTest {
    static StringFormatter formatter;
    
    UnitTest() {
        StringFormatterFactory factory = new StringFormatterFactory();
        formatter = factory.create(IntegerExtention.class.getName(), DateExtention.class.getName());
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
            String str = formatter.format("a{0:d}a", null);
            System.out.println(str.equals("aa"));
        } catch (FormatterException e) {
            System.out.println("fail");
        }
        
    }
    
    public void exceptionTest2() {
        try {
            String str = formatter.format("{0:strangepattern}", new Date());
            System.out.println("fail");
        } catch (FormatterException e) {
            System.out.println("success");
        }
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
    
    public void test4() {
        try {
            String str = formatter.format("{0}", new Integer(10));
            System.out.println(str.equals("10"));
        } catch (FormatterException e) {
            System.out.println("fail");
        }
    }
    
    public void test5() {
        try {
            String str = formatter.format("{0:05d}", new Integer(10));
            System.out.println(str.equals("00010"));
        } catch (FormatterException e) {
            System.out.println(e.getMessage());
        }
    }
}
