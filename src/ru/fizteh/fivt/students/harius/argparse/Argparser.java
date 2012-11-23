package ru.fizteh.fivt.students.harius.argparse;

import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.*;

public class Argparser {
    private String[] args;

    public Argparser(String[] args) {
        this.args = args;
    }

    public void load(Object settings) throws ArgparseException {
        try {
            List<String> tail = new ArrayList<>();
            boolean[] used = new boolean[args.length];
            for (Field field : settings.getClass().getFields()) {
                for (Annotation annot : field.getAnnotations()) {
                    String name = null;
                    if (annot instanceof Flag) {
                        name = ((Flag)annot).name();
                        for (int i = 0; i < args.length; ++i) {
                            if (args[i].equals(name)) {
                                used[i] = true;
                                field.set(settings, true);
                            }
                        }
                    } else if (annot instanceof StrOpt || annot instanceof IntOpt) {
                        if (annot instanceof StrOpt) {
                            name = ((StrOpt)annot).name();
                        } else {
                            name = ((IntOpt)annot).name();
                        }
                        for (int i = 0; i < args.length; ++i) {
                            if (args[i].equals(name)) {
                                used[i] = true;
                                used[i + 1] = true;
                                if (annot instanceof StrOpt) {
                                    field.set(settings, args[i + 1]);
                                } else {
                                    field.set(settings, Integer.parseInt(args[i + 1]));
                                }
                            }
                        }
                    } else if (annot instanceof TailOpt) {
                        field.set(settings, tail);
                    }
                }
            }
            for (int i = 0; i < args.length; ++i) {
                if (!used[i]) {
                    tail.add(args[i]);
                }
            }
        } catch (IllegalAccessException illeg) {
            throw new ArgparseException("Illegal reflection access");
        } catch (ArrayIndexOutOfBoundsException out) {
            throw new ArgparseException("No value found after option");
        } catch (NumberFormatException notNum) {
            throw new ArgparseException("Integer expected after option");
        }
    }
}