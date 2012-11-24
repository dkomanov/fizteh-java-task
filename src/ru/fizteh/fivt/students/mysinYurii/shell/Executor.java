package ru.fizteh.fivt.students.mysinYurii.shell;

public class Executor {
    Command comander;
    
    public Executor() {
        comander = new Command();
    }
    public void parseAndExec(String comand) throws ShellException {
        comand.trim();
        if (comand.length() == 0) {
            return;
        }
        String[] tempComandList = comand.split(";");
        for (int j = 0; j < tempComandList.length; ++j) {
            String[] comandList = tempComandList[j].trim().split("\\s+");
            if ((comandList.length == 1) && (comandList[0].trim().equals("exit"))) {
                throw new ShellException("exit");
            } else {
                int i = 0;
                while (i < comandList.length) {
                    int left = i;
                    while ((i < comandList.length) && !(comandList[i].equals(";"))) {
                        ++i;
                    }
                    String task = comandList[left];
                    if (task.equals("pwd")) {
                        comander.pwd();
                    } else if (task.equals("mkdir")) {
                        if (i - left == 2) {
                            comander.mkdir(comandList[left + 1]);
                        } else {
                            inRange(task, left, i, 2);
                        }
                    } else if (task.equals("cd")) {
                        if (i - left == 2) {
                            comander.cd(comandList[left + 1]);
                        } else {
                            inRange(task, left, i, 2);
                        }
                    } else if (task.equals("cp")) {
                        if (i - left == 3) {
                            comander.copy(comandList[left + 1], comandList[left + 2]);
                        } else {
                            inRange(task, left, i, 3);
                        }
                    } else if (task.equals("mv")) {
                        if (i - left == 3) {
                            comander.mv(comandList[left + 1], comandList[left + 2]);
                        } else {
                            inRange(task, left, i, 3);
                        }
                    } else if (task.equals("dir")) {
                        if (i - left == 1) {
                            comander.dir();
                        } else {
                            inRange(task, left, i, 1);
                        }
                    } else if (task.equals("rm")) {
                        if (i - left == 2) {
                            comander.rm(comandList[left + 1]);
                        } else {
                            inRange(task, left, i ,2);
                        }
                    } else {
                        throw new ShellException("shell", "Uknown comand: " + comandList[left]);
                    }
                    ++i;
                }
            }
        }
    }
    
    public void inRange(String task, int left, int right, int dist) throws ShellException {
        if (right - left > dist) {
            throw new ShellException(task, "Too many arguments");
        } else {
            throw new ShellException(task, "Too few arguments");
        }
    }
}
