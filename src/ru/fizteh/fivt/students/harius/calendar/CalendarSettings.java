/*
 * CalendarSettings.java
 * Dec 1, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.calendar;

import ru.fizteh.fivt.students.harius.argparse.*;
import java.util.List;

/*
 * Settings for calendar viewer
 */
public class CalendarSettings {
    @IntOpt(name = "-m")
    public Integer month = null;

    @IntOpt(name = "-y")
    public Integer year = null;

    @Flag(name = "-w")
    public boolean week = false;

    @StrOpt(name = "-t")
    public String zone = null;

    @TailOpt
    public List<String> rubbish;
}