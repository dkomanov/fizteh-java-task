/*
 * ChatServer.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.channels.*;
import ru.fizteh.fivt.chat.*;

class SocketWrapper {
	private ServerSocketChannel socket;

	public SocketWrapper() throws IOException {
		socket = ServerSocketChannel.open();
	}

	public void unbind() throws IOException {
		socket.close();
		socket = ServerSocketChannel.open();
	}

	public void bind(int port) throws IOException {
		socket.bind(new InetSocketAddress(port));
	}

	public boolean isBound() {
		return socket.socket().isBound();
	}

	public Socket accept() throws IOException {
		return socket.accept().socket();
	}

	public void close() throws IOException {
		socket.close();
	}
}

public class ChatServer implements Operated, Registrating {
	private RegistrationService reg;
	private ConsoleService console;
	private List<SocketService> clients = new ArrayList<SocketService>();
	private SocketWrapper server;
	private Thread regThread;

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		chatServer.start();
	}

	public ChatServer() {
		try {
			server = new SocketWrapper();
		} catch (IOException ioEx) {
			System.err.println("Problems with creating socket for server");
			System.exit(1);
		}
		console = new ConsoleService(this);
		reg = new RegistrationService(this, server, console);
	}

	public void start() {
		new Thread(console).start();
		regThread = new Thread(reg);
		regThread.start();
	}

	private void listen(int port) {
		console.log("entering block for listening");
		//synchronized (server) {
			console.log("entered block for listening");
			if (server.isBound()) {
				console.error("Already listening");
			} else {
				try {
					console.log("binding to port " + port);
					server.bind(port);
					//server.notify();
				} catch (IOException ioEx) {
					console.error("Error while binding server to port " + port);
				}
			}
		//}
		console.log("exited block for listening");
	}

	private int findByNickname(String nick) {
		for (int i = 0; i < clients.size(); ++i) {
			String next = clients.get(i).getNickname();
			if (next != null && next.equals(nick)) {
				return i;
			}
		}
		return -1;
	}

	private String getClientString(int id) {
		String result = clients.get(id).getNickname();
		return result != null ? result : "<unnamed>";
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
		} else if (command.equals("/stop")) {
			console.log("closing server");
			for(SocketService client : clients) {
				client.shutdown();
			}
			console.log("disconnected clients");
			try {
				console.log("interrupting regthread");
				regThread.interrupt();
				console.log("entering block for unbind");
				//synchronized (server) {
					server.unbind();
				//}
				console.log("processed block for unbind");
			} catch(IOException ioEx) {
				console.error("i/o error while unbinding server socket");
			}
		} else if (command.equals("/exit")) {
			processCommand("/stop");
			try {
				console.error("entering block for closing server");
				//synchronized (server) {
					server.close();
				//}
				console.error("processed block for closing server");
			} catch (IOException ioEx) {
				console.error("i/o error while cloing server socket");
			}
			console.log("server shutting down");
			reg.shutdown();
			System.exit(0);
		} else if (command.startsWith("/sendall")) {
			String message = command.substring(8).trim();
			for(SocketService client : clients) {
				client.send(MessageUtils.message("server", message));
			}
		}
	}

	@Override
	public void processPacket(byte[] packet, SocketService from) {
		console.log("processing packet from " + from.getNickname());
		if (Utils.typeOf(packet) == MessageType.HELLO.getId()) {
			String nickname = Utils.getNickname(packet);
			if (from.getNickname() != null) {
				from.send(MessageUtils.error("hello-packet was received already"));
			} else if (findByNickname(nickname) != -1) {
				from.send(MessageUtils.error("nickname is already used"));
				from.shutdown();
				clients.remove(from);
			} else {
				from.setNickname(nickname);
			}
		} else if (Utils.typeOf(packet) == MessageType.BYE.getId()) {
			from.shutdown();
			clients.remove(from);
		} else if (Utils.typeOf(packet) == MessageType.MESSAGE.getId()) {
			for(SocketService client : clients) {
				if (client != from) {
					client.send(packet);
				}
			}
		}
	}

	@Override
	public void processRegistration(Socket user) {
		SocketService service = new SocketService(this, user, console, null);
		clients.add(service);
		new Thread(service).start();
	}
}