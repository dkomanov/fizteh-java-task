package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import java.util.Scanner;

public class ChatServer {
    private final static String serverName = "<server>";

    public static void main(String[] args) {
        String command;
        Scanner scanner = new Scanner(System.in);
        Listener listener = null;
        while((command = scanner.nextLine()) != null) {
            if(command.equals("/listen")) {
                listener = new Listener(6666);
                try {
                    listener.start();
                } catch(Exception e) {
                    System.exit(1);
                }
            } else if(command.equals("/stop")) {
                // TODO stop
            } else if(command.equals("/list")) {
                // TODO list
            } else if(command.equals("/send")) {
                // TODO send
            } else if(command.equals("/sendall")) {
                // TODO sendall
            } else if(command.equals("/kill")) {
                // TODO kill
            } else if(command.equals("/exit")) {
                listener.stop();
                System.exit(0);
            }
        }
    }
}