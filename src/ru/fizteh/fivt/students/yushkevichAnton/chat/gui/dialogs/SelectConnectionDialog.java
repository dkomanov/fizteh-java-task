package ru.fizteh.fivt.students.yushkevichAnton.chat.gui.dialogs;

import ru.fizteh.fivt.students.yushkevichAnton.chat.client.Client;

import javax.swing.*;
import java.awt.event.*;

public class SelectConnectionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;

    private Client client;

    public SelectConnectionDialog(Client client) {
        this.client = client;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onOK();}
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onCancel();}
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
        setVisible(true);
    }

    private void onOK() {
        client.processCommand("/use " + textField1.getText());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}