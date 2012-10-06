package ru.fizteh.fivt.students.altimin.calculator;

public class CalculatorRunner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Program should recieve expression to calculate");
            System.exit(0);
        }
        String expression = "";
        for (String expression_part: args)
            expression += expression_part;
        try {
            Calculator calculator = new Calculator(expression);
            System.out.println(calculator.calculate());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
