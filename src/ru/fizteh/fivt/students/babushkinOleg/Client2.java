
import java.io.*;
import java.util.Vector;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;

public class Client{
	private static int currentServer = -1;
	private static final int myPort = 45000;
	private static final int serverPort = 40401;
	private static String nickname;
	private static Vector<Listener> listeners = new Vector<Listener>();

    static BufferedReader userInput;
	
	public static void main(String[] args) throws IOException{
		if (args.length != 1)
			throw new IOException("Wrong arguments number");
		nickname = args[0];
		new Client().run();
	}
	
	public void run() throws IOException{
		System.out.println("Hello, " + nickname + "!");
		String input = null;
	    userInput = new BufferedReader(new InputStreamReader(System.in));
		do{
			input = userInput.readLine();
			if (!input.isEmpty() && input.charAt(0) == '/'){
				String[] inputCommand = null;
				inputCommand = input.substring(1).split(" ", 2);
				switch (inputCommand[0]){
				
					case "connect" :
						if (inputCommand.length != 2)
							throw new IOException("Wrong arguments number for command \"connect\"");
						else
							if (listeners.contains(new Listener(inputCommand[1])))
								System.out.println("Already connected to " + inputCommand[1]);
							else
								if (connect(inputCommand[1]))
									System.out.println("Connection to " + inputCommand[1] + " successfully");
								else
									System.out.println("Connection to " + inputCommand[1] + " fails");
						break;
						
					case "disconnect" :
						if (inputCommand.length != 1)
							throw new IOException("Wrong arguments number for command \"disconnect\"");
						else
							if (currentServer != -1)
								disconnect(listeners.get(currentServer));
							else
								System.out.println("You don't connect any server");
						break;	
						
					case "whereami" :
						if (currentServer == -1)
							System.out.println("You don't entered anyone server");
						else
							System.out.println("Current server : " + listeners.get(currentServer).ip + ":" + listeners.get(currentServer).port);
						break;
						
					case "list" :
						System.out.println("Server list : ");
						for (int i = 0; i < listeners.size(); ++i)
							System.out.println('\t' + listeners.get(i).ip + ":" + listeners.get(i).port);
						break;
						
					case "use" :
						if (inputCommand.length != 2)
							throw new IOException("Wrong arguments number for command \"use\"");
						else{
							String[] str = inputCommand[1].split(":", 2);
							int index = -1;
							for (int i = 0; i < listeners.size(); ++i)
								if (listeners.get(i).ip.equals(str[0]) && listeners.get(i).port == Integer.parseInt(str[1]))
									index = i;
							if (index == -1)
								System.out.println("You don't connect to " + inputCommand[1]);
							else{
								currentServer = index;
								System.out.println("Use to " + inputCommand[1] + "successfully");
							}
						}
						break;
				}
			}else{
				if (currentServer == -1)
					System.out.print("You don't connect to server\n");
				else{
					listeners.get(currentServer).send(m(input, 2));
				}
			}
		}while(!input.equals("/exit"));
		
		
		System.out.print("Goodbye, " + nickname + '.');
	}
	
	private String m(String line, int b){
		byte[] res = new byte[line.length() + 1], res1 = line.getBytes();
		res[0] = (byte)b;
		for (int i = 0; i < line.length(); ++i)
			res[i + 1] = res1[i];
		return new String(res);
	}
	
	private String cut(String line){
		if (line.length() > 0){
			byte[] b = new byte[line.length() - 1];
			for (int i = 0; i + 1 < line.length(); ++i)
				b[i] = (byte)line.charAt(i + 1);
			return new String(b); 
		}else
			return line;
	}
	
	public void sendMessage(String line){
		for (Listener ls: listeners){
			if (!ls.send(line))
				System.out.println("Couldn't send message for " + ls.ip);
		}
	}
	
	public void disconnect(Listener ls) throws IOException{
		if (listeners.contains(ls)){
			byte[] b = new byte[1];
			sendMessage(m("", 3));
			ls.closeSocket();
			currentServer = -1;
			listeners.remove(ls);
		}
	}
	
	public boolean connect(String ip) throws IOException{
		try{
			String[] str = ip.split(":", 2);
			Listener listener = new Listener(str[0], Integer.parseInt(str[1]));
		    if (!listeners.contains(listener)){
			    Thread thread = new Thread(listener);
			    listener.myThread = thread;
				currentServer = listeners.size();
			    listeners.add(listener);
			    thread.setDaemon(true);
			    thread.start();
		    }
		}catch (IOException e){
			System.out.println("Couldn't create connection");
			return false;
		}
		return true;
	}
	
	private class Listener implements Runnable{
		
		private String ip;
		private int port;
		private Socket socket;
		private BufferedReader socketReader;
		private BufferedWriter socketWriter;
		private Thread myThread;
		
		public synchronized void closeSocket(){
			try{
				if (socket != null && !socket.isClosed())
				socket.close();
			}catch (IOException ignored){}
		}
		
		Listener(String ip){
			this.ip = ip;
		}
		
		Listener(String serverIp, int serverPort) throws IOException{
			socket = new Socket(InetAddress.getByName(serverIp), serverPort);
			ip = serverIp;
			port = serverPort;
			if (socket == null)
				throw new IOException("Couldn't create socket");
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		    socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		    send(m(nickname, 1));
		}
		
		public boolean send(String line){
			try{
				socketWriter.write(line + "\n");
				socketWriter.flush();
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
				return ip.equals(((Listener)obj).ip) && this.port == ((Listener)obj).port;
			else
				return false;
		}
		
		@Override
		public void run(){
			while (!socket.isClosed()){
				String line = null;
				try{
					line = socketReader.readLine();
						
				}catch (IOException e){
					closeSocket();
					break;
				}	
				if (line == null || line.length() == 0 || line.getBytes()[0] == 3)
					break;
				if (line != null)
					System.out.println(cut(line));
			}
			System.out.println("Server " + ip + " disconnected");
			if (currentServer == listeners.indexOf(new Listener(ip)))
				currentServer = -1;
			try {
				disconnect(this);
			} catch (IOException ignored) {}
		}
	}
}
