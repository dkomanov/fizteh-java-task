package ru.fizteh.fivt.students.harius.argparse;

import java.lang.annotation.*;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface IntOpt {
    String name();
}