package ru.fizteh.fivt.students.nikitaAntonov.calculator;

/**
 * Класс исключений для калькулятора
 * 
 * @author Антонов Никита
 */
@SuppressWarnings("serial")
public class CalculatorException extends Exception {

    /* Вопрос: почему нельзя просто взять и отнаследоваться от Exception?
     * PS. Это плохо, оставлять такие вопросы в коде? Мб лучше задавать
     * их в комментариях к комиту?
     */

    public  CalculatorException(String message) {
        super(message);
    }
}
