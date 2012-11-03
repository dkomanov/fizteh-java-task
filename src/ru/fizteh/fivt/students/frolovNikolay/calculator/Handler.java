package ru.fizteh.fivt.students.frolovNikolay.calculator;

/*
 * Фролов Николай 196 группа
 * Задание 1. Калькулятор.
 * Исполнительный класс (main).
 */
public class Handler {
    public static void main(String[] argv) {
        if (argv.length == 0) {
            System.out.println("Использование: java Handler <арифметическое выражение>");
            System.exit(1);
        } else {
            StringBuilder unioner = new StringBuilder();
            for (String iter : argv) {
                unioner.append(iter);
                unioner.append(' ');
            }
            String expression = unioner.toString();
            try {
                if (CorrectChecker.check(expression)) {
                    String rpnExpression = RpnConvertor.convert(expression);
                    int result = Calculator.calculate(rpnExpression);
                    System.out.println("Value: " + result );
                } else {
                    System.err.println("Error. Expression has incorrect symbols");
                    System.exit(1);
                }
            } catch (Exception crush) {
                System.err.println(crush.getMessage());
                System.exit(1);
            }
        }
    }
}