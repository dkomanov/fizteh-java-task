package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.io.IOException;

/**
 * Класс для подсчёта числа строк/столбцов
 * 
 * @author Антонов Никита
 */
public class WordCounter {

    public static void main(String args[]) {

        ProgramOptions opts = null;

        try {
            opts = new ProgramOptions(args);   
        } catch (EmptyArgsException e) {
            ProgramOptions.printUsage();
            System.exit(1);
        } catch (IncorrectArgsException e) {
            System.err.print("Invalid option -- ");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Counter counter = null;
        boolean errorOccured = false;
        
        for (String filename : opts.fileNames) {
            if (counter == null || !opts.aggregation()) {
                counter = opts.createCounter();
            }

            FileTokenizer scanner = null;
            try {
                scanner = opts.createTokenizer(filename);
                
                while (scanner.hasNext()) {
                    String tmp = scanner.next();
                    counter.count(tmp);
                }

                if (scanner.ioException() != null) {
                    throw scanner.ioException();
                }
            } catch (IOException e) {
                System.err.println("An error occured: " + filename + ": " + e.getMessage());
                if (opts.aggregation()) {
                    System.exit(1);
                }
                errorOccured = true;
                continue;
            } finally {
                if (scanner != null) {
                    IOException old_e = scanner.ioException();
                    scanner.close();
                    IOException new_e = scanner.ioException();
                    if (old_e != new_e) {
                        System.err.println("It is impossible to close it correctly: " + new_e.getMessage());
                    }
                }
            }

            if (!opts.aggregation()) {
                System.out.println(filename + ":");
                counter.printResults();
            }
        }

        if (opts.aggregation()) {
            counter.printResults();
        }

        if (errorOccured) {
            System.exit(1);
        }
    }
}
