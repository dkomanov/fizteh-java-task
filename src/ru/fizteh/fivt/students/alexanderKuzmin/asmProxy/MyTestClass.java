package ru.fizteh.fivt.students.alexanderKuzmin.asmProxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import asmProxy.MyTestInterface;

/**
 * @author Alexander Kuzmin group 196 Class MyTestClass
 * 
 */

public class MyTestClass implements MyTestInterface {
    @Override
    @DoNotProxy
    public int getI(int a) {
        return 124;
    }

    @Override
    @Collect
    public Long getL(Long a) {
        return ++a;
    }

    @Override
    public int smth(int a, int b) {
        return a + a * b;
    }

    @Override
    public long getA(long a) {
        return -a;
    }

    @Override
    @Collect
    public int getLength(String s) {
        return s.length();
    }

    @Override
    @Collect
    public int getSomething() {
        return 15;
    }
}
