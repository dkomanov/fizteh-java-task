package ru.fizteh.fivt.students.frolovNikolay.calculator;

import java.util.ArrayList;

/*
 * Символьный стек.
 */
public class CharStack {
    private ArrayList<Character> resourses;
    
    public CharStack() {
        resourses = new ArrayList<Character>();
    }

    public void push(Character symb) {
        resourses.add(symb);
    }
    
    public void pop() {
        resourses.remove(resourses.size() - 1);
    }
    
    public boolean isEmpty() {
        return resourses.isEmpty();
    }
    
    public char top() {
        return resourses.get(resourses.size() - 1).charValue();
    }
}
