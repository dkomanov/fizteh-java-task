package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import ru.fizteh.fivt.PerformanceTest;

public class Tester {
    public static void main(String[] args) {
        new Tester().run();
    }

    void run() {
        Runnable pre = new Runnable() {
            @Override
            public void run() {
                ParallelSort.preFiltering = true;
                new ParallelSort().main(new String[]{"-iu", "-o", "output.txt", "-t", "1", "input.txt"});
            }
        };

        new PerformanceTest(pre).runTestAndPrint(100);

        Runnable post = new Runnable() {
            @Override
            public void run() {
                ParallelSort.preFiltering = false;
                new ParallelSort().main(new String[]{"-iu", "-o", "output.txt", "-t", "1", "input.txt"});
            }
        };

        new PerformanceTest(post).runTestAndPrint(100);
    }
}
