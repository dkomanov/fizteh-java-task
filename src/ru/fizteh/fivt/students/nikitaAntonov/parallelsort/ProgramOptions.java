package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.fizteh.fivt.students.nikitaAntonov.utils.Utils;

/**
 * Могучий класс, парсящий аргументы, переданные программе, и явлюящийся
 * фабрикой классов/сборником настроек и констант для всех используемых в
 * программе алгоритмов.
 * 
 * @author Антонов Никита
 */
class ProgramOptions {

    private List<String> inputFilenames;
    private List<File> inputFiles;
    private String outputFilename = null;

    public List<BufferedReader> inputs;
    public BufferedWriter output = null;

    public boolean caseInsensitive = false;
    public boolean unique = false;
    public int numberOfThreads = 0;

    private static final int defaultChunkSize = 16384;
    private static final int estimateBytesInLine = 256;
    public int chunkSize = defaultChunkSize;
    
    private int actualFile = 0;

    public ProgramOptions(String args[]) throws IncorrectArgsException {
        inputFilenames = new ArrayList<String>();
        inputs = new ArrayList<BufferedReader>();
        inputFiles = new ArrayList<File>();

        parseArgs(args);
        prepareFiles();
        calculateNumberOfThreads();
        predictChunkSize();

    }

    private void parseArgs(String args[]) throws IncorrectArgsException {
        boolean skipNext = false;
        for (int i = 0, end = args.length; i < end; ++i) {

            if (skipNext) {
                skipNext = false;
                continue;
            }

            String opt = args[i];
            String nextOpt = ((i + 1 < end) ? args[i + 1] : null);

            if (args[i].matches("-\\w+")) {
                skipNext = parseOption(opt.substring(1), nextOpt);
            } else {
                inputFilenames.add(opt);
            }
        }
    }

    /* Поэма "Полцарства за getopt" */
    private boolean parseOption(String opt, String nextOpt)
            throws IncorrectArgsException {

        boolean nextOptWasUsed = false;

        for (int i = 0, e = opt.length(); i < e; ++i) {
            char c = opt.charAt(i);
            switch (c) {
            case 'i':
                caseInsensitive = true;
                break;
            case 'u':
                unique = true;
                break;
            case 't':
                if (i + 1 < e || nextOpt == null) {
                    throw new IncorrectArgsException(
                            "Option -t must be followed by parameter");
                }

                int number;
                try {
                    number = Integer.parseInt(nextOpt);
                } catch (NumberFormatException ex) {
                    throw new IncorrectArgsException(
                            "Number of threads must be correct int number");
                }

                if (number <= 0) {
                    throw new IncorrectArgsException(
                            "Number of threads should be not less than 1");
                }

                if (numberOfThreads != 0 && numberOfThreads != number) {
                    throw new IncorrectArgsException(
                            "Сonflicting options for number of threads");
                }

                numberOfThreads = number;
                nextOptWasUsed = true;

                break;
            case 'o':
                if (i + 1 < e || nextOpt == null) {
                    throw new IncorrectArgsException(
                            "Option -o must be followed by parameter");
                }

                outputFilename = nextOpt;
                nextOptWasUsed = true;

                break;
            default:
                throw new IncorrectArgsException("Incorrect option: " + c);
            }
        }

        return nextOptWasUsed;
    }

    private void prepareFiles() throws IncorrectArgsException {

        if (outputFilename == null) {
            output = new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            FileWriter fw = null;

            try {
                fw = new FileWriter(outputFilename);
                output = new BufferedWriter(fw);
            } catch (IOException e) {
                if (output == null) {
                    Utils.closeResource(fw);
                } else {
                    Utils.closeResource(output);
                }
                throw new IncorrectArgsException("Can't use \""
                        + outputFilename + "\" for writing\n" + e.getMessage());
            }
        }

        if (inputFilenames.isEmpty()) {
            inputs.add(new BufferedReader(new InputStreamReader(System.in)));
        } else {
            for (String name : inputFilenames) {
                File file = new File(name);

                if (!file.exists()) {
                    closeAll();
                    throw new IncorrectArgsException("Can't open \"" + name
                            + "\"");
                }

                inputFiles.add(file);

                FileReader fr = null;
                BufferedReader input = null;

                try {
                    fr = new FileReader(file);
                    input = new BufferedReader(fr);
                } catch (Exception e) {
                    closeAll();
                    if (input == null) {
                        Utils.closeResource(fr);
                    } else {
                        Utils.closeResource(input);
                    }
                    throw new IncorrectArgsException("IO Error for file + \""
                            + name + "\"\n" + e.getMessage());
                }

                inputs.add(input);
            }
        }

    }

    private void calculateNumberOfThreads() {

        if (numberOfThreads == 0) {
            numberOfThreads = Runtime.getRuntime().availableProcessors();
        }
    }

    private void predictChunkSize() {
        if (numberOfThreads == 1) {
            chunkSize = 0;
        } else if (!inputFilenames.isEmpty()) {

            int estimateLinesCount = 0;

            for (File file : inputFiles) {
                estimateLinesCount += file.length() / estimateBytesInLine;
            }

            chunkSize = estimateLinesCount / numberOfThreads;
        }
    }

    public void closeAll() {
        if (outputFilename != null) {
            Utils.closeResource(output);
        }

        if (!inputFilenames.isEmpty()) {
            for (BufferedReader input : inputs) {
                Utils.closeResource(input);
            }
        }
    }
    
    public Sorter getSorter() {
        
        if (numberOfThreads == 1) {
            return new SimpleSorter(this);
        } else {
            return new ParallelSorter(this);
        }
    }

    public ArrayList<String> getChunk() throws IOException {
        if (actualFile >= inputs.size()) {
            return null;
        }
        
        ArrayList<String> chunk = null;
        
        if (chunkSize == 0) {
            chunk = new ArrayList<>();
        } else {
            chunk = new ArrayList<>(chunkSize);
        }
        
        int i = 0;
        while (chunkSize == 0 || i != chunkSize) {
            String line = inputs.get(actualFile).readLine();
            
            if (line == null) {
                ++actualFile;
                break;
            }
            
            chunk.add(line);
            ++i;
        }
        
        if (chunk.isEmpty()) {
            return null;
        }
        
        return chunk;
    }
    
    public void write(List<String> result) throws IOException {
        String sep = System.lineSeparator();
        
        if (result == null) {
            return;
        }
        
        for (String line : result) {
            output.write(line);
            output.write(sep);
        }
    }
    
}

/**
 * Исключение, оповещающее о некоректных аргументах
 * 
 * @author Антонов Никита
 */
class IncorrectArgsException extends Exception {

    private static final long serialVersionUID = 5392124941354868981L;

    public IncorrectArgsException(String message) {
        super(message);
    }
}

class DefaultComparator implements Comparator<String> {
    public int compare(String one, String two) {
        return one.compareTo(two);
    }
}