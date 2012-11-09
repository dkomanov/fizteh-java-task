package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * Интерфейс для дешёвой китайской подделки под Scanner.
 * Как и в случае с фальшивыми ёлочными игрушками, использование
 * классов, имплементирующих этот интерфейс не приносит радости.
 * Но ходят легенды, что на вход программы могут подать бинарный файл.
 * Поэтому, пусть будет.
 * 
 * @author Антонов Никита
 */
interface FileTokenizer extends Iterator<String> {
    IOException ioException();
    void close();
}

/**
 * Класс, читающий файл и возвращает по строке через next()
 * 
 * @author Антонов Никита
 */
class LineTokenizer implements FileTokenizer {
    
    private BufferedReader reader = null;
    protected boolean isFinished = false;
    protected String nextLine = null;
    private IOException exception = null;
    
    public LineTokenizer(String filename) throws FileNotFoundException {
        FileReader fr = null;
        
        try {
            fr = new FileReader(filename);
            reader = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            isFinished = true;
            throw e;
        }
        
        prepareNextLine();
    }
    
    protected void prepareNextLine() {
        
        if (isFinished)
        {
            nextLine = null;
            return;
        }
        
        try {
            nextLine = reader.readLine();
            if (nextLine == null) {
                isFinished = true;
            }
        } catch (IOException e) {
            exception = e;
            isFinished = true;
        }
    }
    
    @Override
    public String next() {
        
        if (isFinished) {
            return null;
        }
        
        String result = nextLine;
        prepareNextLine();
        return result;
    }
    
    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            isFinished = true;
            exception = e;
        }
    }

    @Override
    public boolean hasNext() {
        return !isFinished;
    }

    @Override
    public IOException ioException() {
        return exception;
    }
    
    // О Боже, кто додумался добавить remove в базовый интерфейс итератора? %)
    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

/**
 * Класс, читающий файл и возвращает по слову через next()
 * 
 * @author Антонов Никита
 */
class WordTokenizer extends LineTokenizer {

    private String nextWord = null;
    private StringTokenizer tokenizer;
    
    public WordTokenizer(String filename) throws FileNotFoundException {
        super(filename);
        prepareNextWord();
    }
    
    @Override
    public String next() {
        String result = nextWord;
        prepareNextWord();
        return result;
    }
    
    private void prepareNextWord() {
        while (!isFinished && !tokenizer.hasMoreTokens())
        {
            super.prepareNextLine();
            if (isFinished) {
                break;
            }
            
            tokenizer = new StringTokenizer(nextLine, " \t!?';:,.)(@#<>\\/");
            assert(tokenizer != null);
        }
        
        if (isFinished)
        {
            nextWord = null;
            return;
        }
        
        nextWord = tokenizer.nextToken();
    }
    
    @Override
    protected void prepareNextLine()
    {
        super.prepareNextLine();
        if (nextLine != null) {
            tokenizer = new StringTokenizer(nextLine, " \t!?';:,.)(@#<>\\/");
        }
    }
}