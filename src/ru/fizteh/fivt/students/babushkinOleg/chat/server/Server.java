package ru.fizteh.fivt.students.babushkinOleg.chat.server;

import ru.fizteh.fivt.students.babushkinOleg.chat.MessageType;
import ru.fizteh.fivt.students.babushkinOleg.chat.Message;
import ru.fizteh.fivt.students.babushkinOleg.chat.MessageUtils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Vector;


public class Server {
	private ServerSocket socket;
	private Thread serverThread = null;
	private int serverPort;
	private Vector<Listener> listeners = new Vector<Listener>();
	
	class ServerListener implements Runnable{
	
		private ServerListener(int port) throws IOException {
			socket = new ServerSocket(port);
			serverPort = port;
		}
		
		@Override
		public void run(){
			serverThread = Thread.currentThread();
			while (true) {
				Socket newSocket;
				try{
					newSocket = socket.accept();
				}catch (IOException e){
					System.out.println("Couldn't accept socket");
					break;
				}
				if (serverThread.isInterrupted())
					break;
	        	if (newSocket != null){
	                    Listener listener;
						try {
							listener = new Listener(newSocket);
		                    listener.myThread = new Thread(listener);
		                    listeners.add(listener);
		                    listener.myThread.setDaemon(true);
		                    listener.myThread.start();
						} catch (IOException e) {
							System.out.print("Couldn't initialize read/write buffer");
							break;
						}
	        	}
	        }
			sendAll(new Message(MessageType.BYE, "server", ""), true);
			for (Listener ls : listeners){
				ls.myThread.interrupt();
				ls.closeSocket();
			}
		}
	}
	
	public void run() throws IOException{
		String input = null;
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		do{
			input = userInput.readLine();
			if (!input.isEmpty() && input.charAt(0) == '/'){
				String[] inputCommand = null;
				inputCommand = input.substring(1).split(" ", 3);
				switch (inputCommand[0]){
				
					case "listen" :
						if (inputCommand.length != 2)
							throw new IOException("Wrong arguments number for command \"listen\"");
						else{
							System.out.println("Start to listening " + inputCommand[1]);
							serverThread = new Thread(new ServerListener(Integer.parseInt(inputCommand[1])));
							serverThread.setDaemon(true);
							serverThread.start();
						}
						break;
						
					case "list" :
						System.out.println("Connected users : ");
						for (Listener ls : listeners){
							System.out.println("\t" + ls.nickname);
						}
						break;	
						
					case "send" :
						if (inputCommand.length != 3)
							throw new IOException("Wrong arguments number for command \"send\"");
						else
							if (listeners.contains(new Listener(inputCommand[1])))
									listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).send(new Message(MessageType.MESSAGE, "server", inputCommand[2]));
							else
								System.out.println("User " + inputCommand[1] + " not found");
						break;
						
					case "sendall" :
						if (inputCommand.length < 2)
							throw new IOException("Wrong arguments number for command \"sendall\"");
						else
							if (inputCommand.length == 2)
								sendAll(new Message(MessageType.MESSAGE, "server", inputCommand[2]), true);
							else
								sendAll(new Message(MessageType.MESSAGE, "server", inputCommand[1] + " " + inputCommand[2]), true);
						break;
						
					case "kill" :
						if (inputCommand.length != 2)
							throw new IOException("Wrong arguments number for command \"kill\"");
						else{
							if (listeners.contains(new Listener(inputCommand[1]))){
								listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).send(new Message(MessageType.BYE, "server", ""));
								listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).myThread.interrupt();
								listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).closeSocket();
								listeners.remove(new Listener(inputCommand[1]));
							}
						}
				}
			}
		}while(!input.equals("/exit") && !input.equals("/stop"));
		try{
			if (serverThread != null)
				serverThread.interrupt();
			new Socket("localhost", serverPort);
		} catch (IOException ignored){};
	}
	
	public static void main(String[] args) throws IOException{
		new Server().run();
	}
	
	private void sendAll(Message message, boolean isExceptSender){
		for (Listener ls : listeners){
			if (!isExceptSender || !ls.nickname.equals(message.getName()))
				ls.send(message);
		}
	}
	
	private class Listener implements Runnable{
		
		private String ip;
		private Socket socket;
		private Thread myThread;
		private String nickname = null; 
		
		Listener(Socket socket) throws IOException{
            this.socket = socket;
            ip = socket.getInetAddress().toString();
		}
		
		Listener(String nickname){
			this.nickname = nickname;
		}
		
		public synchronized void closeSocket(){
			try{
				if (socket != null && !socket.isClosed())
				socket.close();
			}catch (IOException e){
				System.out.print("Couldn't close socket");
			}
		}

		public boolean send(Message message){
			try{
				socket.getOutputStream().write(message.toByte());
				socket.getOutputStream().flush();
			}catch (IOException e){
				System.out.print("Couldn't send message");
				closeSocket();
				return false;
			}
			return true;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Listener)
				return nickname != null && ((Listener)obj).nickname != null && nickname.equals(((Listener)obj).nickname);
			else
				return false;
		}
		
		@Override
		public void run(){
			Message message = null;
			while (!socket.isClosed()){
				try{
					message = MessageUtils.getMessage(socket.getInputStream());
				}catch (Exception e){
					closeSocket();
					break;
				}
				if (myThread.isInterrupted())
					break;
				if (message == null || message.getType() == MessageType.BYE)
					break;
				if (message.getType() == MessageType.ERROR){
					System.out.println(message.getText());
					break;
				}
				if (nickname == null)
					if (message.getType() == MessageType.HELLO)
						nickname = message.getName();
					else
						send(new Message(MessageType.ERROR, "", "Wrong message type"));
				else
					if (message.getType() == MessageType.MESSAGE)
						sendAll(message, true);					
			}
			listeners.remove(this);
			System.out.println("user " + nickname + " disconnected");
			if (message != null)
				sendAll(new Message(MessageType.MESSAGE, "server", "user " + message.getName() + " has disconnected"), true);
		}
	}
}
