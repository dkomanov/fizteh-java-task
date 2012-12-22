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
import ru.fizteh.fivt.students.tolyapro.chat.TextAreaWriter;

class ServerConnection implements Runnable {
    private InputStream in = null;
    public boolean active;
    public boolean toBeDeleted;
    Socket server;
    ArrayList<Integer> toDelete;
    int number;
    TextAreaWriter areaWriter;
    TextAreaWriter errorWriter;
    private String history;

    synchronized public String getHistory() {
        return history;
    }
    
    synchronized public void updateHistory(String string) {
        history += string;
        history += '\n';
    }
    
    public ServerConnection(Socket server, ArrayList<Integer> toDelete,
            int number, TextAreaWriter areaWriter, TextAreaWriter errorWriter)
            throws IOException {
        in = server.getInputStream();
        active = true;
        this.server = server;
        toBeDeleted = false;
        this.toDelete = toDelete;
        this.number = number;
        this.areaWriter = areaWriter;
        this.errorWriter = errorWriter;
        this.history = " ";
        System.out.println("init " + history);
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
        history = " ";
        System.out.println("run before while " + history);
        try {
            System.out.println("run before while " + history);
            while ((type = in.read()) != -1) {
                if (active) {
                    byte typeOf = (byte) type;
                    System.out.println(typeOf);
                    if (typeOf == 2) {
                        // System.out.println("norm");
                        System.out.println("in mes " + history);
                        String m = MessageUtils.get(in);
                        for (int i = 0; i < m.length(); ++i) {
                            areaWriter.append(m.charAt(i));

                        }
                        areaWriter.append('\n');
                        history += m;
                        history += '\n';
                        System.out.println("new history " + history);
                        areaWriter.moveCaret();
                    } else if (typeOf == 127) {

                        toBeDeleted = true;
                        disable();
                        String err = MessageUtils.getErrorMessage(in);
                        for (int i = 0; i < err.length(); ++i) {
                            errorWriter.append(err.charAt(i));
                        }
                        errorWriter.append('\n');
                    } else if (typeOf == 3) {
                        String m = "bye";
                        for (int i = 0; i < m.length(); ++i) {
                            areaWriter.append(m.charAt(i));
                        }
                        areaWriter.append('\n');
                        areaWriter.moveCaret();
                        disable();
                        toBeDeleted = true;
                        return;
                    } else {
                        if (typeOf != 0) {
                            System.exit(1);
                        }
                    }
                }
            }
            // System.out.println("End of era");
        } catch (Exception e) {
            String m = "Error: " + e.getMessage();
            for (int i = 0; i < m.length(); ++i) {
                try {
                    errorWriter.append(m.charAt(i));
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }
            }
            try {
                errorWriter.append('\n');
                errorWriter.moveCaret();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }
            System.exit(1);
        }
    }
}
