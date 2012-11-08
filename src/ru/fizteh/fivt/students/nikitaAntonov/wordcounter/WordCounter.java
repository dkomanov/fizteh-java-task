package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.io.IOException;
import java.util.Scanner;

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
		    System.out.print("Invalid option -- ");
		    System.out.println(e.getMessage());
		    System.exit(1);
		}
		
		Counter counter = null;
		boolean errorOccured = false;
		
		for (String filename : opts.fileNames) {
		    if (counter == null || !opts.aggregation()) {
		        counter = opts.createCounter();
		    }
		    
		    Scanner scanner = null;
		    try {
		        scanner = opts.createScanner(filename);  
	
		        while (scanner.hasNext()) {
		            counter.count(scanner.next());
		        }
		        
		        if (scanner.ioException() != null) {
		            throw scanner.ioException();
		        }
		    } catch (IOException e) {
		        System.out.println("An error occured: " + filename + ": " + e.getMessage());
		        if (opts.aggregation()) {
		            System.exit(1);
		        }
		        errorOccured = true;
		        continue;
		    } finally {
		        if (scanner != null)
		            scanner.close();
		    }
		    
		    if (!opts.aggregation()) {
		        System.out.println(filename + ":");
		        counter.printResults();
		    }
		}
		
		if(opts.aggregation()) {
		    counter.printResults();
		}
		
		if (errorOccured)
		    System.exit(1);
	}
}
