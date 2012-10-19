/*
 * Operated.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

public interface Operated {
	public void processCommand(String command);
	public void processPacket(byte[] packet, SocketService from);
}