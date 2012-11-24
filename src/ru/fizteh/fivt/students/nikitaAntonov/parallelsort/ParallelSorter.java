package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ParallelSorter extends Sorter {

    public PriorityBlockingQueue<ArrayList<Line>> mergeQueue;
    private ExecutorService executor;
    AtomicInteger numberOfChunks;

    public ParallelSorter(ProgramOptions o) {
        super(o);

        mergeQueue = new PriorityBlockingQueue<>(o.numberOfThreads,
                new ChunkComparator());
        executor = Executors.newFixedThreadPool(o.numberOfThreads);
        numberOfChunks = new AtomicInteger(0);
    }

    @Override
    public List<Line> readAndSort() throws IOException, InterruptedException {

        ArrayList<Line> chunk = opts.getChunk();

        if (chunk == null) {
            return chunk;
        }

        try {

            while (chunk != null) {
                executor.execute(new SortingTask(chunk, this));

                chunk = opts.getChunk();
                numberOfChunks.incrementAndGet();
            }

            while (true) {
                ArrayList<Line> a = mergeQueue.take();

                if (numberOfChunks.get() == 1) {
                    chunk = a;
                    break;
                }

                ArrayList<Line> b = mergeQueue.take();
                executor.execute(new MergingTask(a, b, this));
            }
        } catch (InterruptedException e) {
            throw e;
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
            executor.shutdownNow();
        }
        return chunk;
    }

}

class ChunkComparator implements Comparator<ArrayList<Line>> {

    @Override
    public int compare(ArrayList<Line> o1, ArrayList<Line> o2) {
        return o1.size() - o2.size();
    }

}