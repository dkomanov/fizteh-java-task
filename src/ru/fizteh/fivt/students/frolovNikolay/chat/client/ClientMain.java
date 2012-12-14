package ru.fizteh.fivt.students.frolovNikolay.chat.client;

public class ClientMain {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ClientMain.java <NickName>");
            System.exit(1);
        } else {
            try {
                String nickName = args[0];
                Client client = new Client(nickName);
                client.run();
            } catch (Throwable crush) {
                System.err.println(crush.getMessage());
                System.exit(1);
            }
        }
    }
}