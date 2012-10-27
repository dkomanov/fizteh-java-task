/*
 * ChatClient.java
 * Oct 19, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat;

import java.io.*;
import java.util.*;
import java.net.*;

import ru.fizteh.fivt.chat.*;

public class ChatClient implements Operated {
	private ConsoleService console;
	private List<SocketService> servers = new ArrayList<SocketService>();
	private int current = -1;
	private String nickname;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Please provide your nickname");
		} else {
			ChatClient client = new ChatClient(args[0]);
			client.processCommand("/connect localhost:7777");
		}
	}

	public ChatClient(String nickname) {
		this.nickname = nickname;
		console = new ConsoleService(this);
		new Thread(console).start();
	}

	private void endSession(int id) {
		servers.get(id).shutdown();
		if (current == id) {
			current = -1;
		} else if (current > id) {
			--current;
		}
	}

	private String getServerString(int id) {
		return String.format("%c[%d] %s", id == current ? '>' : ' ', id, servers.get(id).getName());
	}

	@Override
	public void processCommand(String command) {
		if (command.startsWith("/connect")) {
			String arg = command.substring(8);
			if (arg.indexOf(':') == -1) {
				console.error("Please provide server in format \"host:port\"");
			} else {
				String host = arg.substring(0, arg.indexOf(':')).trim();
				String port = arg.substring(arg.indexOf(':') + 1).trim();
				try {
					Socket server = new Socket(host, Integer.parseInt(port));
					// >>> nickname, really?
					SocketService service = new SocketService(this, server, console, nickname);
					servers.add(service);
					service.send(MessageUtils.hello(nickname));
					new Thread(service).start();
					current = servers.size() - 1;
				} catch (NumberFormatException notNumber) {
					console.error(port + " is not a valid port");
				} catch (IOException wrongHost) {
					console.error("Wrong address: " + arg);
				}
			}
		} else if (command.startsWith("/use")) {
			String arg = command.substring(4).trim();
			try {
				int index = Integer.parseInt(arg);
				if (index < 0 || index >= servers.size()) {
					console.error("Wrong server id: " + index);
				} else {
					current = index;
				}
			} catch (NumberFormatException notNumber) {
				console.error("Please provide server id");
			}
		} else if (command.equals("/exit")) {
			while (!servers.isEmpty()) {
				servers.get(0).goodbye();
				endSession(0);
			}
			console.shutdown();
		} else if (command.equals("/list")) {
			if (servers.isEmpty()) {
				console.message("<no servers connected>");
			} else {
				for (int i = 0; i < servers.size(); ++i) {
					console.message(getServerString(i));
				}
			}
		} else if (command.equals("/whereami")) {
			if (current == -1) {
				console.message("<not connected to any server>");
			} else {
				console.message(getServerString(current));
			}
		} else if (command.equals("/disconnect")) {
			if (current == -1) {
				console.error("Not connected to any server");
			} else {
				servers.get(current).goodbye();
				endSession(current);
			}
		} else if (!command.startsWith("/")) {
			if (current == -1) {
				console.error("Not connected to any server");
			} else {
				servers.get(current).send(MessageUtils.message(nickname, command));
			}
		} else {
			console.error("Wrong command: " + command);
		}
	}

	@Override
	public void processPacket(byte[] packet, SocketService from) {
		try {
			if (Utils.typeOf(packet) == MessageType.MESSAGE.getId()) {
				if (current != -1 && from == servers.get(current)) {
					console.message(Utils.messageRepr(packet));
				}
			} else if (Utils.typeOf(packet) == MessageType.BYE.getId()) {
				int id = servers.indexOf(from);
				if (id == -1) {
					console.error("Goodbye from strange server");
				} else {
					console.warn("Server " + from.getName() + " disconnected");
					endSession(id);
				}
			} else if (Utils.typeOf(packet) == MessageType.ERROR.getId()) {
				console.error("Error from server " + from.getName() + ": " + Utils.generalRepr(packet));
			} else {
				throw new BadMessageException("Unexpected message header: " + Utils.typeOf(packet));
			}
		} catch (BadMessageException ex) {
			console.error(ex.getMessage());
			from.send(MessageUtils.error("Ill-formed message received. Be careful!"));
		}
	}

	@Override
	public void removeService(SocketService who) {
		servers.remove(who);
	}
}