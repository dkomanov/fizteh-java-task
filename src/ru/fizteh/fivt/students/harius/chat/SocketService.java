/*
 * SocketService.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.net.*;
import ru.fizteh.fivt.chat.*;

public class SocketService implements Runnable {
	private Operated managed;
	private Socket socket;
	private ConsoleService console;
	private String nickname;
	private boolean running = true;

	public SocketService(Operated managed, Socket socket, ConsoleService console, String nickname) {
		this.managed = managed;
		this.socket = socket;
		this.console = console;
		this.nickname = nickname;
	}

	@Override
	public void run() {
		while(running) {
			try {
				byte[] message = new byte[1024];
				int length = socket.getInputStream().read(message);
				if (length == -1) {
					throw new IOException("Remote end hang unexpectedly, disconnecting");
				}
				managed.processPacket(message, this);
			} catch (IOException ioEx) {
				if (running) {
					console.error("i/o error: " + ioEx);
				}
				shutdown();
				break;
			}
		}
	}

	public void send(byte[] message) {
		try {
			socket.getOutputStream().write(message);
		} catch (IOException ioEx) {
			console.error("i/o problems while connecting to remote end");
			shutdown();
		}
	}

	public void goodbye() {
		send(MessageUtils.bye());
	}

	public void shutdown() {
		running = false;
		try {
			socket.close();
		} catch (IOException ioEx) {
			console.error("Problems while closing connection with server");
		}
		managed.removeService(this);
	}

	public String getName() {
		return socket.getInetAddress().getHostAddress();
	}

	public void setNick(String nick) {
		nickname = nick;
	}

	public String getNick() {
		return nickname;
	}
}