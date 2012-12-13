package ru.fizteh.fivt.students.frolovNikolay.proxy;

import org.junit.*;

public class UnitTests {
    
    private interface TestClassInterface3 {
        public boolean method5(String firstArg, String secondArg) throws Throwable;
    }
    
    private interface TestClassInterface {
        
        public void method1(Integer firstArg, Integer secondArg);
        
        public String method2(String firstArg, String secondArg);
        
        public boolean method3(String firstArg, int secondArg);
        
        public String[] method4(String firstArg, int secondArg); 
    }
    
    private interface TestClassInterface2 extends TestClassInterface3 {
        
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
        public boolean method5(String firstArg, String secondArg) throws Throwable {
            if (firstArg == null || secondArg == null) {
                throw new Exception("null pointer");
            }
            return firstArg.equals(secondArg);
        }
        
        @Override
        public boolean method6(String[] firstArg, Integer secondArg) {
            return firstArg.length == secondArg;
        }
    }

    @Test
    public void validityTest() throws Throwable {
        StringBuffer stream = new StringBuffer();
        Class<?>[] interfaces = new Class<?>[2];
        interfaces[0] = TestClassInterface3.class;
        interfaces[1] = TestClassInterface2.class;
        TestClassInterface2 myClass2 = (TestClassInterface2) new LoggingProxyFactory().createProxy(new TestClass(), stream, interfaces);
        myClass2.method5("hello" + "\t" + "my friend", "hello" + "\t" + "my friend");
        String[] stringArray = {"hello", "my", "friend"};
        Assert.assertEquals("TestClassInterface3.method5(\"hello\\tmy friend\", \"hello\\tmy friend\") returned true\n", stream.toString());
        stream.setLength(0);
        try {
            myClass2.method5(null, null);
        } catch (Throwable exception) {
            Assert.assertEquals("TestClassInterface3.method5(null, null) threw java.lang.Exception: null pointer"
                    + "\n" + "  ru.fizteh.fivt.students.frolovNikolay.proxy.UnitTests$TestClass.method5(UnitTests.java:56)" + "\n", stream.toString());
        }
        stream.setLength(0);
        myClass2.method6(stringArray, 3);
        Assert.assertEquals("TestClassInterface2.method6(3{\"hello\", \"my\", \"friend\"}, 3) returned true\n", stream.toString());
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
    
    private interface TestInterface {
        public void print(Object array);
    }
    
    private class TestClass2 implements TestInterface {
        
        @Override
        public void print(Object array) {
            Object[] array2 = (Object[]) array;
            for (Object iter : array2) {
                System.out.println(iter.toString());
            }
        }
    }
    
    private interface TestInterface2 {
        public void print(int[] array);
    }
    
    private class TestClass3 implements TestInterface2 {
        
        @Override
        public void print(int[] array) {
            for (int iter : array) {
                System.out.println(iter);
            }
        }
    }
    
    @Test(expected = RuntimeException.class)
    public void cycleReferences() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        Object[] array = new Object[1];
        array[0] = array;
        TestInterface object = (TestInterface) factory.createProxy(new TestClass2(), sf, TestInterface.class);
        object.print(array);
    }
    
    @Test
    public void primitiveArray() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        int[] array = new int[2];
        array[0] = 1;
        array[1] = 0;
        TestInterface2 object = (TestInterface2) factory.createProxy(new TestClass3(), sf, TestInterface2.class);
        object.print(array);
        Assert.assertEquals("TestInterface2.print(2{1, 0})" + "\n", sf.toString());
    }
    
    @Test
    public void copyInArray() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        Long element = 1L;
        Long[] array = new Long[2];
        array[0] = element;
        array[1] = element;
        TestInterface object = (TestInterface) factory.createProxy(new TestClass2(), sf, TestInterface.class);
        object.print(array);
        Assert.assertEquals("TestInterface.print(2{1, 1})" + "\n", sf.toString());
    }
    
    
    private interface Thrower {
        
        public void throwE() throws NullPointerException;
    }
    
    private class ThrowTest implements Thrower {
        
        @Override
        public void throwE() throws NullPointerException {
            throw new NullPointerException("Hello");
        }
    }
    
    @Test
    public void throwTest() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        Thrower object = (Thrower) factory.createProxy(new ThrowTest(), sf, Thrower.class);
        try {
            object.throwE();
        } catch (Throwable ignoringException) {
            Assert.assertEquals("Thrower.throwE() threw java.lang.NullPointerException: Hello" + "\n"
                                 + "  ru.fizteh.fivt.students.frolovNikolay.proxy.UnitTests$ThrowTest.throwE(UnitTests.java:194)" + "\n", sf.toString());
        }
    }
    
    @Test
    public void npeTest() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer sf = new StringBuffer();
        Long element = 1L;
        Long[] array = new Long[2];
        array[0] = element;
        array[1] = null;
        TestInterface object = (TestInterface) factory.createProxy(new TestClass2(), sf, TestInterface.class);
        try {
            object.print(array);
        } catch (Throwable ignoringException) {
            Assert.assertEquals("TestInterface.print(2{1, null}) threw java.lang.NullPointerException: null" + "\n"
                                + "  ru.fizteh.fivt.students.frolovNikolay.proxy.UnitTests$TestClass2.print(UnitTests.java:130)" + "\n", sf.toString());
        }
    }
}