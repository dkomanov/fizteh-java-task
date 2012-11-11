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

	public final ReentrantLock lock = new ReentrantLock();
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
			while (server.isClosed() || !server.isBound()) {
				try {
					isBound.await();
				} catch (InterruptedException interrupted) {
					continue;
				}
			}
			try {
				Socket user = server.accept();
				managed.processRegistration(user);
			} catch (SocketException timeout) {
				// seems nothing to do here
			} catch (IOException ioEx) {
				System.err.println("i/o error: " + ioEx);
			} finally  {
				lock.unlock();
			}
		}
	}

	public void setSocket(ServerSocket newSocket) {
		server = newSocket;
	}

	public void shutdown() {
		running = false;
	}
}