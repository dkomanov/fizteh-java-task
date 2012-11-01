package ru.fizteh.fivt.students.frolovNikolay.calculator;

import java.util.ArrayList;

/*
 * Стек чисел.
 */
public class IntStack {
    private ArrayList<Integer> resourses;
    
    public IntStack() {
        resourses = new ArrayList<Integer>();
    }

    public void push(Integer numb) {
        resourses.add(numb);
    }
    
    public void pop() {
        resourses.remove(resourses.size() - 1);
    }
    
    public boolean isEmpty() {
        return resourses.isEmpty();
    }
    
    public int top() {
        return resourses.get(resourses.size() - 1).intValue();
    }
}