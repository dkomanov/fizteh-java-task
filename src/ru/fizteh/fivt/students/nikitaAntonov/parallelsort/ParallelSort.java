package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.List;

public class ParallelSort {
    
    public static void main(String args[]) {
        
        ProgramOptions opts = null;
        
        try {
            opts = new ProgramOptions(args);
        } catch (IncorrectArgsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            Sorter sorter = opts.getSorter();
        
            List<String> result = sorter.readAndSort();
            opts.write(result);
        } catch (IOException e) {
            System.err.println("Unknown IO error uccured\n" + e.getMessage());
        } finally {
            opts.closeAll();
        }
    }
    
}