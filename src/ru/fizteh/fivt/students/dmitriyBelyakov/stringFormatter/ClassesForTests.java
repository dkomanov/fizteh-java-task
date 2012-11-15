package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import java.util.Calendar;

class ClassEleven {
    private int intField = 11;
    public String stringField = "eleven";
    String nullField = null;
}

class ClassTwelve extends ClassEleven {
    ClassEleven eleven = new ClassEleven();
}

class ClassWithCalendar {
    Calendar calendar;

    ClassWithCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}