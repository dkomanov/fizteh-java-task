/*
 * TestInterface.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy.test;

import ru.fizteh.fivt.students.harius.proxy.*;
import ru.fizteh.fivt.proxy.*;
import java.util.List;

/*
 * Correct interface for testing
 */
public interface TestInterface {

    @Collect
    int intFromStringCollect(String arg);

    @Collect
    long longFromStringCollect(String arg);

    @Collect
    Integer iintFromStringCollect(String arg);

    @Collect
    Long llongFromStringCollect(String arg);

    @Collect
    List<String> listFromIntegerCollect(int arg);

    @DoNotProxy
    void voidFromNoneIgnore();

    long longFromInt(int arg);

    int intFromLong(long arg);

    int intFromStringAndLong(String arg, long arg2);
}