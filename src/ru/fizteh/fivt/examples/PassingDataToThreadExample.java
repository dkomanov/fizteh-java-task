package ru.fizteh.fivt.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Dmitriy Komanov (spacelord)
 */
public class PassingDataToThreadExample {

    public static void main(String[] args) throws Exception {
        Holder holder = new Holder();

        Thread thread = new MyThread(holder);

        thread.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        if (line == null) {
            System.exit(0);
        }

        if ("interrupt".equals(line)) {
            thread.interrupt();
        } else {
            int value = Integer.parseInt(line);
            synchronized (holder) {
                holder.value = value;
                holder.notify();
            }
        }

        thread.join();
    }

    private static class Holder {
        public Integer value;
    }

    private static class MyThread extends Thread {
        private final Holder holder;

        private MyThread(Holder holder) {
            this.holder = holder;
        }

        @Override
        public void run() {
            synchronized (holder) {
                while (holder.value == null) {
                    try {
                        holder.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted");
                        return;
                    }
                }

                System.out.println(holder.value);
            }
        }
    }
}
