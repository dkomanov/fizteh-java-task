import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Vector;

public class Server {
	private ServerSocket socket;
	private Thread serverThread;
	private int port;
	private Vector<Listener> listeners = new Vector<Listener>();
	
	private Server() throws IOException{
	}
	
	class ServerListener implements Runnable{
	
		public ServerListener(int p) throws IOException {
			socket = new ServerSocket(p);
			port = p;
		}
		@Override
		public void run(){
			serverThread = Thread.currentThread();
			Socket s;
			while (true) {
				try{
					s = socket.accept();
				}catch (IOException e){
					break;
				}
				if (serverThread.isInterrupted())
					break;
	        	if (s != null){
	                try {
	                    Listener listener = new Listener();
	                    listener.socket = s;
	                    listener.ip = s.getInetAddress().toString();
	                    listener.socketReader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
	                    listener.socketWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
	                    Thread thread = new Thread(listener);
	                    listener.myThread = thread;
	                    listeners.add(listener);
	                    thread.setDaemon(true);
	                    thread.start();
	                }catch (IOException ignored) {}
	        	}
	        }
			sendAll("server shutdown", "server", 2);
			for (Listener ls : listeners){
				ls.myThread.interrupt();
				try {
					ls.socket.close();
				} catch (IOException ignored) {}
				ls.socketReader = null;
				ls.socketWriter = null;
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
							Thread thread = new Thread(new ServerListener(Integer.parseInt(inputCommand[1])));
							thread.setDaemon(true);
							thread.start();
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
									listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).send(m("<server> " + inputCommand[2], 2));
							else
								System.out.println("User " + inputCommand[1] + " not found");
						break;
						
					case "sendall" :
						if (inputCommand.length < 2)
							throw new IOException("Wrong arguments number for command \"sendall\"");
						else
							if (inputCommand.length == 2)
								sendAll(inputCommand[1], "server", 2);
							else
								sendAll(inputCommand[1] + " " + inputCommand[2], "server", 2);
						break;
						
					case "kill" :
						if (inputCommand.length != 2)
							throw new IOException("Wrong arguments number for command \"kill\"");
						else{
							if (listeners.contains(new Listener(inputCommand[1]))){
								listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).send(m("<server> You have been disconnected", 2));
								listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).myThread.interrupt();
								listeners.get(listeners.indexOf(new Listener(inputCommand[1]))).closeSocket();
								listeners.remove(new Listener(inputCommand[1]));
							}
						}
				}
			}
		}while(!input.equals("/exit") && !input.equals("/stop"));
		try{
			serverThread.interrupt();
			new Socket("localhost", port);
		} catch (IOException ignored){};
	}
	
	public static void main(String[] args) throws IOException{
		new Server().run();
	}
	
	private void sendAll(String line, String nickname, int b){
		for (Listener ls : listeners){
			if (!ls.nickname.equals(nickname))
				ls.send(m("<" + nickname + "> : " + line, (byte)b));
		}
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
	
	private class Listener implements Runnable{
		
		private String ip;
		private Socket socket;
		private BufferedReader socketReader;
		private BufferedWriter socketWriter;
		private Thread myThread;
		private String nickname = null; 
		
		public synchronized void closeSocket(){
			try{
				if (socket != null && !socket.isClosed())
				socket.close();
			}catch (IOException ignored){}
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
		
		Listener(String nickname){
			this.nickname = nickname;
		}

		Listener(){}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Listener)
				return nickname.equals(((Listener)obj).nickname);
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
				if (myThread.isInterrupted())
					break;
				if (line != null){
					if (line.getBytes()[0] == 1){
						nickname = cut(line);
						for (Listener ls : listeners){
							if (!ls.nickname.equals(nickname))
								ls.send(m("<server> : user " + nickname + " connected", 2));
							else
								ls.send(m("You are welcome to <" + ip + "," + port + ">!", 2)); 
						}
					}
					else
						if (line.getBytes()[0] == 3)
							break;
						else
							sendAll(cut(line), nickname, 2);
					System.out.println("<" + nickname + "> " + line);
				}
			}
			listeners.remove(new Listener(nickname));
			System.out.println("user " + nickname + " disconnected");
			sendAll("user " + nickname + " disconnected", "server", 2);
		}
	}
}
