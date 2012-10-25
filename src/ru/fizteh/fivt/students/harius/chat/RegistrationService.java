/*
 * RegistrationService.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import ru.fizteh.fivt.chat.*;

public class RegistrationService implements Runnable {
	private Registrating managed;
	private SocketWrapper server;
	private ConsoleService console;
	private boolean running = true;

	public RegistrationService(Registrating managed, SocketWrapper server, ConsoleService console) {
		this.managed = managed;
		this.server = server;
		this.console = console;
	}

	@Override
	public void run() {
		while(running) {
			synchronized (server) {
				while (!server.isBound()) {
					try {
						console.log("waiting while server is not bound");
						server.wait();
						console.log("registrator invoked");
					} catch(InterruptedException interrupted) {
						console.log("interrupted wait for binding");
						break;
					}
				}

				if(!running) {
					break;
				}

				if (!server.isBound()) {
					continue;
				}

				try {
					console.log("ready to accept");
					Socket user = server.accept();
					console.log("accepted user");
					managed.processRegistration(user);
				} catch (ClosedChannelException timeout) {
					console.log("accept interrupted");
					try {
						server.wait();
					} catch (InterruptedException ex) {
						console.log("wait for server interrupted");
						continue;
					}
				} catch (IOException ioEx) {
					console.error("i/o error: " + ioEx);
				}
				console.log("accepted");
			}
			/*try {
				Thread.sleep(1000);
			} catch(InterruptedException interrupted) {
				console.error("internal error: thread interrupted");
			}*/
		}
		console.log("reg service shutting down");
	}

	public void shutdown() {
		running = false;
	}
}