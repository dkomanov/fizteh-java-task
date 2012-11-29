package ru.fizteh.fivt.students.harius.calendar;

import ru.fizteh.fivt.students.harius.argparse.*;

public class CalendarSettings {
    @IntOpt(name = "-m")
    public Integer month = null;

    @IntOpt(name = "-y")
    public Integer year = null;

    @Flag(name = "-w")
    public boolean week = false;

    @StrOpt(name = "-t")
    public String zone = null;
}