package ru.fizteh.fivt.students.frolovNikolay.chatGui;

public class ChatGuiMain {
    
    public static void main(String[] args) {
        try {
            ChatGui mainFrame = new ChatGui();
            mainFrame.handle();
        } catch (Throwable crush) {
            System.err.println(crush.getMessage());
            System.exit(1);
        }
    }

}
