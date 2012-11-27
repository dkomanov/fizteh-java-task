package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class CaseInsensitiveStringHashSet implements Set<String> {
    HashMap<Integer, TreeSet<String>> map = new HashMap<Integer, TreeSet<String>>();

    int caseInsensitiveHashCode(String s) {
        int res = s.length();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                c = 0;
            }
            res = res * 31 + c;
        }
        return res;
    }

    int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<String> iterator() {
        throw new NotImplementedException();
    }

    @Override
    public Object[] toArray() {
        throw new NotImplementedException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new NotImplementedException();
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof String)) {
            return false;
        }
        String s = (String) o;
        int hash = caseInsensitiveHashCode(s);
        if (!map.containsKey(hash)) {
            return false;
        }
        if (map.get(hash).contains(s)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean add(String s) {
        int hash = caseInsensitiveHashCode(s);
        if (!map.containsKey(hash)) {
            map.put(hash, new TreeSet<String>(String.CASE_INSENSITIVE_ORDER));
        }
        if (map.get(hash).add(s)) {
            size++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        size = 0;
        map.clear();
    }
}