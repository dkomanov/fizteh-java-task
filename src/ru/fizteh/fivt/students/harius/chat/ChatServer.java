/*
 * ChatServer.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.util.*;
import java.net.*;
import ru.fizteh.fivt.chat.*;

public class ChatServer implements Operated, Registrating {
	private RegistrationService reg;
	private ConsoleService console;
	private List<SocketService> clients = new ArrayList<SocketService>();
	private ServerSocket server;

	public static void main(String[] args) {
		new ChatServer();
	}

	public ChatServer() {
		try {
			server = new ServerSocket();
		} catch (IOException ioEx) {
			System.err.println("Problems with creating socket for server");
			System.exit(1);
		}
		console = new ConsoleService(this);
		new Thread(console).start();
		reg = new RegistrationService(this, server, console);
		new Thread(reg).start();
	}

	private void listen(int port) {
		if (server.isBound()) {
			console.error("Already listening port " + server.getLocalPort());
		} else {
			try {
				server.bind(new InetSocketAddress(port));
			} catch (IOException ioEx) {
				console.error("Error while binding server to port " + port);
			}
		}
	}

	@Override
	public void processCommand(String command) {
		if (command.startsWith("/listen")) {
			String arg = command.substring(7).trim();
			try {
				int port = Integer.parseInt(arg);
				listen(port);
			} catch (NumberFormatException notNumber) {
				console.error(arg + " is not a valid port number");
			}
		}
	}

	@Override
	public void processPacket(byte[] packet, SocketService from) {
		if (Utils.typeOf(packet) == MessageType.BYE.getId()) {
			from.goodbye();
			from.shutdown();
			clients.remove(from);
		} else if (Utils.typeOf(packet) == MessageType.MESSAGE.getId()) {
			for(SocketService client : clients) {
				client.send(packet);
			}
		}
	}

	@Override
	public void processRegistration(Socket user) {
		SocketService service = new SocketService(this, user, console, "server");
		clients.add(service);
		new Thread(service).start();
	}
}