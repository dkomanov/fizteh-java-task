package ru.fizteh.fivt.students.frolovNikolay.calculator;

/*
 * Фролов Николай 196 группа
 * Задание 1. Калькулятор.
 * Исполнительный класс (main).
 */
public class Handler {
    public static void main(String[] argv) {
        if (argv.length == 0) {
            System.err.println("Error. Empty expression");
            System.exit(1);
        } else {
            StringBuilder unioner = new StringBuilder();
            for (String Iter : argv) {
                unioner.append(Iter);
                unioner.append(' ');
            }
            String expression = unioner.toString();
            try {
                if (CorrectChecker.handle(expression)) {
                    String rpnExpression = RpnConvertor.handle(expression);
                    int result = Calculator.handle(rpnExpression);
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
