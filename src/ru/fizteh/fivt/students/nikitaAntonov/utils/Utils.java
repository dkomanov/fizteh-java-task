package ru.fizteh.fivt.students.nikitaAntonov.utils;

import java.io.Closeable;

public class Utils {

    public static String concat(String list[]) {
        StringBuilder result = new StringBuilder();

        for (String s : list) {
            result.append(s);
            result.append(" ");
        }

        return result.toString();
    }

    public static void closeResource(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception expt) {
            }
        }
    }
    
    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }
    
    private static String escapeJavaString(String str) {
        if (str == null) {
            return null;
        }
        
        int len  = str.length();
        StringBuffer out = new StringBuffer(len * 2);
        
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (c > 0xfff) {
                out.append("\\u" + hex(c));
            } else if (c > 0xff) {
                out.append("\\u0" + hex(c));
            } else if (c > 0x7f) {
                out.append("\\u00" + hex(c));
            } else if (c < 32) {
                switch (c) {
                    case '\b':
                        out.append('\\');
                        out.append('b');
                        break;
                    case '\n':
                        out.append('\\');
                        out.append('n');
                        break;
                    case '\t':
                        out.append('\\');
                        out.append('t');
                        break;
                    case '\f':
                        out.append('\\');
                        out.append('f');
                        break;
                    case '\r':
                        out.append('\\');
                        out.append('r');
                        break;
                    default :
                        if (c > 0xf) {
                            out.append("\\u00" + hex(c));
                        } else {
                            out.append("\\u000" + hex(c));
                        }
                        break;
                }
            } else {
                switch (c) {
                    case '"':
                        out.append('\\');
                        out.append('"');
                        break;
                    case '\\':
                        out.append('\\');
                        out.append('\\');
                        break;
                    default :
                        out.append(c);
                        break;
                }
            }
        }
        
        return out.toString();
    }


}