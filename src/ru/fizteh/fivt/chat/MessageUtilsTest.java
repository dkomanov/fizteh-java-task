package ru.fizteh.fivt.chat;

import java.util.Arrays;

public final class MessageUtilsTest {

    public void testHello() {
        byte[] bytes = MessageUtils.hello("123");
        byte[] expected = new byte[]{1, 1, 0, 0, 0, 3, 49, 50, 51};
        check(expected, bytes);
    }

    public void testMessage() {
        byte[] bytes = MessageUtils.message("123", "Привет");
        byte[] expected = new byte[]{
                2,
                2,
                0, 0, 0, 3,
                49, 50, 51,
                0, 0, 0, 12,
                -48, -97, -47, -128, -48, -72, -48, -78, -48, -75, -47, -126
        };
        check(expected, bytes);
    }

    public void testBye() {
        byte[] bytes = MessageUtils.bye();
        byte[] expected = new byte[]{3, 0};
        check(expected, bytes);
    }

    public void testError() {
        byte[] bytes = MessageUtils.error("Bad");
        byte[] expected = new byte[]{127, 1, 0, 0, 0, 3, 66, 97, 100};
        check(expected, bytes);
    }

    private void check(byte[] expected, byte[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new RuntimeException("Expected: " + Arrays.toString(expected)
                    + ", actual: " + Arrays.toString(actual));
        }
    }
}
