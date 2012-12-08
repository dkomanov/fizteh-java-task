package ru.fizteh.fivt.students.frolovNikolay.chat.server;

public class ServerMain {

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
        } catch (Throwable crush) {
            System.out.println(crush.getMessage());
            System.exit(1);
        }
    }
}