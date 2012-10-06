/*
 * Dmitriy Belyakov
 * 196 group
 *
 * The Java programming language, MIPT
 */

import brain.Brain;
import java.math.BigInteger;

/**
 * Class for calculate expression from command line arguments.
 * 
 * @version	1.0 23 September 2012
 * @author	Dmitriy Belyakov
 */
public class Calculator {
	/**
	 * Convert args to string and make calculation.
	 * @param args Arguments of command line
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			System.err.println("Use: java Calculator <expression>");
			System.exit(1);
		}
		StringBuilder builder = new StringBuilder();
		for(String s: args) {
			builder.append(s);
			builder.append(' ');
		}
		String expression = builder.toString();
		expression = expression.replaceAll("(\\s)+", " ");
		for(int i = 1; i < expression.length() - 1; ++i) {
			if(expression.charAt(i) == ' ' && Character.isDigit(expression.charAt(i - 1))
					&& Character.isDigit(expression.charAt(i + 1))) {
				System.out.println("Expression: " + expression);
				System.err.println("Error: incorrect expression.");
				System.exit(-1);
			}
		}
		expression = expression.replaceAll("\\s", "");
		BigInteger result = null;
		System.out.println("Expression: " + expression);
		try {
			result = Brain.calculate(expression);
		} catch(Exception e) {
			if(e.getMessage() == null) {
				System.out.println("Error: unknown.");
			} else {
				System.err.println("Error: " + e.getMessage());
			}
			System.exit(-1);
		}
		System.out.println("Result: " + result);
	}
}