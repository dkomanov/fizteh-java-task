//package ru.fizteh.fivt.students.MescherinIlya.Calculator;

//import java.io.*;
import java.util.*;


public class Calculator {
	
	private static boolean delim(char c) {
		return c == ' ';
	}
	
	private static boolean isOp(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
	}
	
	private static int priority(Integer op) {
		if (op < 0) {
			return 4;
		}
		return
				op == '+' || op == '-' ? 1 :
				op == '*' || op == '/' || op == '%' ? 2 :
				-1;
	}
	
	private static void executeOperation(Stack<Integer> numbers, Integer op) {
		if (op < 0) {
			Integer operand = numbers.pop();
			switch (-op) {
				case (int)'+': numbers.push(operand); break;
				case (int)'-': numbers.push(-operand); break;
			}
		}
		else {
			Integer right = numbers.pop();
			Integer left = numbers.pop();
			switch (op) {
				case (int)'+': numbers.push(left + right); break;
				case (int)'-': numbers.push(left - right); break;
				case (int)'*': numbers.push(left * right); break;
				case (int)'/': 
					if (right == 0) {
						System.err.println("Error: division by zero");
						System.exit(-1);
					}
					numbers.push(left / right); 
					break;
				case (int)'%': 
					if (right == 0) {
						System.err.println("Error: division by zero");
						System.exit(-1);
					}
					numbers.push(left % right); 
					break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.err.println("Using: Calculator <expression>");
			System.exit(-1);
		}
		
		StringBuilder builder = new StringBuilder();
		for (String s : args) {
			builder.append(s + ' ');
		}
		
		String str = builder.toString();
		
		boolean mayUnary = true;
		Stack<Integer> numbers = new Stack<Integer>();
		Stack<Integer> operations = new Stack<Integer>();
		
		for (int i = 0; i < str.length(); i++) {
			if (!delim(str.charAt(i))) {
				if (str.charAt(i) == '(') {
					operations.push((int)'(');
					mayUnary = true;
				}
				else if (str.charAt(i) == ')') {
					while (operations.peek() != '(') {
						executeOperation(numbers, operations.peek());
						operations.pop();
					}
					operations.pop();
					mayUnary = false;
				}
				else if (isOp(str.charAt(i))) {
					char c = str.charAt(i);
					int curOp = c;
					if (mayUnary && (curOp == '+' || curOp == '-')) 
						curOp = -curOp;
					while (!operations.empty() && priority(operations.peek()) >= priority(curOp)) {
						executeOperation(numbers, operations.pop());
					}
					operations.push(curOp);
					mayUnary = true;
				}
				else {
					String operand = "";
					while (i < str.length() && "0123456789".indexOf(str.charAt(i)) != -1) {
						operand += str.charAt(i++);
					}
					i--;
					numbers.push(new Integer(operand));
					mayUnary = false;
				}
			}
		}
		while (!operations.empty()) {
			executeOperation(numbers, operations.pop());
		}
		
		System.out.println(numbers.pop());
		
		
	}
	
	
	
	
}
