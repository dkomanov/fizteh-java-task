public class MainClass {
	private static int operation(int x, int y, char op) {
		if (op == '+') {
			return x + y;
		} else if (op == '-') {
			return x - y;
		} else if (op == '*') {
			return x * y;
		} else if (op == '/') {
			if (y==0)
			{
				System.out.print("Division by zero");
				System.exit(0);
			}
			return x / y;
		}
		return 0;
	}

	private static boolean isOp(char symbol) {
		return (symbol == '+' || symbol == '*' || symbol == '-' || symbol == '/');
	}

	private static int calc(char[] expression, int pos, int end) {
		int i = pos;
		int res = 0;
		int j;
		int numb;
		int currNumber;
		char currentOp = '0';
		for (i = pos; i <= end; ++i) {
			if (expression[i] == '(') {
				j = i;
				numb = 1;
				while (numb != 0) {
					++j;
					if (expression[j] == ')') {
						--numb;
					}
					if (expression[j] == '(') {
						++numb;
					}
				}
				if (currentOp == '0') {
					res = calc(expression, i + 1, j - 1);
				} else {
					res = operation(res, calc(expression, i + 1, j - 1),
							currentOp);
				}
				i = j;
			} else if (Character.isDigit(expression[i])) {
				currNumber = 0;
				while (i <= end && Character.isDigit(expression[i])) {
					currNumber *= 10;
					currNumber += (expression[i] - '0');
					++i;
				}
				if (currentOp == '0') {
					res = currNumber;
				} else {
					res = operation(res, currNumber, currentOp);
				}
				--i;
			} else {
				currentOp = expression[i];
				if (currentOp == '+' || currentOp == '-') {
					numb = 0;
					j = i + 1;
					while ((numb != 0 || (expression[j] != '+' && expression[j] != '-'))
							&& j <= end) {
						++j;
						if (expression[j] == ')') {
							--numb;
						}
						if (expression[j] == '(') {
							++numb;
						}
					}
					res = operation(res, calc(expression, i + 1, j - 1),
							currentOp);
					i = j - 1;
				}
			}
		}
		// System.out.print(res);
		// System.out.print('\n');
		return res;
	}

	private static boolean check(char[] expression) {
		int i=0;
		int numb=0;
		if (expression[i] != '(' && !(Character.isDigit(expression[i]))) {
			return false;
		}
		for (; i < expression.length; ++i) {
			if (expression[i] == '(') {
				++numb;
			} else if (expression[i] == ')') {
				--numb;
				if (numb < 0) {
					return false;
				}
			}
			if (i > 0) {
				if ((expression[i - 1] == '(' && isOp(expression[i]))
						|| (Character.isDigit(expression[i - 1]) && expression[i] == '(')) {
					return false;
				}
				if ((isOp(expression[i - 1]) && expression[i] == ')')
						|| (expression[i - 1] == ')' && Character
								.isDigit(expression[i]))) {
					return false;
				}
			}
		}
		return (numb == 0);
	}

	public static void main(String[] args) {
		StringBuilder buildExpr = new StringBuilder();
		String expr = "";
		int spaces = 0;
		if (args.length == 0) {
			System.out.print("Incorrect input");
			System.exit(0);
		}
		for (int i = 0; i < args.length; ++i) {
			buildExpr.append(args[i]);
		}
		expr = buildExpr.toString();
		// expr =
		// " 4   +  1   0    * 7 +            8 * ( 2 + 3 ) +  2 * 2 + 2";
		char[] expressionFirst = expr.toCharArray();
		for (int i = 0; i < expressionFirst.length; ++i) {
			if (expressionFirst[i] == ' ') {
				++spaces;
			} else if (!(Character.isDigit(expressionFirst[i]))
					&& !(isOp(expressionFirst[i]))
					&& expressionFirst[i] != '(' && expressionFirst[i] != ')') {
				System.out.print("Incorrect input");
				System.exit(0);
			}
		}
		char[] expression = new char[expressionFirst.length - spaces];
		for (int i = 0, j = 0; i < expressionFirst.length; ++i) {
			if (expressionFirst[i] != ' ') {
				expression[j] = expressionFirst[i];
				++j;
			}
		}
		if (!(check(expression)))
		{
			System.out.print("Incorrect input");
			System.exit(0);
		}
		System.out.print(calc(expression, 0, expression.length - 1));
	}
}