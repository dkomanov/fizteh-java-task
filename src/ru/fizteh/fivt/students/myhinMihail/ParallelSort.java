package ru.fizteh.fivt.students.myhinMihail;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Math;

class SortPiece {
    public int from = 0;
    public int to = 0;
    
    SortPiece(int f, int t) {
        from = f;
        to = t;
    }
}

public class ParallelSort {
    final static int MAX_LENGTH = 10000;
    final static int MIN_LENGTH = 1000;
    
    public static boolean onlyUnique = false;
    public static boolean notCaseSensitive = false;
    public static int threadsCount = 0;
    public static String output = "";
    
    public static class Sorter extends Thread {
        private List<String> list;
        private Object synchronizer;
        private LinkedBlockingQueue<SortPiece> queue;

        public Sorter(List<String> inList, LinkedBlockingQueue<SortPiece> q, Object sync) {
            list = inList;
            queue = q;
            synchronizer = sync;
        }
        
        public void run() {
            while (true) {
                SortPiece sync = null;
                try {
                    sync = queue.take();
                    if (sync.from == sync.to) { 
                        synchronized (synchronizer) {
                            if (queue.isEmpty()) {
                                synchronizer.notify();
                            }
                        }
                        break;
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                if (notCaseSensitive) {
                    Collections.sort(list.subList(sync.from, sync.to), String.CASE_INSENSITIVE_ORDER);
                } else {
                    Collections.sort(list.subList(sync.from, sync.to));
                }
            }
        }
        
    }
    
    public static class StringComp implements Comparator<String> {
        public int compare(String one, String two) {
            return one.compareTo(two);
        }
    }
    
    public static void merge(List<String> inList, int low, int mid, int high, boolean lastMerge, Comparator<String> comp) {
        List<String> mergeList = new ArrayList<String>();
        int h = low, j = mid, k;
            
        while (h < mid && j < high) {
            
            if (onlyUnique && lastMerge && mergeList.size() > 0) {
                while (h < mid && comp.compare(inList.get(h), mergeList.get(mergeList.size() - 1)) == 0) {
                    h++;
                }
                while (j < high && comp.compare(mergeList.get(mergeList.size() - 1), inList.get(j)) == 0) {
                    j++;
                }
            }
            
            if (h >= mid || j >= high) {
                break;
            }
            
            if ((comp.compare(inList.get(h), inList.get(j)) <= 0)) {    
                mergeList.add(inList.get(h));
                h++;
            } else {
                mergeList.add(inList.get(j));  
                j++;
            }
        }
        

        if (h >= mid) {
            for (k = j; k < high; k++) {
                if ((onlyUnique && lastMerge && !inList.get(k).equals(mergeList.get(mergeList.size() - 1))) 
                    || !onlyUnique || !lastMerge) {
                    mergeList.add(inList.get(k)); 
                }
            }
        } else {
            for (k = h; k < mid; k++) {
                if ((onlyUnique && lastMerge && !inList.get(k).equals(mergeList.get(mergeList.size() - 1))) 
                    || !onlyUnique || !lastMerge) {
                    mergeList.add(inList.get(k)); 
                }
            }
        }

        if (onlyUnique && lastMerge) {
            inList.clear();
            inList.addAll(mergeList);
        } else {
            for (k = low; k < high; k++) {
                inList.set(k, mergeList.get(k - low));
            }
        }

    }
    
    public static void readKeys(String[] args, List<String> list) {
        int params = 0;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].isEmpty()) {
                params++;
                continue;
            }
            
