/*
 * Author: Mysin Yurii
 * 
 * Group: 196
 *  
 */

package Calculator;

import java.io.IOException;

public class Calculator {
	
    	public static void main(String[] args) throws IOException {
		for (int j = 0; j < args.length; ++j) {
			String expression = args[j];
			Expression answer = new Expression(expression);
			char c = answer.correctSymbols();
			if (c != ' '){
				System.out.println("Incorrect input symbol: " + c);
				continue;
			}
			int res = 0;
			try{
				res = answer.result();
			}catch (IllegalArgumentException e)
			{
				System.out.println( e.getMessage() );
				continue;
			}catch (ArithmeticException e){
				System.out.println("Divide by zero");
				continue;
			}
			System.out.println(res);
		}
	}
}
