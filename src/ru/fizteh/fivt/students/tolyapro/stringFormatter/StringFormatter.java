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
		// TODO Auto-generated method stub
		parser(result, format, 0, args);
	}

	public ArrayList<StringFormatterExtension> extensions = new ArrayList<StringFormatterExtension>();

	public void addToExtensions(StringFormatterExtension extension)
			throws FormatterException {
		if (extension == null) {
			throw new FormatterException("no extension");
		}
		try {
			extensions.add(extension);
		} catch (Exception e) {
			throw new FormatterException(e.getMessage());
		}
	}

	public void giveMeObject(StringBuilder result, String format,
			Object... args) throws FormatterException {
		Object object;
		int position = format.indexOf(':');
		if (position == -1) {
			position = format.length();
		}
		String pattern = format.substring(0, position);
		String[] tokens = pattern.split("\\.");
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
		int leftPosition = format.indexOf('{', currPosition);
		int rightPosition = format.indexOf('}', currPosition);

		if (leftPosition == -1 && rightPosition == -1) {
			result.append(format.substring(currPosition));
			return;
		}
		if (leftPosition == -1) {
			leftPosition = format.length();
		}
		if (rightPosition == -1) {
			rightPosition = format.length();
		}

		if (leftPosition <= rightPosition) {
			result.append(format.substring(currPosition, leftPosition));
			if (format.length() > (leftPosition + 1)
					&& format.charAt(leftPosition + 1) == '{') {
				result.append('{');
				parser(result, format, leftPosition + 1, args);
			} else if (rightPosition == format.length()) {
				throw new FormatterException("Brackets error");
			} else if (rightPosition == leftPosition + 1) {
				throw new FormatterException("Expression in brackets is empty");
			} else {
				giveMeObject(result,
						format.substring(leftPosition + 1, rightPosition), args);
				parser(result, format, rightPosition + 1, args);
			}

		} else {
			if (rightPosition + 1 < format.length()
					&& format.charAt(rightPosition + 1) == '}') {
				result.append(format.substring(currPosition, rightPosition + 1));
				parser(result, format, rightPosition + 2, args);
			} else {
				throw new FormatterException("Brackets Error");
			}
		}

	}

}
