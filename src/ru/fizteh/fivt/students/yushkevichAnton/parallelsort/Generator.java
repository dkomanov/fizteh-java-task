package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.io.*;
import java.util.*;

public class Generator {
    public static void main(String[] args) {
        try {
            int n = 10000;
            ArrayList<String> strings = new ArrayList<String>();
            String random = getRandomString();
            for (int i = 0; i < n / 2; i++) {
                strings.add(random);
                strings.add(getRandomString());
            }
            Collections.shuffle(strings);

            PrintWriter out = new PrintWriter(new FileWriter("input.txt"));
            for (String s : strings) {
                out.println(s);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Random random = new Random();

    private static String getRandomString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            int c = random.nextInt(26);
            if (random.nextBoolean()) {
                c += 'a';
            } else {
                c += 'A';
            }
            stringBuilder.append((char) c);
        }
        return stringBuilder.toString();
    }
}