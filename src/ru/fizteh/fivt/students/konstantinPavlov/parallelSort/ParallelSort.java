package ru.fizteh.fivt.students.konstantinPavlov.parallelSort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import ru.fizteh.fivt.students.konstantinPavlov.Utils;

public class ParallelSort {
    public static boolean flagOnlyUnique = false;
    public static boolean flagNoRegister = false;
    public static int countOfThreads = 0;
    public static String output = "";

    public static void setFlags(String[] args, List<String> stringsToSort) {
        boolean wasInputFiles = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].isEmpty()) {
                continue;
            }

            if (args[i].charAt(0) == '-') {

                if (args[i].length() == 1) {
                    System.err.println("Error: invalid flag");
                    continue;
                }

                boolean toContinue = false;
                for (int j = 1; j < args[i].length(); ++j) {
                    switch (args[i].charAt(j)) {
                    case 'u':
                        flagOnlyUnique = true;
                        break;

                    case 'i':
                        flagNoRegister = true;
                        break;

                    case 't':
                        if (i >= args.length) {
                            System.err
                                    .println("Error: invalid count of thread");
                            System.exit(1);
                        }

                        try {
                            countOfThreads = Integer.parseInt(args[++i]);
                        } catch (Exception e) {
                            System.err
                                    .println("Error: invalid count of threads");
                            System.exit(1);
                        }

                        if (countOfThreads < 1) {
                            System.err
                                    .println("Error: invalid count of threads");
                            System.exit(1);
                        }
                        toContinue = true;
                        break;

                    case 'o':
                        if (i + 1 >= args.length
                                || args[i + 1].charAt(0) == '-') {
                            System.err
                                    .println("Error: invalid output filename");
                            System.exit(1);
                        }

                        output = args[++i];
                        toContinue = true;
                        break;

                    default:
                        System.err.println("Error: invalid  parametr: \'"
                                + args[i].charAt(j) + "\'");
                        System.exit(1);
                        break;
                    }
                    if (toContinue) {
                        break;
                    }
                }
            } else {
                try {
                    stringsFromFileToArray(args[i], stringsToSort);
                    wasInputFiles = true;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }

        if (countOfThreads == 0) {
            countOfThreads = Runtime.getRuntime().availableProcessors();
        }

        if (!wasInputFiles) {
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(System.in);
                bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringsToSort.add(line);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

    }

    public static void main(String[] args) {
        BufferedWriter out = null;
        FileWriter fileWriter = null;

        try {
            List<String> stringsToSort = new ArrayList<String>();

            setFlags(args, stringsToSort);

            // check output
            if (!output.isEmpty()) {
                try {
                    fileWriter = new FileWriter(output);
                    out = new BufferedWriter(fileWriter);
                } catch (Exception e) {
                    Utils.closer(out);
                    System.err.println("Can't write to file '" + output + "':"
                            + e.getMessage());
                    System.exit(1);
                }
            } else {
                out = new BufferedWriter(new OutputStreamWriter(System.out));
            }

            int portion = (stringsToSort.size() / countOfThreads) + 1;

            int currentCountOfThreads = 0;

            Comparator<String> comparator = new ComparatorForStrings();

            if (flagNoRegister) {
                comparator = String.CASE_INSENSITIVE_ORDER;
            }

            if (countOfThreads > 1) {
                ArrayList<Integer> mergeRange = new ArrayList<Integer>();
                int curFrom = 0;
                int curTo = 0;
                Object synchronizer = new Object();

                LinkedBlockingQueue<PairOfIndexesToSort> linkedBlockingQueue = new LinkedBlockingQueue<PairOfIndexesToSort>(
                        countOfThreads);
                for (int i = 0; i < countOfThreads; ++i) {
                    curFrom = curTo;
                    curTo = curFrom + portion;

                    if (stringsToSort.size() - curTo < portion) {
                        curTo = stringsToSort.size();
                    }

                    if (curFrom == stringsToSort.size()) {
                        break;
                    }

                    mergeRange.add(curFrom);
                    PairOfIndexesToSort range = new PairOfIndexesToSort(
                            curFrom, curTo);
                    linkedBlockingQueue.put(range);
                    Sort srt = new Sort(stringsToSort, linkedBlockingQueue,
                            synchronizer, flagNoRegister);
                    srt.start();
                    currentCountOfThreads++;
                }
                mergeRange.add(curTo);

                for (int i = 0; i < currentCountOfThreads; ++i) {
                    PairOfIndexesToSort sync = new PairOfIndexesToSort(0, 0);
                    linkedBlockingQueue.put(sync);
                }

                while (!linkedBlockingQueue.isEmpty()) {
                    synchronized (synchronizer) {
                        synchronizer.wait();
                    }
                }

                if (currentCountOfThreads > 1) {
                    boolean lastMerge = false;
                    int sizeOfRange = 1;
                    int i = 0;
                    while (2 * sizeOfRange < mergeRange.size()) {
                        i = 0;
                        while (i < mergeRange.size() - 1) {
                            if (i + 2 * sizeOfRange < mergeRange.size()) {
                                if (mergeRange.get(i) == 0
                                        && mergeRange.get(i + 2 * sizeOfRange) == stringsToSort
                                                .size()) {
                                    lastMerge = true;
                                } else {
                                    lastMerge = false;
                                }
                                merger(stringsToSort, mergeRange.get(i),
                                        mergeRange.get(i + sizeOfRange),
                                        mergeRange.get(i + 2 * sizeOfRange),
                                        lastMerge, comparator);
                                i += 2 * sizeOfRange;
                            } else {
                                if (mergeRange.get(i - 2 * sizeOfRange) == 0
                                        && mergeRange.get(i + sizeOfRange) == stringsToSort
                                                .size()) {
                                    lastMerge = true;
                                } else {
                                    lastMerge = false;
                                }
                                merger(stringsToSort,
                                        mergeRange.get(i - 2 * sizeOfRange),
                                        mergeRange.get(i),
                                        mergeRange.get(i + sizeOfRange),
                                        lastMerge, comparator);
                                break;
                            }
                        }
                        sizeOfRange = sizeOfRange * 2;
                    }
                }

            } else {
                if (flagNoRegister) {
                    Collections.sort(stringsToSort,
                            String.CASE_INSENSITIVE_ORDER);
                } else {
                    Collections.sort(stringsToSort);
                }
            }

            String currentString = stringsToSort.get(0);
            out.write(currentString + System.lineSeparator());
            String nextString;
            for (int i = 1; i < stringsToSort.size(); ++i) {
                if (flagOnlyUnique) {
                    nextString = stringsToSort.get(i);
                    if (comparator.compare(nextString, currentString) != 0) {
                        out.write(nextString + System.lineSeparator());
                    }
                    currentString = nextString;
                } else {
                    out.write(stringsToSort.get(i) + System.lineSeparator());
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (!output.isEmpty()) {
                Utils.closer(out);
            } else {
                try {
                    out.flush();
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
            Utils.closer(fileWriter);
        }
    }

    public static void merger(List<String> stringsToSort, int left, int center,
            int right, boolean lastMerge, Comparator<String> comparator) {
        List<String> mergedStrings = new ArrayList<String>();
        int leftIndex = left;
        int rightIndex = center;
        int k;

        while (leftIndex < center && rightIndex < right) {
            if (flagOnlyUnique && lastMerge && mergedStrings.size() > 0) {
                while (leftIndex < center
                        && comparator.compare(stringsToSort.get(leftIndex),
                                mergedStrings.get(mergedStrings.size() - 1)) == 0) {
                    leftIndex++;
                }
                while (rightIndex < right
                        && comparator.compare(
                                mergedStrings.get(mergedStrings.size() - 1),
                                stringsToSort.get(rightIndex)) == 0) {
                    rightIndex++;
                }
            }

            if (leftIndex >= center || rightIndex >= right) {
                break;
            }

            if ((comparator.compare(stringsToSort.get(leftIndex),
                    stringsToSort.get(rightIndex)) <= 0)) {
                mergedStrings.add(stringsToSort.get(leftIndex));
                leftIndex++;
            } else {
                mergedStrings.add(stringsToSort.get(rightIndex));
                rightIndex++;
            }
        }

        if (leftIndex >= center) {
            for (k = rightIndex; k < right; k++) {
                if ((flagOnlyUnique && lastMerge && !stringsToSort.get(k)
                        .equals(mergedStrings.get(mergedStrings.size() - 1)))
                        || !flagOnlyUnique || !lastMerge) {
                    mergedStrings.add(stringsToSort.get(k));
                }
            }
        } else {
            for (k = leftIndex; k < center; k++) {
                if ((flagOnlyUnique && lastMerge && !stringsToSort.get(k)
                        .equals(mergedStrings.get(mergedStrings.size() - 1)))
                        || !flagOnlyUnique || !lastMerge) {
                    mergedStrings.add(stringsToSort.get(k));
                }
            }
        }

        if (flagOnlyUnique && lastMerge) {
            stringsToSort.clear();
            stringsToSort.addAll(mergedStrings);
        } else {
            for (k = left; k < right; k++) {
                stringsToSort.set(k, mergedStrings.get(k - left));
            }
        }

    }

    public static void stringsFromFileToArray(String path,
            List<String> stringsToSort) throws Exception {
        File file = new File(path);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        if (!file.exists()) {
            System.err.println("Error: can't open file '" + file + "'");
            System.exit(1);
        }

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String string;
            while ((string = bufferedReader.readLine()) != null) {
                stringsToSort.add(string);
            }
        } finally {
            Utils.closer(fileReader);
            Utils.closer(bufferedReader);
        }
    }
}
