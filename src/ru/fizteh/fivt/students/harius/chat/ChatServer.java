/*
 * ChatServer.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.fizteh.fivt.chat.*;

public class ChatServer implements Operated, Registrating {
	private RegistrationService reg;
	private ConsoleService console;
	private List<SocketService> clients = new CopyOnWriteArrayList<SocketService>();
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

	private void rebindServer() throws IOException {
		server.close();
		server = new ServerSocket();
		reg.setSocket(server);
	}

	private void listen(int port) {
		try {
			console.warn("Preparing to switch port");
			reg.lock.lock();
			server.bind(new InetSocketAddress(port));
			reg.isBound.signal();
			console.warn("Port switched to " + port);
		} catch (IOException ioEx) {
			console.error("Error while binding server to port " + port + ": " + ioEx.getMessage());
		} finally {
			reg.lock.unlock();
		}
	}

	private void endSession(SocketService client) {
		client.goodbye(); 
		client.shutdown();
	}

	private void endAllSessions() {
		for (SocketService client : clients) {
			endSession(client);
		}
	}

	private String getClientString(SocketService client) {
		String result = client.getNick();
		if (result == null) {
			result = "<unnamed>";
		}
		result += "@" + client.getName();
		return result;
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
				String show = getClientString(client);
				console.message(show);
			}
			if (clients.isEmpty()) {
				console.message("<no clients connected>");
			}
		} else if (command.equals("/stop")) {
			endAllSessions();
			console.warn("Preparing to stop server");
			try {
				rebindServer();
				console.warn("Server stopped");
			} catch (IOException ioEx) {
				console.error("i/o error while closing server " + ioEx.getMessage());
			}
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
		} else if (command.startsWith("/send")) {
			int delim = command.indexOf(" ", 6);
			if (delim == -1) {
				console.error("Please provide nickname and message");
			} else {
				String nickname = command.substring(5, delim).trim();
				String message = command.substring(delim + 1);
				int id = idByNick(nickname);
				if (id == -1) {
					console.error("No such user: " + nickname);
				} else {
					clients.get(id).send(MessageUtils.message("server", message));
				}
			}
		} else if (command.startsWith("/kill")) {
			String nickname = command.substring(5).trim();
			int id = idByNick(nickname);
			if (id == -1) {
				console.error("No such user: " + nickname);
			} else {
				clients.get(id).send(MessageUtils.message("server", "Disconnecting you from the server"));
				endSession(clients.get(id));
			}
		} else {
			console.error("Wrong command: " + command);
		}
	}

	private int idByNick(String nick) {
		for (int id = 0; id < clients.size(); ++id) {
			if (nick.equals(clients.get(id).getNick())) {
				return id;
			}
		}
		return -1;
	}

	@Override
	public void processPacket(byte[] packet, SocketService from) {
		try {
			if (Utils.typeOf(packet) == MessageType.HELLO.getId()) {
				String nick = Utils.helloRepr(packet); 
				if (idByNick(nick) == -1 && from.getNick() == null && nick.matches("[\\w\\d]+") && nick.length() < 23 && nick.length() > 2) {
					console.warn("New user entered: " + nick);
					from.setNick(nick);
				} else {
					console.warn("Unsuccessfull registration");
					from.send(MessageUtils.error("Nickname in use or it's wrong or you are logged in already"));
					if (from.getNick() == null) {
						endSession(from);
					}
				}
			}
			else if (Utils.typeOf(packet) == MessageType.BYE.getId()) {
				console.warn("Disconnecting " + from.getNick());
				from.shutdown();
			} else if (Utils.typeOf(packet) == MessageType.MESSAGE.getId()) {
				if (from.getNick() == null) {
					from.send(MessageUtils.error("Introduce yourself first!"));
				} else if (!Utils.dispatch(packet).get(0).equals(from.getNick())) {
					from.send(MessageUtils.error("Cheating cheater, it's not your nick!"));
				} else {
					for (SocketService client : clients) {
						if (client != from) { // nickname checking?
							client.send(packet);
						}
					}
				}
			} else if (Utils.typeOf(packet) == MessageType.ERROR.getId()) {
				console.error("Error from " + getClientString(from) + ": " + Utils.generalRepr(packet));
				from.shutdown();
			} else {
				throw new BadMessageException("Unexpected message header: " + Utils.typeOf(packet));
			}
		} catch (BadMessageException ex) {
			console.error(ex.getMessage());
			from.send(MessageUtils.error("Ill-formed message received. Be careful!"));
			from.shutdown();
		} catch (Exception ex) {
			console.error("Exception while processing message: " + ex.getMessage());
			from.shutdown();
		}
	}

	@Override
	public void processRegistration(Socket user) {
		console.warn("User connected");
		SocketService service = new SocketService(this, user, console, null);
		clients.add(service);
		new Thread(service).start();
	}

	@Override
	public void removeService(SocketService who) {
		clients.remove(who);
	}
}