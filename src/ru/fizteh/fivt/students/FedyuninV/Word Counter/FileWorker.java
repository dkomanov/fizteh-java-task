import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Scanner;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class FileWorker {

        public ResultContainer run(String fileName, boolean ignoreCase,
                                   boolean readLines, boolean agregate, boolean unique) {
        ResultContainer result = new ResultContainer(ignoreCase);
        String incomingData;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((incomingData = reader.readLine()) != null) {
                if (!readLines) {
                    String[] tokens = incomingData.split(" ");
                    for (int i = 0; i < tokens.length; i++) {
                        result.add(tokens[i]);
                    }
                } else {
                    result.add(incomingData);
                }
            }
            if (!agregate) {
                System.out.println(fileName);
                result.print(unique);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return result;
    }
}
