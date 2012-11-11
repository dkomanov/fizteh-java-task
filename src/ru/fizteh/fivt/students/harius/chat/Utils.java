/*
 * Utils.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.util.*;
import java.nio.*;
import ru.fizteh.fivt.chat.*;

public abstract class Utils {
	public static byte typeOf(byte[] message) throws BadMessageException {
		try {
			return message[0];
		} catch (Exception ex) {
			throw new BadMessageException("Error while parsing message: " + ex.getMessage());
		}
	}

	public static List<String> dispatch(byte[] message) throws BadMessageException {
		try {
			List<String> result = new ArrayList<String>();
			ByteBuffer buffer = ByteBuffer.wrap(message);
			int head = buffer.get();
			int count = buffer.get();
			for (int m = 0; m < count; ++m) {
				int length = buffer.getInt();
				byte[] temp = new byte[length];
				buffer.get(temp);
				result.add(new String(temp));
			}
			return result;
		} catch (Exception ex) {
			throw new BadMessageException("Error while parsing message: " + ex.getMessage());
		}
	}

	public static String generalRepr(byte[] message) throws BadMessageException {
		List<String> parts = dispatch(message);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < parts.size(); ++i) {
			result.append(parts.get(i));
		}
		return result.toString();
	}

	public static String messageRepr(byte[] message) throws BadMessageException {
		List<String> parts = dispatch(message);
		StringBuilder result = new StringBuilder();
		if (parts.isEmpty()) {
			throw new BadMessageException("Empty message-packet");
		}
		result.append("[" + parts.get(0) + "]: ");
		for (int i = 1; i < parts.size(); ++i) {
			result.append(parts.get(i));
		}
		return result.toString();
	}

	public static String helloRepr(byte[] message) throws BadMessageException {
		List<String> parts = dispatch(message);
		return parts.get(0);
	}
}

class BadMessageException extends Exception {
	public BadMessageException(String message) {
		super(message);
	}
}