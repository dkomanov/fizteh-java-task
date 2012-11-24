package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.util.ArrayList;

class MergingTask implements Runnable {

    private ArrayList<Line> chunk1;
    private ArrayList<Line> chunk2;
    private ParallelSorter sorter;

    public MergingTask(ArrayList<Line> a, ArrayList<Line> b, ParallelSorter s) {
        chunk1 = a;
        chunk2 = b;
        sorter = s;
    }

    @Override
    public void run() {
        ArrayList<Line> result = new ArrayList<>(chunk1.size() + chunk2.size());

        int i = 0;
        int j = 0;

        while (i < chunk1.size() && j < chunk2.size()) {

            if (sorter.opts.unique && result.size() > 0) {
                while (i < chunk1.size()
                        && sorter.cmp.compare(chunk1.get(i),
                                result.get(result.size() - 1)) == 0) {
                    if (chunk1.get(i).chunkNo < result.get(result.size() - 1).chunkNo) {
                        result.set(result.size() - 1, chunk1.get(i));
                    }
                    ++i;
                }
                while (j < chunk2.size()
                        && sorter.cmp.compare(chunk2.get(j),
                                result.get(result.size() - 1)) == 0) {
                    if (chunk2.get(j).chunkNo < result.get(result.size() - 1).chunkNo) {
                        result.set(result.size() - 1, chunk2.get(j));
                    }
                    ++j;
                }
            }

            if (i >= chunk1.size() || j >= chunk2.size()) {
                break;
            }

            if (sorter.cmp.compare(chunk1.get(i), chunk2.get(j)) <= 0) {
                result.add(chunk1.get(i));
                ++i;
            } else {
                result.add(chunk2.get(j));
                j++;
            }

        }

        if (i >= chunk1.size()) {
            for (; j < chunk2.size(); ++j) {
                if ((sorter.opts.unique && sorter.cmp.compare(chunk2.get(j),
                        result.get(result.size() - 1)) != 0)
                        || !sorter.opts.unique) {
                    result.add(chunk2.get(j));
                } else if (sorter.opts.unique
                        && sorter.cmp.compare(chunk2.get(j),
                                result.get(result.size() - 1)) == 0
                        && chunk2.get(j).chunkNo < result
                                .get(result.size() - 1).chunkNo) {
                    result.set(result.size() - 1, chunk2.get(j));
                }
            }
        } else {
            for (; i < chunk1.size(); ++i) {
                if ((sorter.opts.unique && sorter.cmp.compare(chunk1.get(i),
                        result.get(result.size() - 1)) != 0)
                        || !sorter.opts.unique) {
                    result.add(chunk1.get(i));
                } else if (sorter.opts.unique
                        && sorter.cmp.compare(chunk1.get(i),
                                result.get(result.size() - 1)) == 0
                        && chunk1.get(i).chunkNo < result
                                .get(result.size() - 1).chunkNo) {
                    result.set(result.size() - 1, chunk1.get(i));
                }
            }
        }

        chunk1.clear();
        chunk2.clear();
        sorter.mergeQueue.put(result);
        sorter.numberOfChunks.decrementAndGet();
    }
}
