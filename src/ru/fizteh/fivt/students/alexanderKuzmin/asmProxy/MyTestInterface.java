package ru.fizteh.fivt.students.alexanderKuzmin.asmProxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

/**
 * @author Alexander Kuzmin group 196 Class MyTestInterface
 * 
 */

public interface MyTestInterface {
    @DoNotProxy
    public int getI(int a);

    @Collect
    public Long getL(Long a);

    int smth(int a, int b);

    public long getA(long a);

    @Collect
    public int getLength(String s);

    @Collect
    public int getSomething();
}
