package ru.fizteh.fivt.students.alexanderKuzmin.wordCounter;

import java.util.HashMap;
import ru.fizteh.fivt.PerformanceTest;

/**
 * @author Alexander Kuzmin
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final HashMap<String, Integer> hmap = new HashMap<String, Integer>();
        for (int i = 0; i < 64; ++i) {
            hmap.put("1fdn" + i, 1);
        }

        // Test #1
        PerformanceTest test1 = new PerformanceTest(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                for (String str : hmap.keySet()) {
                    sb.append(str).append(" ").append(hmap.get(str))
                            .append("\n");
                }
                System.out.println(sb);
            }
        });

        // Test #2
        PerformanceTest test2 = new PerformanceTest(new Runnable() {
            @Override
            public void run() {

                for (String str : hmap.keySet()) {
                    System.out.println(str + " " + hmap.get(str));
                }
            }
        });

        // Test #3
        PerformanceTest test3 = new PerformanceTest(new Runnable() {
            @Override
            public void run() {

                for (String str : hmap.keySet()) {
                    System.out.print(str);
                    System.out.print(" ");
                    System.out.println(hmap.get(str));
                }
            }
        });
         //test1.runTest(100000);
         test2.runTest(100000);
         //test3.runTest(100000);
    }
}