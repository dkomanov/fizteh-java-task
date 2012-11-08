package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import sun.management.counter.StringCounter;
import sun.management.counter.Units;
import sun.org.mozilla.javascript.Undefined;

/**
 * Класс для подсчёта числа строк/столбцов
 * 
 * @author Антонов Никита
 */
public class WordCounter {
	
	public static void main(String args[]) {
	    
	    ProgramOptions opts;
	    
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
	}
}


class OptionsException extends Exception {

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

interface Counter {
    public void count(String str);
    public void printResults();
}

class SimpleCounter implements Counter {
    
    private int c;
    
    public SimpleCounter() {
        c = 0;
    }
    
    public void count(String str) {
        ++c;
    }
    
    public void printResults() {
        System.out.println(c);
    }
    
}

class UniqueCounter implements Counter {
    
    private Map<String, Integer> dictionary;
    
    public UniqueCounter(Comparator<String> comparator) {
        dictionary = new TreeMap<String, Integer>(comparator);
    }
    
    public void count(String str) {
        
    }
    
    public void printResults() {
        // TODO: add here smth
    }
}


class ProgramOptions {
    
    public List<String> fileNames;   
    private boolean needAggregating = false;
    
    enum EntityToCount {UNDEFINED, LINES, WORDS}
    enum Uniqueness {NOT_UNIQUE, CASESENSITIVE, CASEUNSENSITIVE}
    
    private EntityToCount whatCount = EntityToCount.UNDEFINED; 
    private Uniqueness uniqueness = Uniqueness.NOT_UNIQUE;
    
    public ProgramOptions(String args[]) throws OptionsException {
        
        if (args.length == 0)
            throw new EmptyArgsException();
        
        fileNames = new ArrayList<String>();
        
        for (String opt : args) {
            if (opt.matches("-\\w")) {
                parseOption(opt.substring(1));
            }
            else {
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
    
    public Scanner createScanner() {
        
    }
    
    public Counter createCounter() {
        
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

