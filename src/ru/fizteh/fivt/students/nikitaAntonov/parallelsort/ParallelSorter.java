package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class ParallelSorter extends Sorter {

    public PriorityBlockingQueue<ArrayList<String>>  mergeQueue;
    private ExecutorService executor;
    AtomicInteger numberOfChunks;
    
    public ParallelSorter(ProgramOptions o) {
        super(o);
        
        mergeQueue = new PriorityBlockingQueue<>(o.numberOfThreads, new ChunkComparator());
        executor = Executors.newFixedThreadPool(o.numberOfThreads);
        numberOfChunks = new AtomicInteger(0);
    }

    @Override
    public List<String> readAndSort() throws IOException {
        
        ArrayList<String> chunk = opts.getChunk();
        
        if (chunk == null) {
            return chunk;
        }
        
        while (chunk != null) {
            executor.execute(new SortingTask(chunk, this));
            
            chunk = opts.getChunk();
            numberOfChunks.incrementAndGet();
        }
        
        while (true) {
            ArrayList<String> a = mergeQueue.take();
            
            if (numberOfChunks.get() == 1) {
                chunk = a;
                break;
            }
            
            ArrayList<String> b = mergeQueue.take();
            executor.execute(new MergingTask(a, b, this));
        }
        
        executor.shutdown();
        
        return chunk;
    }

}

class ChunkComparator implements Comparator<ArrayList<String>> {

    @Override
    public int compare(ArrayList<String> o1, ArrayList<String> o2) {
        return o1.size() - o2.size();
    }
    
}