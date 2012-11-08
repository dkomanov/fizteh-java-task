package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

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


abstract class OptionsException extends Exception {

    private static final long serialVersionUID = 6683980083518203006L;
    
    public OptionsException(String message) {
        super(message);
    }
    
    public OptionsException()
    {
        super();
    }
}

class EmptyArgsException extends OptionsException {

    private static final long serialVersionUID = 6101462613212173841L;
}

class IncorrectArgsException extends OptionsException {

    private static final long serialVersionUID = 6101462613212173841L;
    
    public  IncorrectArgsException(String message) {
        super(message);
    }
}

abstract class Counter {
    abstract public void count(String str);
    abstract public void printResults();
}

class SimpleCounter extends Counter {
    
    private int c;
    
    public SimpleCounter() {
        c = 0;
    }
    
    @Override
    public void count(String str) {
        ++c;
    }
    
    @Override
    public void printResults() {
        System.out.println(c);
    }
    
}

class UniqueCounter extends Counter {
    
    private Map<String, Integer> dictionary;
    
    public UniqueCounter(Comparator<String> comparator) {
        dictionary = new TreeMap<String, Integer>(comparator);
    }
    
    @Override
    public void count(String str) {
        Integer value = dictionary.get(str);
        if (value == null) {
            dictionary.put(str, 1);
        } else {
            dictionary.put(str, value + 1);
        }
    }
    
    @Override
    public void printResults() {
        for (Entry<String, Integer> e : dictionary.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
    }
}


class ProgramOptions {
    
    public List<String> fileNames;   
    private boolean needAggregating = false;
    
    enum EntityToCount {UNDEFINED, LINES, WORDS}
    enum Uniqueness {NOT_UNIQUE, CASESENSITIVE, CASEUNSENSITIVE}
    
    private EntityToCount whatCount = EntityToCount.UNDEFINED; 
    private Uniqueness uniqueness = Uniqueness.NOT_UNIQUE;
    
    public ProgramOptions(String args[]) throws EmptyArgsException, IncorrectArgsException {
        
        if (args.length == 0)
            throw new EmptyArgsException();
        
        fileNames = new ArrayList<String>();
        
        for (String opt : args) {
            if (opt.matches("-\\w+")) {
                parseOption(opt.substring(1));
            } else {
                fileNames.add(opt);
            }
        }
        
        if (fileNames.size() == 0)
            throw new EmptyArgsException();
    }
    
    void parseOption(String opt) throws IncorrectArgsException {
        char letters[] = opt.toCharArray();
        for (char c : letters) {
            switch (c) {
            case 'a':
                needAggregating = true;
                break;
            case 'w':
                if (whatCount == EntityToCount.LINES) {
                    throw new IncorrectArgsException("-w shouldn't be used together with -l");
                }
                whatCount = EntityToCount.WORDS;
                break;
            case 'l':
                if (whatCount == EntityToCount.WORDS) {
                    throw new IncorrectArgsException("-l shouldn't be used together with -w");
                }
                whatCount = EntityToCount.LINES;
                break;
            case 'u':
                if (uniqueness == Uniqueness.CASEUNSENSITIVE) {
                    throw new IncorrectArgsException("-u shouldn't be used together with -U");
                }
                uniqueness = Uniqueness.CASESENSITIVE;
                break;
            case 'U':
                if (uniqueness == Uniqueness.CASESENSITIVE) {
                    throw new IncorrectArgsException("-U shouldn't be used together with -u");
                }
                uniqueness = Uniqueness.CASEUNSENSITIVE;
                break;
            default:
                throw new IncorrectArgsException(String.valueOf(c));
            }
        }
    }
    
    public Scanner createScanner(String filename) throws IOException {
        Scanner tmp = new Scanner(new File(filename));
        switch (whatCount) {
        case LINES:
            tmp.useDelimiter("\\n");
            break;
        case WORDS:
        default:
            tmp.useDelimiter("\\s+");
        }
        
        return tmp;
    }
    
    public Counter createCounter() {
        switch (uniqueness) {
        case CASESENSITIVE:
            return new UniqueCounter(null);
            //break;
        case CASEUNSENSITIVE:
            return new UniqueCounter(String.CASE_INSENSITIVE_ORDER);
            //break;
        case NOT_UNIQUE:
        default:
            return new SimpleCounter();
            //break;
        }
    }
    
    public boolean aggregation() {
        return needAggregating;
    }
    
    public static void printUsage() {
        System.out.print("Usage:\n" +
                "java WordCounter [keys] FILE1 FILE2 ...\n" +
                "\n" +
                "Ключи:\n" +
                "\t-l — считать количество строк\n" +
                "\t-w — считать количество слов (по умолчанию активен этот режим)\n" +
                "\t-u — считать только уникальные элементы (с учётом регистра)\n" +
                "\t-U — считать только уникальные элементы (без учёта регистра)\n" +
                "\t-a — агрегировать ли информацию по всем файлам\n");
    }
}

