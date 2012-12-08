package ru.fizteh.fivt.students.mysinYurii.testClass;

public class TestClass {
    private interface Inter {
        void hello(int i);
    }
    
    public static Class<Inter> getInter() {
        return Inter.class;
    }
    
    public static NestedClass getNested() {
        return new NestedClass();
    }
    
    private static class NestedClass implements Inter {
        public void hello(int i) {
            System.out.println("Hello");
        }
    }
}
