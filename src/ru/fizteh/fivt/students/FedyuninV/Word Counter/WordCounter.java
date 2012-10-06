import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */


public class WordCounter {

    private static boolean agregate;
    private static boolean ignoreCase;
    private static boolean readLines;
    private static boolean unique;
    private static boolean errorOccured;


    private static void initFlags() {
        agregate = false;
        ignoreCase = false;
        readLines = false;
        unique = false;
        errorOccured = false;
    }

    private static void parametersParser(String parameters) {
        for (int i = 1; i < parameters.length(); i++)
            switch (parameters.charAt(i)) {
                case ('U'):
                    ignoreCase = true;
                    unique = true;
                    break;
                case ('u'):
                    ignoreCase = false;
                    unique = true;
                    break;
                case ('a'):
                    agregate = true;
                    break;
                case ('l'):
                    readLines = true;
                    break;
                case ('w'):
                    readLines = false;
                    break;
                default:
                    System.out.println("Incorrect parameter " + parameters.charAt(i));
            }
    }

    public static void main(String[] args) {

        initFlags();
        int currArgIndex = 0;
        while (currArgIndex < args.length  &&  args[currArgIndex].charAt(0) == '-') {
            parametersParser(args[currArgIndex]);
            currArgIndex++;
        }
        if (currArgIndex == args.length) {
            System.out.println("usage: java WordCounter [-alwuU ...] FILE1 [FILE2 ...]");
            System.exit(1);
        }

        ResultContainer result = new ResultContainer(ignoreCase);
        FileWorker worker = new FileWorker();

        for (; currArgIndex < args.length; currArgIndex++) {
            if (agregate) {
                ResultContainer temp = worker.run(args[currArgIndex], ignoreCase, readLines, agregate, unique);
                errorOccured = errorOccured || (temp == null);
                result.add(temp);
            } else {
                worker.run(args[currArgIndex], ignoreCase, readLines, agregate, unique);
            }
        }
        if (agregate) {
            result.print(unique);
        }
        if (errorOccured) {
            System.exit(1);
        }
    }
}
