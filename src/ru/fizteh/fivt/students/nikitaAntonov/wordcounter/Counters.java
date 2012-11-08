package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Интерфейс счётчика - счётчик должен уметь посчитать строку
 * и напечать результаты своих подсчётов
 * 
 * @author Антонов Никита
 */
interface Counter {
    abstract public void count(String str);
    abstract public void printResults();
}

/**
 * Счётчик строк/слов
 * 
 * @author Антонов Никита
 */
class SimpleCounter implements Counter {
    
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

/**
 * Счётчик уникальных слов/строк
 * 
 * @author Антонов Никита
 */
class UniqueCounter implements Counter {
    
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
