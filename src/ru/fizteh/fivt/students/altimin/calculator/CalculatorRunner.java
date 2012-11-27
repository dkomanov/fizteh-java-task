package ru.fizteh.fivt.students.altimin.calculator;

public class CalculatorRunner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Program should receive expression to calculate");
            System.exit(1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String expressionPart : args) {
            stringBuilder.append(" ");
            stringBuilder.append(expressionPart);
        }
        String expression = stringBuilder.toString();
        try {
            Calculator calculator = new Calculator(expression);
            System.out.println(calculator.calculate());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
