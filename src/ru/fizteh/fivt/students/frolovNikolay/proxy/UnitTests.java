package ru.fizteh.fivt.students.frolovNikolay.proxy;

import org.junit.*;

public class UnitTests {
    
    private interface TestClassInterface {
        
        public void method1(Integer firstArg, Integer secondArg);
        
        public String method2(String firstArg, String secondArg);
        
        public boolean method3(String firstArg, int secondArg);
        
        public String[] method4(String firstArg, int secondArg); 
    }
    
    private interface TestClassInterface2 {
        
        public boolean method5(String firstArg, String secondArg);
        
        public boolean method6(String[] firstArg, Integer secondArg);
    }
    
    private class TestClass implements TestClassInterface, TestClassInterface2 {
        
        @Override
        public void method1(Integer firstArg, Integer secondArg) {
            firstArg = firstArg.intValue() + secondArg.intValue();
        }
        
        @Override
        public String method2(String firstArg, String secondArg) {
            return firstArg + secondArg;
        }
        
        @Override
        public boolean method3(String firstArg, int secondArg) {
            return firstArg.length() == secondArg;
        }
        
        @Override
        public String[] method4(String firstArg, int secondArg) {
            String[] result = new String[secondArg];
            for (int i = 0; i < secondArg; ++i) {
                result[i] = new String(firstArg);
            }
            return result;
        }
        
        @Override
        public boolean method5(String firstArg, String secondArg) {
            return firstArg.equals(secondArg);
        }
        
        @Override
        public boolean method6(String[] firstArg, Integer secondArg) {
            return firstArg.length == secondArg;
        }
    }

    @Test
    public void validityTest() {
        StringBuffer stream = new StringBuffer();
        TestClass myClass = new TestClass();
        Class<?>[] interfaces = new Class<?>[2];
        interfaces[0] = TestClassInterface.class;
        interfaces[1] = TestClassInterface2.class;
        TestClassInterface2 myClass2 = (TestClassInterface2) new LoggingProxyFactory().createProxy(myClass, stream, interfaces);
        myClass2.method5("hello", "hello");
        String[] stringArray = {"hello", "my", "friend"};
        Assert.assertEquals("TestClassInterface2.method5(\\\"hello\\\", \\\"hello\\\") returned true\n", stream.toString());
        stream.setLength(0);
        myClass2.method6(stringArray, 3);
        Assert.assertEquals("TestClassInterface2.method6(3{\\\"hello\\\", \\\"my\\\", \\\"friend\\\"}, 3) returned true\n", stream.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullTarget() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        factory.createProxy(null, null, null);
    }
    
    private class WithoutInterfaces {
        static public final String msg = "Forever alone"; 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void withoutInterfaces() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        TestClassInterface proxy = (TestClassInterface) factory.createProxy(new WithoutInterfaces(), sf, TestClassInterface.class);
    }
    
    private interface EmptyInterface {
    }
    
    private class WithEmptyInterface implements EmptyInterface {
        static public final String msg = "Forever not alone"; 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void withEmptyInterface() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        EmptyInterface proxy = (EmptyInterface) factory.createProxy(new WithEmptyInterface(), sf, EmptyInterface.class);
    }
}