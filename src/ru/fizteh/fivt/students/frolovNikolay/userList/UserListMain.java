package ru.fizteh.fivt.students.frolovNikolay.userList;

public class UserListMain {

    public static void main(String[] args) { 
        try {
            UserList mainFrame = new UserList();
        } catch (Throwable crush) {
            System.err.println(crush.getMessage());
            System.exit(1);
        }
    }
}
