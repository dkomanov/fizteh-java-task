package ru.fizteh.fivt.students.tolyapro.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;

class ServerConnection implements Runnable {
    private InputStream in = null;
    public boolean active;
    public boolean toBeDeleted;
    Socket server;
    ArrayList<Integer> toDelete;
    int number;

    public ServerConnection(Socket server, ArrayList<Integer> toDelete,
            int number) throws IOException {
        in = server.getInputStream();
        active = true;
        this.server = server;
        toBeDeleted = false;
        this.toDelete = toDelete;
        this.number = number;
    }

    void disable() {
        active = false;
    }

    boolean isActive() {
        return active;
    }

    void activate() {
        active = true;
    }

    boolean toBeDeleted() {
        return toBeDeleted;
    }

    public void run() {
        String msg;
        int type;
        try {
            while ((type = in.read()) != -1) {
                if (active) {
                    byte typeOf = (byte) type;
                    if (typeOf != 0)
                        System.out.println(typeOf);
                    if (typeOf == 2) {
                        // System.out.println("norm");
                        System.out.println(MessageUtils.get(in));
                    } else if (typeOf == 127) {
                        toBeDeleted = true;
                        disable();
                        throw new RuntimeException(
                                MessageUtils.getErrorMessage(in)); // caught
                                                                   // below
                    } else if (typeOf == 3) {
                        System.out.println("bye");
                        disable();
                        toBeDeleted = true;
                        return;
                    }
                }
            }
            // System.out.println("End of era");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
            // throw new RuntimeException(e);
            // kill me, my server!
            /*
             * try { PrintWriter out = new PrintWriter(server.getOutputStream(),
             * true); out.println(new String(MessageUtils
             * .error("i see your error and  i raise your mine"))); out.close();
             * // # TODO finally
             * 
             * } catch (IOException e1) { }
             */
            // return;
        }
    }
}
