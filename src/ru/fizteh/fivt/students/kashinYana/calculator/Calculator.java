package ru.fizteh.fivt.students.kashinYana.calculator;


public class Calculator {
    public static void main(String[] args) throws Exception {

        StringBuilder input = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            input.append(args[i] + " ");
        }
        String inputString = input.toString();

        if (inputString.length() == 0) {
            System.out.println("Write formula");
            System.exit(0);
        }
        try {
            CalculatorBody calculatorBody = new CalculatorBody(inputString);
            System.out.println(calculatorBody.recognaze());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
