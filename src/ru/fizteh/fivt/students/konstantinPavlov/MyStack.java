package ru.fizteh.fivt.students.konstantinPavlov;

import java.util.ArrayList;

class MyStack<T> {
    private ArrayList<T> list;
    MyStack() {
        list=new ArrayList<T>();
    }
    
    public void push(T o) {
        list.add(o);
    }

    public T pop() {
        return list.remove(list.size() - 1);
    }
    
    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }
    
    public T peek() {
        return list.get(list.size() - 1);
    }
}
