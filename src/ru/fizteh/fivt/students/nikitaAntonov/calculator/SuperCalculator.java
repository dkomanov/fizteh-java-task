package ru.fizteh.fivt.students.nikitaAntonov.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 * Класс для подсчёта значения арифметического выражения
 * 
 * @author Антонов Никита
 */
public class SuperCalculator {

    private int result;
    private boolean isCalculated;
    private Lexer lexer;


    public SuperCalculator(String src) {
        isCalculated = false;
        lexer = new Lexer(src);
    }

    public static void main(String args[]) {
        try {
            if (args.length > 0) {
                runWithParams(args);
            } else {
                runInteractive();
            }
        } catch (IOException e) {
            System.err.println("Unknown IO error");
            System.exit(1);
        }
    }

    public static void runWithParams(String args[]) throws IOException {
        String str = concat(args);

        if (str.trim().isEmpty()) {
            runInteractive();
            return;
        }

        try {
            runCalculating(str);
        } catch (CalculatorException e) {
            System.exit(1);
        }
    }

    public static void runInteractive() throws IOException {
        System.out.println("Write arithmethic expression to calc or 'quit' to exit");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s = in.readLine();

        while (!s.equals("quit")) {

            if (s.trim().isEmpty()) {
                s = in.readLine();
                continue;
            }

            try {
                runCalculating(s);
            } catch (CalculatorException e) {
            }

            s = in.readLine();
        }
    }

    private static void runCalculating(String expr) throws CalculatorException {
        SuperCalculator calc = new SuperCalculator(expr);

        try {
            System.out.println(calc.getResult());
        } catch (ArithmeticException e) {
            System.out.println("Arithmetic error: " + e.getMessage());
            throw new CalculatorException(e.getMessage());
        } catch (CalculatorException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    private static String concat(String args[]) {
        StringBuilder result = new StringBuilder();

        for (String s : args) {
            result.append(s);
            result.append(" ");
        }

        return result.toString();
    }

    public int getResult() throws CalculatorException {
        if (isCalculated) {
            return result;
        }

        result = parseExpression(false);

        return result;
    }

    private int parseExpression(boolean inBrackets) throws CalculatorException {
        int res;

        res = parseAddend();

        while (lexer.lex.type == Lexem.Type.OP_LEVEL1) {
            BigInteger overflowCtrl = new BigInteger(Integer.toString(res));

            switch (lexer.lex.type1) {
            case MINUS:
                overflowCtrl = overflowCtrl.subtract(
                        new BigInteger(Integer.toString(parseAddend())));
                break;
            case PLUS:
                overflowCtrl = overflowCtrl.add(
                        new BigInteger(Integer.toString(parseAddend())));
                break;
            }

            if (overflowCtrl.compareTo(
                    new BigInteger(Integer.toString(Integer.MAX_VALUE))) > 0
                || overflowCtrl.compareTo(
                    new BigInteger(Integer.toString(Integer.MIN_VALUE))) < 0) {

                throw new CalculatorException("Overflow!");
            }

            res = overflowCtrl.intValue();
        }

        if ((lexer.lex.type == Lexem.Type.END 
                    && !inBrackets)
                ||  (lexer.lex.type == Lexem.Type.BRACKET_CLOSE
                    && inBrackets)) {
            return res;    
        } else {
            throw new CalculatorException("Syntax Error at position: " + lexer.pos);
        }
    }

    private int parseAddend() throws CalculatorException {
        int res;

        res = parseMultiplier();

        while (lexer.lex.type == Lexem.Type.OP_LEVEL2) {

            switch (lexer.lex.type2) {
            case MULT:
                BigInteger overflowCtrl = new BigInteger(Integer.toString(res));

                overflowCtrl = overflowCtrl.multiply(
                        new BigInteger(Integer.toString(parseMultiplier())));

                if (overflowCtrl.compareTo(
                        new BigInteger(Integer.toString(Integer.MAX_VALUE))) > 0
                    || overflowCtrl.compareTo(
                        new BigInteger(Integer.toString(Integer.MIN_VALUE))) < 0) {

                    throw new CalculatorException("Overflow!");
                }

                res = overflowCtrl.intValue();
                break;
            case DIV:
                res = res / parseMultiplier();
                break;
            case MOD:
                res = res % parseMultiplier();
                break;
            }
        }

        return res;
    }

    private int parseMultiplier() throws CalculatorException {
        int res;

        lexer.nextLexem();

        switch(lexer.lex.type) {
        case NUM:
            res = lexer.lex.number;
            break;
        case BRACKET_OPEN:
            res = parseExpression(true);
            break;
        default:
            if (lexer.pos < lexer.source.length()) {
                throw new CalculatorException("Syntax error at " + lexer.pos);
            } else {
                throw new CalculatorException("Syntax error at the end of file");
            }
        }

        lexer.nextLexem();
        return res;
    }
}
