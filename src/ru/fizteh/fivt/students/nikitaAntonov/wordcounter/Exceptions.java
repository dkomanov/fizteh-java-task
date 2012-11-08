package ru.fizteh.fivt.students.nikitaAntonov.wordcounter;

/**
 * Исключение, оповещающее об отсутствии аргументов на входе программы
 * 
 * @author Антонов Никита
 */
class EmptyArgsException extends Exception {

    private static final long serialVersionUID = 6101462613212173841L;
}

/**
 * Исключение, оповещающее о некоректных аргументах
 * 
 * @author Антонов Никита
 */
class IncorrectArgsException extends Exception {

    private static final long serialVersionUID = 6101462613212173841L;

    public IncorrectArgsException(String message) {
        super(message);
    }
}
