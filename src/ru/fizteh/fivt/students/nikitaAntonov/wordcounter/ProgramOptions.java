package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Могучий класс, парсящий аргументы, переданные программе,
 * и создающий на их основе счётчики и сканнеры, а также 
 * выделяющий список имён файлов из аргументов 
 * 
 * @author Антонов Никита
 */
class ProgramOptions {
    
    private enum EntityToCount {UNDEFINED, LINES, WORDS}
    private enum Uniqueness {NOT_UNIQUE, CASE_SENSITIVE, CASE_UNSENSITIVE}
    
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
        
        for (int i = 0, e = opt.length(); i < e; ++i) {
            char c = opt.charAt(i);
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
                if (uniqueness == Uniqueness.CASE_UNSENSITIVE) {
                    throw new IncorrectArgsException("-u shouldn't be used together with -U");
                }
                uniqueness = Uniqueness.CASE_SENSITIVE;
                break;
            case 'U':
                if (uniqueness == Uniqueness.CASE_SENSITIVE) {
                    throw new IncorrectArgsException("-U shouldn't be used together with -u");
                }
                uniqueness = Uniqueness.CASE_UNSENSITIVE;
                break;
            default:
                throw new IncorrectArgsException(String.valueOf(c));
            }
        }
    }
    
    public FileTokenizer createTokenizer(String filename) throws IOException {
        switch (whatCount) {
        case LINES:
            return new LineTokenizer(filename);
        case WORDS:
        default:
            return new WordTokenizer(filename);
        }
    }
    
    public Counter createCounter() {
        switch (uniqueness) {
        case CASE_SENSITIVE:
            return new UniqueCounter(null);
        case CASE_UNSENSITIVE:
            return new UniqueCounter(String.CASE_INSENSITIVE_ORDER);
        case NOT_UNIQUE:
        default:
            return new SimpleCounter();
        }
    }
    
    public boolean aggregation() {
        return needAggregating;
    }
    
    public static void printUsage() {
        System.err.print("Usage:\n" +
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
