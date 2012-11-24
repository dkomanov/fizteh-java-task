package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

public class ParallelSort {
    
    public static void main(String args[]) {
        
        try {
            ProgramOptions opts = new ProgramOptions(args);
        } catch (IncorrectArgsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
        Sorter sorter = opts.getSorter();
        
        String[] result = sorter.readAndSort();
        opts.write(result);
    }
    
}