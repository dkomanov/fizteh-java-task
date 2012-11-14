import java.util.Scanner;


public class Shell {
    public static void main(String[] args) {
        Executor runner = new Executor();
        if (args.length == 0) {
            Scanner inputData = new Scanner(System.in);
            
            while (true) {
                System.out.print("$ ");
                String s = inputData.nextLine();
                try {
                    runner.parseAndExec(s);
                } catch(ShellException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        } else {
            StringBuilder newComand = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                newComand.append(args[i]);
                newComand.append(" ");
            }
            try {
                runner.parseAndExec(newComand.toString());
            } catch (ShellException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
