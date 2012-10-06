/*
 * Dmitriy Belyakov
 * 196 group
 *
 * The Java programming language, MIPT
 */
package ru.fizteh.fivt.students.dmitriyBelyakov.calculator.brain;

import java.util.Stack;
import java.lang.RuntimeException;
import java.math.BigInteger;

/**
 * Class to make a calculations
 *
 * @version 1.0 23 September 2012
 * @author Dmitriy Belyakov
 */
public class Brain {
	/**
	 * Calculate single operation
	 * @param operation Character of operation
	 * @param first First operand
	 * @param second Second operand
	 */
	private static BigInteger calculateOperation(char operation, BigInteger first, BigInteger second) throws RuntimeException {
		BigInteger tmp;
		switch(operation) {
			case '+':
				return first.add(second);
			case '-':
				return first.subtract(second);
			case '*':
				return first.multiply(second);
			case '/':
				return first.divide(second);
			default:
				throw new RuntimeException("Unknown operation.");
		}
	}

	/**
	 * Calculate expression in the string.
	 * @param exp Expression
	 */
	public static BigInteger calculate(String exp) throws RuntimeException {
		Stack<BigInteger> interpreter = new Stack<BigInteger>();
		Stack<Character> translator = new Stack<Character>();
		int firstOperandPosition = 0;
		boolean	operandIsEmpty = true;
		int i = 0;
		while(true) {
			char currentChar = '0';
			if(i < exp.length()) {
				currentChar = exp.charAt(i);
			}
			if((i < exp.length()) && Character.isDigit(currentChar)) {
				if(operandIsEmpty) {
					firstOperandPosition = i;
					operandIsEmpty = false;
				}
				++i;
			}
			else if(i >= exp.length()) {
				if(!operandIsEmpty) {
					operandIsEmpty = true;
					try {
						interpreter.push(new BigInteger(exp.substring(firstOperandPosition, i)));
					} catch(Exception e) {
						throw new RuntimeException("Some operand is very large.");
					}
				}
				if(translator.empty()) {
					break;
				} else if(translator.peek() == '(') {
					throw new RuntimeException("Incorrect count of '(' and ')'.");
				} else {
					char operation = translator.pop();
					BigInteger second = interpreter.pop();
					BigInteger first = interpreter.pop();
					BigInteger result = calculateOperation(operation, first, second);
					interpreter.push(result);
				}
			} else if(currentChar == '(') {
				if(!operandIsEmpty) {
					operandIsEmpty = true;
					try {
						interpreter.push(new BigInteger(exp.substring(firstOperandPosition, i)));
					} catch(Exception e) {
						throw new RuntimeException("Some operand is very large.");
					}
				}
				translator.push(currentChar);
				++i;
			} else if((currentChar == '+') || (currentChar == '-')) {
				if(!operandIsEmpty) {
					operandIsEmpty = true;
					try {
						interpreter.push(new BigInteger(exp.substring(firstOperandPosition, i)));
					} catch(Exception e) {
						throw new RuntimeException("Some operand is very large.");
					}
				}
				if(translator.empty() || (translator.peek() == '(')) {
					translator.push(currentChar);
					++i;
				} else if((translator.peek() == '+') || (translator.peek() == '-')) {
					char operation = translator.pop();
					BigInteger second = interpreter.pop();
					BigInteger first = interpreter.pop();
					BigInteger result = calculateOperation(operation, first, second);
					interpreter.push(result);
					translator.push(currentChar);
					++i;
				} else {
					char operation = translator.pop();
					BigInteger second = interpreter.pop();
					BigInteger first = interpreter.pop();
					BigInteger result = calculateOperation(operation, first, second);
					interpreter.push(result);
				}
			} else if((currentChar == '*') || (currentChar == '/')) {
				if(!operandIsEmpty) {
					operandIsEmpty = true;
					try {
						interpreter.push(new BigInteger(exp.substring(firstOperandPosition, i)));
					} catch(Exception e) {
						throw new RuntimeException("Some operand is very large.");
					}
				}
				if(translator.empty() || (translator.peek() == '(')
						|| (translator.peek() == '+') || (translator.peek() == '-')) {
					translator.push(new Character(currentChar));
					++i;
				} else {
					char operation = translator.pop();
					BigInteger second = interpreter.pop();
					BigInteger first = interpreter.pop();
					BigInteger result = calculateOperation(operation, first, second);
					interpreter.push(result);
					translator.push(currentChar);
					++i;
				}
			} else if(currentChar == ')') {
				if(!operandIsEmpty) {
					operandIsEmpty = true;
					try {
						interpreter.push(new BigInteger(exp.substring(firstOperandPosition, i)));
					} catch(Exception e) {
						throw new RuntimeException("Some operand is very large.");
					}
				}
				if(translator.empty()) {
					throw new RuntimeException("Incorrect count of '(' and ')'.");
				} else if(translator.peek() == '(') {
					translator.pop();
					++i;
				} else {
					char operation = translator.pop();
					BigInteger second = interpreter.pop();
					BigInteger first = interpreter.pop();
					BigInteger result = calculateOperation(operation, first, second);
					interpreter.push(result);
				}
			} else {
				throw new RuntimeException("Unknown symbol.");
			} 
		}
		if(interpreter.size() != 1) {
			throw new RuntimeException("Incorrect expression.");
		}
		return interpreter.peek();
	}
}
