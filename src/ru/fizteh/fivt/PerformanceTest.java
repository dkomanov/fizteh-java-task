package ru.fizteh.fivt;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class PerformanceTest {

    private final Runnable runnable;

    public PerformanceTest(Runnable runnable) {
        this.runnable = runnable;
    }

    public void runTest(final int n) {
        if (n <= 1000) {
            throw new IllegalArgumentException("n should be greater than 1000. Actual: " + n);
        }

        // warm up
        for (int i = 0; i < 1000; i++) {
            runnable.run();
        }

        // real test
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            runnable.run();
        }
        long finish = System.nanoTime();

        long nanos = finish - start;
        long msecs = nanos / 1000;
        long millis = msecs / 1000;

        System.out.println("Total (nanos):    " + nanos);
        System.out.println("Total (msecs):    " + msecs);
        System.out.println("Total (millis):   " + millis);
        System.out.println("Total (seconds):  " + (millis / 1000));

        System.out.println("Single (nanos):   " + (nanos / n));
        System.out.println("Single (msecs):   " + (msecs / n));
        System.out.println("Single (millis):  " + (millis / n));
        System.out.println("Single (seconds): " + (millis / 1000 / n));
    }

    public static void main(String[] args) {
        PerformanceTest test = new PerformanceTest(new Runnable() {
            @Override
            public void run() {
                String.format("%d %d", 1, 1);
            }
        });
        test.runTest(100000);
    }
}
