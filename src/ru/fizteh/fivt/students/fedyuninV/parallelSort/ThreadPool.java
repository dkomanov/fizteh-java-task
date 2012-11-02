package ru.fizteh.fivt.students.fedyuninV.parallelSort;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ThreadPool implements Runnable {

    private BlockingQueue<Runnable> queue;

    private Thread[] threadsList;
    public ThreadPool(int threadsCount) {
        //this.queue = queue;
        this.queue = new BlockingQueue<Runnable>();
        threadsList = new Thread[threadsCount];
    }

    public void start() {
        for (int i = 0; i < threadsList.length; i++) {
            threadsList[i] = new Thread(this);
            threadsList[i].start();
        }
    }


    public void join() {
        for (int i = 0; i < threadsList.length; i++) {
            //System.out.println(i);
            try {
                threadsList[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //System.out.println(i);
        }
    }

    public void add(Runnable runnable) {
        synchronized (queue) {
            queue.add(runnable);
        }
    }

    public void run() {
        //System.out.println("Started");
        while (!queue.isEmpty()) {
            queue.remove().run();
        }
        //System.out.println("Finished");
    }
}
