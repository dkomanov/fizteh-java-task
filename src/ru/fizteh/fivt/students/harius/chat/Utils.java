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
	public static byte typeOf(byte[] message) {
		return message[0];
	}

	public static List<String> dispatch(byte[] message) {
		List<String> result = new ArrayList<String>();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		int head = buffer.get();
		int count = buffer.get();
		for(int m = 0; m < count; ++m) {
			int length = buffer.getInt();
			byte[] temp = new byte[length];
			buffer.get(temp);
			result.add(new String(temp));
		}
		return result;
	}

	public static String getNickname(byte[] message) {
		List<String> result = dispatch(message);
		if (result.size() != 1) {
			return null; //TODO: throw!
		} else {
			return result.get(0);
		}
	}

	public static String messageRepr(byte[] message) {
		List<String> parts = dispatch(message);
		StringBuilder result = new StringBuilder();
		result.append("[" + parts.get(0) + "] ");
		for (int i = 1; i < parts.size(); ++i) {
			result.append(parts.get(i));
		}
		return result.toString();
	}

	public static String errorRepr(byte[] message) {
		List<String> parts = dispatch(message);
		StringBuilder result = new StringBuilder();
		result.append("<error> ");
		for (int i = 0; i < parts.size(); ++i) {
			result.append(parts.get(i));
		}
		return result.toString();
	}
}