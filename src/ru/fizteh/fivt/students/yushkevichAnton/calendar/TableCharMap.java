package ru.fizteh.fivt.students.yushkevichAnton.calendar;

import java.util.*;

public class TableCharMap extends HashMap<Long, Character> {
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;

    long getIndex(int x, int y) {
        return ((long) x << 32) + y;
    }

    public boolean containsKey(int x, int y) {
        return containsKey(getIndex(x, y));
    }

    public char get(int x, int y) {
        return get(getIndex(x, y));
    }

    public void put(int x, int y, char c) {
        if (c == ' ') {
            return;
        }
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        minY = Math.min(minY, y);
        maxY = Math.max(maxY, y);
        put(getIndex(x, y), c);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (containsKey(x, y)) {
                    stringBuilder.append(get(x, y));
                } else {
                    stringBuilder.append(' ');
                }
            }
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}