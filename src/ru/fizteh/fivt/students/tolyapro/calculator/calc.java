import java.util.*;
import java.math.*;

public class calc {

	public static boolean isNumber(String s) {
		if (s.length() != 0)
			if (Character.isDigit(s.charAt(0)))
				return true;
		return false;

	}

	public static boolean check_brackets(String s) {
		int depth = 0;
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) == '(')
				depth++;
			else if (s.charAt(i) == ')')
				depth--;
			if (depth < 0)
				return false;
		}
		return depth == 0;
	}

	public static void main(String[] args) throws Exception {
		String expr = new String();
		StringBuilder builder = new StringBuilder();
		boolean is_prev_num = false;
		for (String s : args) {
			if (is_prev_num) {
				if ((s.charAt(0) >= '0') && (s.charAt(0) <= '9')) {
					System.err.println("Error: Incorrect Input");
					System.exit(1);
				}
			}
			builder.append(s);
			if ((s.charAt(s.length() - 1) >= '0')
					&& (s.charAt(s.length() - 1) <= '9')) {
				is_prev_num = true;
			} else {
				is_prev_num = false;
			}

		}
		expr = builder.toString();
		expr.replaceAll("\"", "");
		expr.replaceAll(" ", "");
		if (!(check_brackets(expr))) {
			System.err.println("Incorrect input: Brackets error");
			System.exit(1);
		}
		if (expr.isEmpty()) {
			System.err.println("Incorrect input: Empty string");
			System.exit(1);
		}
		reverse_polish_notation converter = new reverse_polish_notation();
		String output = converter.to_polish(expr);
		String[] tokens = output.split(" ");
		Stack<String> polish_stack = new Stack<String>();
		for (int i = 0; i < tokens.length; ++i) {
			if (isNumber(tokens[i])) {
				polish_stack.push(tokens[i]);
			} else {
				try {
					if (polish_stack.empty()) {
						throw new Exception("Incorrect Input");
					}
					int a = new Integer(polish_stack.peek());
					polish_stack.pop();
					if (polish_stack.empty()) {
						throw new Exception("Incorrect Input");
					}
					int b = new Integer(polish_stack.peek());
					polish_stack.pop();
					if (tokens[i].charAt(0) == '+') {
						String sum_str = new String(Integer.toString(a + b));
						String a_str = new String(Integer.toString(a));
						String b_str = new String(Integer.toString(b));
						BigInteger sum = new BigInteger(sum_str);
						BigInteger a_big = new BigInteger(a_str);
						BigInteger b_big = new BigInteger(b_str);
						if (sum.equals(a_big.add(b_big)))
							polish_stack.push(Integer.toString(a + b));
						else
							throw new Exception("Overflow!");
					} else if (tokens[i].charAt(0) == '-') {
						String sub_str = new String(Integer.toString(b - a));
						String a_str = new String(Integer.toString(a));
						String b_str = new String(Integer.toString(b));
						BigInteger sub = new BigInteger(sub_str);
						BigInteger a_big = new BigInteger(a_str);
						BigInteger b_big = new BigInteger(b_str);
						if (sub.equals(b_big.subtract(a_big)))
							polish_stack.push(Integer.toString(b - a));
						else
							throw new Exception("Overflow!");
					} else if (tokens[i].charAt(0) == '*') {
						String mult_str = new String(Integer.toString(a * b));
						String a_str = new String(Integer.toString(a));
						String b_str = new String(Integer.toString(b));
						BigInteger mult = new BigInteger(mult_str);
						BigInteger a_big = new BigInteger(a_str);
						BigInteger b_big = new BigInteger(b_str);
						if (mult.equals(a_big.multiply(b_big)))
							polish_stack.push(Integer.toString(a * b));
						else
							throw new Exception("Overflow!");
					} else if (tokens[i].charAt(0) == '/') {
						polish_stack.push(Integer.toString(b / a));
					}
				} catch (Exception e) {
					System.err.print("Error: ");
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		}
		if (!polish_stack.empty()) {
			System.out.println(polish_stack.peek());
		} else {
			System.err.print("Error");
			System.exit(1);
		}
	}
}
