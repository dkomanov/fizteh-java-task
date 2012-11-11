package ru.fizteh.fivt.students.tolyapro.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Shell {

    public static void main(String[] args) {
        ShellCommandsExecutor shell = new ShellCommandsExecutor();
        String currPath = System.getProperty("user.dir");
        if (args.length > 0) {
            StringBuilder argumentsBuilder = new StringBuilder();
            for (String s : args) {
                argumentsBuilder.append(s);
                argumentsBuilder.append(" ");
            }
            String[] arguments = argumentsBuilder.toString().split(";");
            for (String arg : arguments) {
                try {
                    if (!arg.isEmpty()) {
                        arg = arg.replaceAll("^\\s+", "");
                        arg = arg.replaceAll("$\\s+", "");
                        currPath = shell.execute(arg, currPath);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        } else {
            while (true) {
                System.out.print("$ ");
                InputStreamReader in = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(in);
                try {
                    String input = br.readLine();
                    if (input != null) {
                        String[] arguments = input.split(";");
                        for (String arg : arguments) {
                            try {
                                if (!arg.isEmpty()) {
                                    arg = arg.replaceAll("^\\s+", "");
                                    arg = arg.replaceAll("$\\s+", "");
                                    currPath = shell.execute(arg, currPath);
                                }
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    } else {
                        System.exit(0);
                    }

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
