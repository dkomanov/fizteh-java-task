/*
 * ConsoleService.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;

public class ConsoleService implements Runnable {
	private Operated managed;
	private boolean running = true;

	public ConsoleService(Operated managed) {
		this.managed = managed;
	}

	@Override
	public void run() {
		BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
		while (running) {
			try {
				String cmd = inp.readLine();
				managed.processCommand(cmd);
			} catch (IOException ioEx) {
				System.err.println("i/o error: " + ioEx);
			}
		}
	}

	public synchronized void message(String message) {
		System.out.println(message);
	}

	public synchronized void warn(String message) {
		System.err.println(message);
	}

	public synchronized void error(String error) {
		System.err.println(error);
	}

	public void shutdown() {
		running = false; // will it actually stop?
	}
}