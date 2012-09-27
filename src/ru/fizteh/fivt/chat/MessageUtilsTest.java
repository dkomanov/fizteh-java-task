package ru.fizteh.fivt.chat;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.util.Arrays;

public final class MessageUtilsTest {

    @Test
    public void hello() {
        byte[] bytes = MessageUtils.hello("123");
        byte[] expected = new byte[]{1, 0, 0, 0, 3, 49, 50, 51};
        check(expected, bytes);
    }

    @Test
    public void message() {
        byte[] bytes = MessageUtils.message("Привет");
        byte[] expected = new byte[]{2, 0, 0, 0, 12, -48, -97, -47, -128, -48, -72, -48, -78, -48, -75, -47, -126};
        check(expected, bytes);
    }

    @Test
    public void bye() {
        byte[] bytes = MessageUtils.bye();
        byte[] expected = new byte[]{3, 0, 0, 0, 0};
        check(expected, bytes);
    }

    private void check(byte[] expected, byte[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionFailedError("Expected: " + Arrays.toString(expected)
                    + ", actual: " + Arrays.toString(actual));
        }
    }
}
