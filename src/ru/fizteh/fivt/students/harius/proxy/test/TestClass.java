/*
 * TestClass.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy.test;

import java.util.*;

/*
 * Correct interface implementation
 */
public class TestClass implements TestInterface {

    private final String yell;

    public TestClass(String yell) {
        this.yell = yell;
    } 

    @Override
    public int intFromStringCollect(String arg) {
        return arg.length();
    }

    @Override
    public long longFromStringCollect(String arg) {
        return arg.length();
    }

    @Override
    public Integer iintFromStringCollect(String arg) {
        return arg.length();
    }

    @Override
    public Long llongFromStringCollect(String arg) {
        return Long.valueOf(arg.length());
    }

    @Override
    public List<String> listFromIntegerCollect(int arg) {
        return Collections.nCopies(arg, yell);
    }

    @Override
    public void voidFromNoneIgnore() {}

    @Override
    public long longFromInt(int arg) {
        return 42;
    }

    @Override
    public int intFromLong(long arg) {
        return 43;
    }

    @Override
    public int intFromStringAndLong(String arg, long arg2) {
        return arg.length();
    }
}