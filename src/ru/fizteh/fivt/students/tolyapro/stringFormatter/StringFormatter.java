package ru.fizteh.fivt.students.tolyapro.stringFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {

	@Override
	public String format(String format, Object... args)
			throws FormatterException {
		StringBuilder result = new StringBuilder();
		format(result, format, args);
		return result.toString();
	}

	@Override
	public void format(StringBuilder result, String format, Object... args)
			throws FormatterException {
		parser(result, format, 0, args);
	}

	public ArrayList<StringFormatterExtension> extensions = new ArrayList<StringFormatterExtension>();

	public void addToExtensions(StringFormatterExtension extension)
			throws FormatterException {
		if (extension == null) {
			throw new FormatterException("Null extension");
		}
		try {
			extensions.add(extension);
		} catch (Exception e) {
			throw new FormatterException(e.getMessage());
		}
	}

	public void getObject(StringBuilder result, String format, Object... args)
			throws FormatterException {
		Object object;
		int position = format.indexOf(':');
		if (position == -1) {
			position = format.length();
		}
		String beforePattern = format.substring(0, position);
		String[] tokens = beforePattern.split("\\.");
		try {
			String tmp = tokens[0];
			int number = Integer.parseInt(tmp);
			if (number < 0) {
				throw new Exception();
			}
			object = args[number];
			for (int i = 1; i < tokens.length; ++i) {
				Class<?> clazz = object.getClass();
				String nameOfField = tokens[i];
				Field tempField = null;
				while (true) {
					try {
						tempField = clazz.getDeclaredField(nameOfField);
						tempField.setAccessible(true);
						object = tempField.get(object);
						break;
					} catch (Exception e) {
						tempField = null;
						clazz = clazz.getSuperclass();
						if (clazz == null) {
							throw new Exception();
						}
					}
				}

			}
			if (object == null) {
				return;
			}
		} catch (Exception e) {
			throw new FormatterException("Error with fields");
		}
		StringFormatterExtension thisExtension = null;
		for (StringFormatterExtension e : extensions) {
			if (e.supports(object.getClass())) {
				thisExtension = e;
				break;
			}
		}
		if (thisExtension != null) {
			if (position == format.length()) {
				result.append(object.toString());
				return;
			} else {
				thisExtension.format(result, object,
						format.substring(position + 1));
			}
		} else {
			throw new FormatterException("No such extension");
		}

	}

	public void parser(StringBuilder result, String format, int currPosition,
			Object... args) {
		int leftBracket = format.indexOf('{', currPosition);
		int rightBracket = format.indexOf('}', currPosition);

		if (leftBracket == -1 && rightBracket == -1) {
			result.append(format.substring(currPosition));
			return;
		}
		if (leftBracket == -1) {
			leftBracket = format.length();
		}
		if (rightBracket == -1) {
			rightBracket = format.length();
		}
		if (leftBracket <= rightBracket) {
			result.append(format.substring(currPosition, leftBracket));
			if (format.length() > (leftBracket + 1)
					&& format.charAt(leftBracket + 1) == '{') {
				result.append('{');
				if (leftBracket + 2 < format.length()) {
					parser(result, format, leftBracket + 2, args);
				}
			} else if (rightBracket == format.length()
					|| rightBracket == leftBracket + 1) {
				throw new FormatterException("Brackets error");
			} else {
				getObject(result,
						format.substring(leftBracket + 1, rightBracket), args);
				parser(result, format, rightBracket + 1, args);
			}

		} else {
			if (rightBracket + 1 < format.length()
					&& format.charAt(rightBracket + 1) == '}') {
				result.append(format.substring(currPosition, rightBracket + 1));
				parser(result, format, rightBracket + 2, args);
			} else {
				throw new FormatterException("Brackets Error");
			}
		}

	}

}
