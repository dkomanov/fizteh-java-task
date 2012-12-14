package ru.fizteh.fivt.students.yuliaNikonova.common;

import java.util.ArrayList;
import java.util.List;

public class ClassForTest implements InterfaceTest {
    public int number;

    public SubClassForTests newInstanceOfSubClass() {
        return new SubClassForTests(0);
    }

    private class SubClassForTests implements InterfaceTest {
        SubClassForTests(int num) {
            number = num;
        }

        public int numInt(int num) {
            return 1;
        }

        public int numLong(long num) {
            return 2;
        }

        public int numCollectInt() {
            return number;
        }

        public void numCollectVoid() {
            return;
        }

        public long numCollectLong() {
            return number;
        }

        public List numCollectList() {
            List returnList = new ArrayList<Integer>();
            returnList.add(number);
            returnList.add(number + 1);
            returnList.add(number + 2);
            return returnList;
        }

        public void numNotForProxy() {
            return;

        }
    }

    public ClassForTest(int num) {
        number = num;
    }

    public int numInt(int num) {
        return number;
    }

    public int numLong(long num) {
        return number;
    }

    public void numNotForProxy() {
        return;
    }

    public int numCollectInt() {
        return number;
    }

    public void numCollectVoid() {
        return;
    }

    public long numCollectLong() {
        return number;
    }

    public List numCollectList() {
        List returnList = new ArrayList<Integer>();
        returnList.add(number);
        returnList.add(number + 1);
        returnList.add(number + 2);
        return returnList;
    }
}
