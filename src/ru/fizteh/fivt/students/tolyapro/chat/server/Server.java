package ru.fizteh.fivt.students.tolyapro.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;
import ru.fizteh.fivt.students.tolyapro.chat.User;

/**
 * @author tolyapro
 * 
 * @version 0.1
 * 
 */

public class Server {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String string = scanner.nextLine();
        Processor processor = null;
        int port = -1;
        while (string != null) {
            String[] tokens = string.split("\\s");
            for (int i = 0; i < tokens.length; ++i) {
                if (tokens[i].equals("/listen")) {
                    if ((i + 1) < tokens.length) {
                        try {
                            port = Integer.parseInt(tokens[i + 1]);
                            if (port < 1 || port > 30000) {
                                throw new Exception();
                            }
                            processor = new Processor(port);
                            processor.start();
                            port = -1;
                        } catch (Exception e) {
                            System.err.println("Bad listen");
                            break;
                        }
                    } else {
                        System.err.println("Bad listen");
                        break;
                    }
                } else if (tokens[i].equals("/kill") && i != tokens.length - 1
                        && tokens.length == 2) {
                    try {
                        processor.kill(tokens[i + 1]);
                    } catch (Exception e) {
                        System.err.println("Can't kill" + tokens[i + 1]);
                        break;
                    }
                    break;
                } else if (tokens[i].equals("/list")) {
                    Set<String> users = processor.getUsers();
                    for (String s : users) {
                        System.out.println(s);
                    }
                    break;
                } else if (tokens[i].equals("/stop")) {
                    processor.stop();
                    break;
                } else if (tokens[i].equals("/sendall") && tokens.length > 1) {
                    String message = string.substring(tokens[0].length());
                    processor.sendToAll(
                            new String(MessageUtils
                                    .message("<server>", message)), null);
                    break;
                } else if (tokens[i].equals("/send") && tokens.length > 2) {
                    String message = string.substring(tokens[0].length()
                            + tokens[1].length() + 1);
                    if (!processor.sendTo(
                            new String(MessageUtils
                                    .message("<server>", message)),
                            tokens[i + 1])) {
                        System.err.println("No such user " + tokens[1]);
                    }
                    break;
                } else {
                }
            }
            string = scanner.nextLine();
        }
    }
}
