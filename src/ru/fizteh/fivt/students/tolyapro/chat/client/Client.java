package ru.fizteh.fivt.students.tolyapro.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JTextArea;

import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;
import ru.fizteh.fivt.students.tolyapro.chat.TextAreaWriter;

public class Client {
    private String nickname;
    private ChatClient client;
    
    Client(String nick) {
        nickname = nick;
    }
    
    public void connect(String host, String port) throws Exception {
        client.connect(host, port);        
    }
    
    public void use(String name) {
        String[] arg = name.split(":");
        client.use(arg[0], arg[1]);
    }
    
    public void disconnect() {
        client.disconnectFromActive();
    }
    
    public void sendMessage(String string) throws IOException {
        client.sendMessageFromConsole(new String(MessageUtils
                .message(nickname, string)));
    }

    public void parse(JTextArea jTextArea) throws IOException {
        //byte[] expected = new byte[]{12, 1, 0, 0, 0, 3, 66, 97, 100};
        //System.out.println(expected);
        TextAreaWriter areaWriter = new TextAreaWriter(jTextArea);
        //client = new ChatClient(nickname, areaWriter, );
        Scanner scanner = new Scanner(System.in);
        String string = null;
        try {
            while ((string = scanner.nextLine()) != null) {
                String[] tokens = string.split("\\s");
                for (int i = 0; i < tokens.length; ++i) {
                    if (tokens[i].equals("/connect") && i != tokens.length - 1) {
                        String[] serverString = tokens[i + 1].split(":");
                        if (serverString.length != 2) {
                            break;
                        } else {
                            try {
                                client.connect(serverString[0], serverString[1]);
                            } catch (Exception e) {
                                System.err.println("Usage: /connect host:port");
                                break;
                            }
                        }
                        break;
                    } else if (tokens[i].equals("/disconnect")) {
                        client.disconnectFromActive();
                        break;
                    } else if (tokens[i].equals("/whereami")) {
                        System.out.println(client.whereAmI());
                        break;
                    } else if (tokens[i].equals("/list")) {
                        ArrayList<String> servs = client.list();
                        for (String s : servs) {
                            System.out.println(s);
                        }
                        break;
                    } else if (tokens[i].equals("/use")
                            && i != tokens.length - 1) {
                        String[] tmp = tokens[i + 1].split(":");
                        if (tmp.length != 2) {
                            System.err.println("Usage: /use host:port");
                        } else {
                            client.use(tmp[0], tmp[1]);
                        }
                        break;
                    } else if (tokens[i].equals("/exit")) {
                        client.exit();
                        System.exit(0);
                    } else if (string.isEmpty()) {

                    } else if (tokens[i].startsWith("/") && i == 0) {
                        System.err.println("Unknown command");
                        break;
                    } else {
                        System.out.println("In mes");
                        client.sendMessageFromConsole(new String(MessageUtils
                                .message(nickname, string)));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("in main client" + e.getMessage());
        }
    }
}
