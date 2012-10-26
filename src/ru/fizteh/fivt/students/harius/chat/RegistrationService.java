/*
 * RegistrationService.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.*;
import ru.fizteh.fivt.chat.*;

public class RegistrationService implements Runnable {
	private Registrating managed;
	private ServerSocket server;
	private ConsoleService console;
	private boolean running = true;

	public final Lock lock = new ReentrantLock();
	public final Condition isBound = lock.newCondition();

	public RegistrationService(Registrating managed, ServerSocket server, ConsoleService console) {
		this.managed = managed;
		this.server = server;
		this.console = console;
	}

	@Override
	public void run() {
		while(running) {
			lock.lock();
			while (!server.isBound()) {
				try {
					isBound.await();
				} catch (InterruptedException interrupted) {

				}
			}
			try {
				Socket user = server.accept();
				managed.processRegistration(user);
			} catch (IOException ioEx) {
				System.err.println("i/o error: " + ioEx);
			}
			lock.unlock();
		}
	}

	public void shutdown() {
		running = false;
	}
}