            if (args[i].charAt(0) == '-') {
                params++;
                
                if (args[i].length() == 1) {
                    System.err.println("Error: empty key");
                    continue;
                }
                
                boolean toBreak = false;
                for (int j = 1; j < args[i].length(); ++j) {
                    switch (args[i].charAt(j)) {
                        case 'u': 
                            onlyUnique = true;
                            break;
                        
                        case 'i':
                            notCaseSensitive = true;
                            break;
                        
                        case 't':
                            if (i + 1 >= args.length) {
                                Utils.printErrorAndExit("Error: no threads count");
                            }
                            
                            try {
                                threadsCount = Integer.parseInt(args[++i]);
                            } catch (Exception expt) {
                                Utils.printErrorAndExit("Error: no threads count");
                            }
                            
                            int proc = Runtime.getRuntime().availableProcessors();
                            if (threadsCount < 1) {
                                System.err.println("Error: threads count is lower then 1");
                                System.exit(1);
                            }
                            
                            if (threadsCount > proc * 4) {
                                System.err.println("Error: threads count is higher then " + proc * 4);
                                System.exit(1);
                            }
                            toBreak = true;
                            params++;
                            break;
                            
                        case 'o':
                            if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
                                Utils.printErrorAndExit("Error: no output file name");
                            }
                            
                            output = args[++i];
                            toBreak = true;
                            params++;
                            break;
                        
                        default:
                        	Utils.printErrorAndExit("Unknown parametr: \'" + args[i].charAt(j) + "\'");
                            break;
                    }
                    if (toBreak) {
                        break;
                    }
                }
            } else {
                try {
                    readFileToArray(args[i], list);
                } catch (Exception expt) {
                    Utils.printErrorAndExit(expt.getMessage());
                }
            }
        }
        
        BufferedReader br = null;
        InputStreamReader isr = null;
        
        try {
            if (params == args.length) {
                isr = new InputStreamReader(System.in);
                br = new BufferedReader(isr);
                String line;
            
                while ((line = br.readLine()) != null) {
                    list.add(line);
                }
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        } finally {
            Utils.tryClose(br);
        }
       
    }
    
    public static void readFileToArray(String path, List<String> list) throws Exception {
        File file = new File(path);
        FileReader fr = null;
        BufferedReader reader = null;
        
        if (!file.exists()) {
            System.err.println("Error: can not open " + file);
            System.exit(1);
        }

        try {
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            String line;
            
            while ((line = reader.readLine()) != null) {
                list.add(line);
            } 
                
        } finally {
            Utils.tryClose(fr);
            Utils.tryClose(reader);
        }    
    }
        
    public static void main(String[] args) {
        BufferedWriter out = null;
        FileWriter fw = null;
        
        try {
            String separator = System.getProperty("line.separator");
            List<String> list = new ArrayList<String>();
            
            readKeys(args, list);
        
            if (threadsCount == 0) {
                threadsCount = Runtime.getRuntime().availableProcessors() + 1;
            }
        
            if (!output.isEmpty()) {
                try {
                    fw = new FileWriter(output);
                    out = new BufferedWriter(fw);
                } catch (Exception e) {
                    Utils.tryClose(out);
                    System.err.println("Can not write to " + output + "\n" + e.getMessage());
                    System.exit(1);
                }
            } else {
                out = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            
            int linesCount = list.size();
            int portion = 0;
            
            int maxLength = Math.max(linesCount / threadsCount, MAX_LENGTH);
            
            if (linesCount >= MIN_LENGTH * threadsCount && linesCount <= maxLength * threadsCount) {
                portion = linesCount / threadsCount;
            } else { 
                if (linesCount <= maxLength * threadsCount) {
                    portion = MIN_LENGTH; 
                } else {
                    portion = maxLength;
                }
            }
            
            int realTreadsCount = 0;
            
            if (threadsCount > 1) {
                Vector<Integer> mergeRange = new Vector<Integer>();
                int curFrom = 0;
                int curTo = 0;
                Object synchronizer = new Object();
                
                LinkedBlockingQueue<SortPiece> queue = new LinkedBlockingQueue<SortPiece>(threadsCount);
                for (int i = 0; i < threadsCount; i++) {
                    curFrom = curTo ;
                    curTo = curFrom + portion;
                
                    if (linesCount - curTo < portion) {
                        curTo = linesCount;
                    }
                
                    if (curFrom == linesCount) { 
                        break; 
                    }
                    
                    mergeRange.add(curFrom);
                    SortPiece sync = new SortPiece(curFrom, curTo);
                    queue.put(sync);
                    Sorter srt = new Sorter(list, queue, synchronizer);
                    srt.start();
                    realTreadsCount++;
                }
                mergeRange.add(curTo);
            
                for (int i = 0; i < realTreadsCount; ++i) {
                    SortPiece sync = new SortPiece(0, 0);
                    queue.put(sync);
                }
            
                while(!queue.isEmpty()) {
                    synchronized (synchronizer) {
                        synchronizer.wait();
                    }
                }
                Comparator<String> comp = new StringComp();
                
                if (notCaseSensitive) {
                    comp = String.CASE_INSENSITIVE_ORDER;
                }
                if (realTreadsCount > 1) {
                    boolean lastMerge = false;
                    int mult = 1;
                    int i = 0;
                    while (2 * mult < mergeRange.size()) {
                        i = 0;
                        while (i < mergeRange.size() - 1) {
                            if (i + 2 * mult < mergeRange.size()) {
                                if (mergeRange.get(i) == 0 && mergeRange.get(i + 2 * mult) == list.size()) {
                                    lastMerge = true;
                                } else {
                                    lastMerge = false;
                                }
                                merge(list, mergeRange.get(i), mergeRange.get(i + mult), mergeRange.get(i + 2 * mult), lastMerge, comp);
                                i += 2 * mult;
                            } else {
                                if (mergeRange.get(i - 2 * mult) == 0 && mergeRange.get(i + mult) == list.size()) {
                                    lastMerge = true;
                                } else {
                                    lastMerge = false;
                                }
                                merge(list, mergeRange.get(i - 2 * mult), mergeRange.get(i), mergeRange.get(i + mult), lastMerge, comp);
                                break;
                            }
                        }
                        mult *= 2;
                    }
                }
                
            } else {
                if (notCaseSensitive) {
                    Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
                } else {
                    Collections.sort(list);
                }
            }
            
            out.write(list.get(0) + separator);
            for (int i = 1; i < list.size(); ++i) {
                if (onlyUnique && realTreadsCount <= 1) {
                    if (notCaseSensitive) {
                        if (String.CASE_INSENSITIVE_ORDER.compare(list.get(i), list.get(i - 1)) != 0) {
                            out.write(list.get(i) + separator);
                        }
                    } else {
                        if (!list.get(i).equals(list.get(i - 1))) {
                            out.write(list.get(i) + separator);
                        }
                    }
                } else { 
                    out.write(list.get(i) + separator);
                }
            }
            
        } catch (Exception expt) {
            System.err.println("Error: " + expt.getMessage());
        } finally {
            if (!output.isEmpty()) {
                Utils.tryClose(out);
            } else {
                try {
                    out.flush();
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
            
            Utils.tryClose(fw);
        }
        
    }

}
