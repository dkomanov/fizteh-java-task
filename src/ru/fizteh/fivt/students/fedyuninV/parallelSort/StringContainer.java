package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.Comparator;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringContainer {
    private int fileNum;
    private int lineNum;
    private String string;


    public StringContainer(String string, int lineNum, int fileNum) {
        this.string = string;
        this.lineNum = lineNum;
        this.fileNum = fileNum;
    }

    public String string() {
        return string;
    }

    public int fileNum() {
        return fileNum;
    }

    public int lineNum() {
        return lineNum;
    }


    static class DefaultComparator implements Comparator<StringContainer> {
        public int compare(StringContainer x, StringContainer y) {
            if (x.string().equals(y.string())) {
                if (x.fileNum() == y.fileNum()) {
                    if (x.lineNum() < y.lineNum()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                if (x.fileNum() < y.fileNum()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return x.string().compareTo(y.string());
        }
    }

    static class CaseInsensitiveComparator implements Comparator<StringContainer> {
        public int compare(StringContainer x, StringContainer y) {
            if (x.string().equalsIgnoreCase(y.string())) {
                if (x.fileNum() == y.fileNum()) {
                    if (x.lineNum() < y.lineNum()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                if (x.fileNum() < y.fileNum()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return x.string().compareToIgnoreCase(y.string());
        }
    }
}




