package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Могучий класс, парсящий аргументы, переданные программе,
 * и создающий на их основе счётчики и сканнеры, а также 
 * выделяющий список имён файлов из аргументов 
 * 
 * @author Антонов Никита
 */
class ProgramOptions {
    
    private enum EntityToCount {UNDEFINED, LINES, WORDS}
    private enum Uniqueness {NOT_UNIQUE, CASESENSITIVE, CASEUNSENSITIVE}
    
    public List<String> fileNames;   
    private boolean needAggregating = false;
   
    private EntityToCount whatCount = EntityToCount.UNDEFINED; 
    private Uniqueness uniqueness = Uniqueness.NOT_UNIQUE;
    
    public ProgramOptions(String args[]) throws EmptyArgsException, IncorrectArgsException {
        
        if (args.length == 0) {
            throw new EmptyArgsException();
        }

        fileNames = new ArrayList<String>();

        for (String opt : args) {
            if (opt.matches("-\\w+")) {
                parseOption(opt.substring(1));
            } else {
                fileNames.add(opt);
            }
        }
        
        if (fileNames.size() == 0) {
            throw new EmptyArgsException();
        }
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
