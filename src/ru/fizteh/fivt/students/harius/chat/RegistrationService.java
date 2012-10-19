/*
 * RegistrationService.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.net.*;
import ru.fizteh.fivt.chat.*;

public class RegistrationService implements Runnable {
	private Registrating managed;
	private ServerSocket server;
	private ConsoleService console;
	private boolean running = true;

	public RegistrationService(Registrating managed, ServerSocket server, ConsoleService console) {
		this.managed = managed;
		this.server = server;
		this.console = console;
	}

	@Override
	public void run() {
		while(running) {
			if (!server.isBound()) {
				continue;
			}
			try {
				Socket user = server.accept();
				managed.processRegistration(user);
			} catch (IOException ioEx) {
				System.err.println("i/o error: " + ioEx);
			}
		}
	}

	public void shutdown() {
		running = false;
	}
}