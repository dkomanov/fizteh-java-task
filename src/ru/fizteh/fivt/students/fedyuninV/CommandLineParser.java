package ru.fizteh.fivt.students.fedyuninV;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class CommandLineParser implements Runnable{
    private CommandLine owner;
    private BufferedReader reader;
    private String exitCommand;


    public CommandLineParser(CommandLine owner, InputStream input, String exitCommand) {
        this.owner = owner;
        this.exitCommand = exitCommand;
        InputStreamReader inputStreamReader = new InputStreamReader(input);
        reader = new BufferedReader(inputStreamReader);
    }


    public void run() {
        while (true) {
            try {
                String incomingData = reader.readLine();
                if (incomingData != null  &&  incomingData.length() > 0) {
                    String[] tokens = incomingData.split("[ ]+");
                    if (incomingData.charAt(0) == '/') {
                        owner.execute(tokens[0], Arrays.copyOfRange(tokens, 1, tokens.length));
                        if (tokens[0].equals(exitCommand)) {
                            break;
                        }
                    } else {
                        owner.execute("", new String[]{incomingData});
                    }
                }
            } catch (IOException ignored) {
                //TODO: bad code
            }
        }
        IOUtils.tryClose(reader);
    }
}
