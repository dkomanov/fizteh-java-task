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
		console.log("socketService started");
		while(running) {
			try {
				byte[] message = new byte[1024];
				console.log("waiting for a message...");
				int length = socket.getInputStream().read(message);
				managed.processPacket(message, this);
			} catch (IOException ioEx) {
				if (running) {
					console.error("i/o error: " + ioEx);
				}
			}
		}
	}

	public synchronized void send(byte[] message) {
		console.log("sending message...");
		try {
			socket.getOutputStream().write(message);
			console.log("message sent");
		} catch (IOException ioEx) {
			console.error("i/o problems while connecting to remote end");
		}
	}

	private void goodbye() {
		send(MessageUtils.bye());
		try {
			socket.close();
		} catch (IOException ioEx) {
			console.error("Problems while closing connection with server");
		}
	}

	public void shutdown() {
		goodbye();
		running = false;
	}

	public String getAddress() {
		return socket.getInetAddress().getHostAddress();
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}