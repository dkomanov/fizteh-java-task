package ru.fizteh.fivt.students.nikitaAntonov.calculator;

/**
 * Вспомогательный класс, представляющий собой лексему
 * 
 * @author Антонов Никита
 */
class Lexem {
    public enum Type {
        BRACKET_OPEN, BRACKET_CLOSE, OP_LEVEL1, OP_LEVEL2, NUM, END, BEGIN
    }

    public enum Op1Type {
        PLUS, MINUS
    }

    public enum Op2Type {
        MULT, DIV, MOD
    }

    public Type type;
    public int number;
    public Op1Type type1;
    public Op2Type type2;

    public Lexem(int num) {
        type = Type.NUM;
        number = num;
    }

    public Lexem(Type t) {
        type = t;
    }

    public Lexem(char c) throws CalculatorException {
        switch (c) {
        case '+':
            type = Type.OP_LEVEL1;
            type1 = Op1Type.PLUS;
            break;
        case '-':
            type = Type.OP_LEVEL1;
            type1 = Op1Type.MINUS;
            break;
        case '*':
            type = Type.OP_LEVEL2;
            type2 = Op2Type.MULT;
            break;
        case '/':
            type = Type.OP_LEVEL2;
            type2 = Op2Type.DIV;
            break;
        case '%':
            type = Type.OP_LEVEL2;
            type2 = Op2Type.MOD;
            break;
        default:
            throw new CalculatorException("Incorrect symbol of operation: " + c);
        }
    }
}
