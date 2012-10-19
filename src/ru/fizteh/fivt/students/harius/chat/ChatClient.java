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
			new ChatClient(args[0]);
		}
	}

	public ChatClient(String nickname) {
		this.nickname = nickname;
		console = new ConsoleService(this);
		new Thread(console).start();
	}

	private void endSession(int id) {
		servers.get(id).goodbye();
		servers.get(id).shutdown();
		servers.remove(id);
		if (current == id) {
			current = -1;
		} else if (current > id) {
			--current;
		}
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
			String arg = command.substring(4);
			try {
				int index = Integer.parseInt(arg);
				if (index <= 0 || index >= servers.size()) {
					console.error("Wrong server id: " + index);
				} else {
					current = index;
				}
			} catch (NumberFormatException notNumber) {
				console.error("Please provide server id");
			}
		} else if (command.equals("/exit")) {
			while (!servers.isEmpty()) {
				endSession(0);
			}
			console.shutdown();
		} else if (command.equals("/list")) {
			if (servers.isEmpty()) {
				console.message("<no servers connected>");
			} else {
				for (int i = 0; i < servers.size(); ++i) {
					console.message(String.format("[%d] %s", i, servers.get(i).getName()));
				}
			}
		} else if (command.equals("/whereami")) {
			if (current == -1) {
				console.message("Not connected to any server");
			} else {
				console.message(String.format("[%d] %s", current, servers.get(current).getName()));
			}
		} else if (command.equals("/disconnect")) {
			if (current == -1) {
				console.error("Not connected to any server");
			} else {
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
		if (Utils.typeOf(packet) == MessageType.MESSAGE.getId()) {
			console.message(Utils.messageRepr(packet));
		}
	}
}