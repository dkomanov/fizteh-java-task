package ru.fizteh.fivt.students.almazNasibullin.chatGui;

import javax.swing.*;

/**
 * 15.12.12
 * @author almaz
 */

public class UtilsGui {

    public static void showErrorMessage(String error) {
        JFrame jf = new JFrame();
        JOptionPane optionPane = new JOptionPane(error, JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = optionPane.createDialog(jf, "Error");
        dialog.setVisible(true);
    }

    public static void showErrorMessageAndExit(String error) {
        showErrorMessage(error);
        System.exit(1);
    }
}
