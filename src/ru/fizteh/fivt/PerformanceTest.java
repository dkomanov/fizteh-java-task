package ru.fizteh.fivt;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class PerformanceTest {

    private final Runnable runnable;

    public PerformanceTest(Runnable runnable) {
        this.runnable = runnable;
    }

    public RunResult runTestAndPrint(int n) {
        RunResult result = runTest(n);

        print("Total", result.getTotal());
        print("Single", result.getSingle());

        return result;
    }

    private static void print(String name, RunTimes times) {
        System.err.println(name + " (nanos):    " + times.getNanos());
        System.err.println(name + " (msecs):    " + times.getMsecs());
        System.err.println(name + " (millis):   " + times.getMillis());
        System.err.println(name + " (seconds):  " + times.getSeconds());
    }

    public RunResult runTest(final int n) {
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

        return new RunResult(
                new RunTimes(nanos, msecs, millis, millis / 1000),
                new RunTimes(nanos / n, msecs / n, millis / n, millis / 1000 / n)
        );
    }

    public static void main(String[] args) {
        PerformanceTest test = new PerformanceTest(new Runnable() {
            @Override
            public void run() {
                String.format("%d %d", 1, 1);
            }
        });
        test.runTestAndPrint(100000);
    }

    public static class RunResult {
        private final RunTimes total;
        private final RunTimes single;

        public RunResult(RunTimes total, RunTimes single) {
            this.total = total;
            this.single = single;
        }

        public RunTimes getTotal() {
            return total;
        }

        public RunTimes getSingle() {
            return single;
        }
    }

    public static class RunTimes {
        private final long nanos;
        private final long msecs;
        private final long millis;
        private final long seconds;

        public RunTimes(long nanos, long msecs, long millis, long seconds) {
            this.nanos = nanos;
            this.msecs = msecs;
            this.millis = millis;
            this.seconds = seconds;
        }

        public long getNanos() {
            return nanos;
        }

        public long getMsecs() {
            return msecs;
        }

        public long getMillis() {
            return millis;
        }

        public long getSeconds() {
            return seconds;
        }
    }
}
