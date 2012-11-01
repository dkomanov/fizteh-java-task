package ru.fizteh.fivt.students.frolovNikolay.calculator;
/*
 * Данный класс проверяет
 * наличие посторонних
 * арифметическому выражению
 * символов.
 */
public class CorrectChecker {
    public static boolean handle(String expression) {
        for (int i = 0; i < expression.length(); ++i) {
            if (('0' <= expression.charAt(i) && expression.charAt(i) <= '9')
                || expression.charAt(i) == '+' || expression.charAt(i) == '-'
                || expression.charAt(i) == '*' || expression.charAt(i) == '/'
                || expression.charAt(i) == ')' || expression.charAt(i) == '('
                || expression.charAt(i) == ' ') {
                continue;
            }
            System.err.println(i);
            return false;
        }
        return true;
    }
}
