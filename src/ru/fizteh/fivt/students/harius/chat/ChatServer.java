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
		ChatServer server = new ChatServer();
		//server.processCommand("/listen 7777");
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
				reg.lock.lock();
				server.bind(new InetSocketAddress(port));
				reg.isBound.signal();
				reg.lock.unlock();
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
		} else if (command.equals("/list")) {
			for (SocketService client : clients) {
				String nick = client.getNick();
				if (nick == null) {
					nick = "<unnamed>";
				}
				console.message(client.getNick() + "@" + client.getName());
			}
			if (clients.isEmpty()) {
				console.message("<no clients connected>");
			}
		} else if (command.equals("/stop")) {
			for (SocketService client : clients) {
				client.goodbye();
				client.shutdown();
			}
			clients.clear();
		} else if (command.equals("/exit")) {
			processCommand("/stop");
			reg.shutdown();
			console.shutdown();
			System.exit(0);
		} else if (command.startsWith("/sendall")) {
			String message = command.substring(8).trim();
			for (SocketService client : clients) {
				client.send(MessageUtils.message("server", message));
			}
		} else {
			console.error("Wrong command: " + command);
		}
	}

	@Override
	public void processPacket(byte[] packet, SocketService from) {
		if (Utils.typeOf(packet) == MessageType.HELLO.getId()) {
			String nick = Utils.helloRepr(packet);// multiple hello packets?
			boolean unique = true;
			for (SocketService client : clients) {
				if (nick.equals(client.getNick())) {
					unique = false;
				}
			}
			if (unique) {
				console.warn("New user entered: " + nick);
				from.setNick(nick);
			} else {
				from.send(MessageUtils.error("Nickname already in use"));
				from.goodbye(); // strange behavior while double connect attempt?
				from.shutdown();
				clients.remove(from);
			}
		}
		else if (Utils.typeOf(packet) == MessageType.BYE.getId()) {
			console.warn("Disconnecting " + from.getNick());
			from.shutdown();
			clients.remove(from);
		} else if (Utils.typeOf(packet) == MessageType.MESSAGE.getId()) {
			for(SocketService client : clients) {
				if (client != from) { // nickname checking?
					client.send(packet);
				}
			}
		} else if (Utils.typeOf(packet) == MessageType.ERROR.getId()) {
			console.error("Error from " + from.getNick() + ": " + Utils.generalRepr(packet));
		}
	}

	@Override
	public void processRegistration(Socket user) {
		console.warn("User connected");
		SocketService service = new SocketService(this, user, console, null);
		clients.add(service);
		new Thread(service).start();
	}
}