package ru.fizteh.fivt.students.mesherinIlya.asmproxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;


public interface MyTestInterface {
    @DoNotProxy
    public int getI(int a);

//    @Collect
    public long getL(long a);

    int smth(int a, int b);

    public long getA(Long a);

//    @Collect
///    public int getLength(String s);

//    @Collect
//    public int getSomething();

//    @Collect
  //  public void throwException(String s);
}

class MyTestClass implements MyTestInterface {
    @Override
    @DoNotProxy
    public int getI(int a) {
        return 124;
    }

    @Override
    public long getL(long a) {
        return  2*a+2;
    }
   
    @Override
    public int smth(int a, int b) {
        return a + a * b;
    }

    @Override
    public long getA(Long a) {
        return -a;
    }

  //  @Override
   // @Collect
    public int getLength(String s) {
        return s.length();
    }

    //@Override
   // @Collect
    public int getSomething() {
        return 15;
    }

   // @Override
  //  @Collect
    public void throwException(String s) {
        throw new RuntimeException(s);
    }

}

