package ru.fizteh.fivt.students.babushkinOleg.chat.client;

import ru.fizteh.fivt.students.babushkinOleg.chat.MessageType;
import ru.fizteh.fivt.students.babushkinOleg.chat.Message;
import ru.fizteh.fivt.students.babushkinOleg.chat.MessageUtils;
import java.io.*;
import java.util.Vector;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	private int currentServer = -1;
	private String nickname;
	private Vector<Listener> listeners = new Vector<Listener>();
	private BufferedReader userInput;

	public static void main(String[] args) throws IOException {
		if (args.length != 1)
			throw new IOException("Wrong arguments number");
		new Client(args[0]).run();
	}

	private Client(String nick) {
		nickname = nick;
	}

	private void run() throws IOException {
		System.out.println("Hello, " + nickname + "!");
		String input = null;
		userInput = new BufferedReader(new InputStreamReader(System.in));
		if (userInput == null)
			System.out.println("couldn't create buffer reader");
		else
			do {
				input = userInput.readLine();
				if (!input.isEmpty() && input.charAt(0) == '/') {
					String[] inputCommand = null;
					inputCommand = input.substring(1).split(" ", 2);
					switch (inputCommand[0]) {
					case "connect":
						if (inputCommand.length != 2)
							throw new IOException(
									"Wrong arguments number for command \"connect\"");
						else if (listeners.contains(new Listener(
								inputCommand[1])))
							System.out.println("Already connected to "
									+ inputCommand[1]);
						else if (connect(inputCommand[1]))
							System.out.println("Connection to "
									+ inputCommand[1] + " successfully");
						else
							System.out.println("Connection to "
									+ inputCommand[1] + " fails");
						break;

					case "disconnect":
						if (inputCommand.length != 1)
							throw new IOException(
									"Wrong arguments number for command \"disconnect\"");
						else if (currentServer != -1)
							disconnect(listeners.get(currentServer));
						else
							System.out.println("You don't connect any server");
						break;

					case "whereami":
						if (currentServer == -1)
							System.out
									.println("You don't entered anyone server");
						else
							System.out.println("Current server : "
									+ listeners.get(currentServer).ip + ":"
									+ listeners.get(currentServer).port);
						break;

					case "list":
						System.out.println("Server list : ");
						for (int i = 0; i < listeners.size(); ++i)
							System.out.println('\t' + listeners.get(i).ip + ":"
									+ listeners.get(i).port);
						break;

					case "use":
						if (inputCommand.length != 2)
							throw new IOException(
									"Wrong arguments number for command \"use\"");
						else {
							String[] str = inputCommand[1].split(":", 2);
							int index = -1;
							for (int i = 0; i < listeners.size(); ++i)
								if (listeners.get(i).ip.equals(str[0])
										&& listeners.get(i).port == Integer
												.parseInt(str[1]))
									index = i;
							if (index == -1)
								System.out.println("You don't connect to "
										+ inputCommand[1]);
							else {
								currentServer = index;
								System.out.println("Use to " + inputCommand[1]
										+ "successfully");
							}
						}
					}
				} else {
					sendMessage(new Message(MessageType.MESSAGE, nickname,
							input));
				}
			} while (!input.equals("/exit"));

		System.out.print("Goodbye, " + nickname + '.');
	}

	public void sendMessage(Message message) {
		if (currentServer == -1)
			System.out.println("You don't connect to server\n");
		else if (!listeners.get(currentServer).send(message))
			System.out.println("Couldn't send message for "
					+ listeners.get(currentServer).ip);
	}

	public void disconnect(Listener ls) throws IOException {
		if (listeners.contains(ls)) {
			byte[] b = new byte[1];
			sendMessage(new Message(MessageType.BYE, nickname, ""));
			ls.closeSocket();
			currentServer = -1;
			listeners.remove(ls);
		}
	}

	public boolean connect(String ip) throws IOException {
		try {
			String[] str = ip.split(":", 2);
			Listener listener = new Listener(str[0], Integer.parseInt(str[1]));
			if (!listeners.contains(listener)) {
				Thread thread = new Thread(listener);
				listener.myThread = thread;
				currentServer = listeners.size();
				listeners.add(listener);
				thread.setDaemon(true);
				thread.start();
				sendMessage(new Message(MessageType.HELLO, nickname, ""));
			}
		} catch (IOException e) {
			System.out.println("Couldn't create connection");
			return false;
		}
		return true;
	}

	private class Listener implements Runnable {

		private String ip;
		private int port;
		private Socket socket;
		private BufferedReader socketReader;
		private BufferedWriter socketWriter;
		private Thread myThread;

		public synchronized void closeSocket() {
			try {
				if (socket != null && !socket.isClosed())
					socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close socket");
			}
		}

		Listener(String ip) {
			this.ip = ip;
		}

		Listener(String serverIp, int serverPort) throws IOException {
			socket = new Socket(InetAddress.getByName(serverIp), serverPort);
			ip = serverIp;
			port = serverPort;
			if (socket == null)
				throw new IOException("Couldn't create socket");
		}

		public boolean send(Message message) {
			try {
				socket.getOutputStream().write(message.toByte());
			} catch (IOException e) {
				System.out.print("Couldn't send message");
				closeSocket();
				return false;
			}
			return true;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Listener)
				return ip.equals(((Listener) obj).ip)
						&& this.port == ((Listener) obj).port;
			else
				return false;
		}

		@Override
		public void run() {
			Message message = null;
			while (!socket.isClosed()) {
				try {
					message = MessageUtils.getMessage(socket.getInputStream());
				} catch (Exception e) {
					closeSocket();
					break;
				}
				if (myThread.isInterrupted())
					break;
				if (message == null || message.getType() == MessageType.BYE)
					break;
				if (message.getType() == MessageType.ERROR) {
					System.out.println(message.getText());
					break;
				}
				if (message.getType() == MessageType.MESSAGE) {
					if (currentServer == listeners.indexOf(this))
						System.out.println("<" + message.getName() + "> "
								+ message.getText());
				} else
					break;

			}
			listeners.remove(this);
			System.out.println("Connection lost : " + ip + ":" + port);
			if (currentServer == listeners.indexOf(this))
				currentServer = -1;
		}
	}
}
