package ru.fizteh.fivt.students.almazNasibullin.chat.client;

import ru.fizteh.fivt.students.almazNasibullin.chat.client.Client;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;

/**
 * 21.12.12
 * @author almaz
 */

public class ClientRun {
    private static Client client;

    public static void main(String[] args) {
        if (args.length != 1) {
            IOUtils.printErrorAndExit("Put your nick");
        }
        client = new Client(args[0]);

        for (;;) {
            if (client.isBufReady()) {
                client.handlerConsole();
            }
            if (client.isConnected()) {
                int num = client.getSelectedCount();
                if (num == 0) {
                    continue;
                }
                client.handlerServer();
            }
        }
    }
}
