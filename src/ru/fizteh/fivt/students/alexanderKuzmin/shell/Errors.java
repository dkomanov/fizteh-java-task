package ru.fizteh.fivt.students.alexanderKuzmin.shell;

/**
* @author Alexander Kuzmin group 196 Class Errors
* 
*/

public class Errors {

    public static void printErrAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }
    
    public static void printErrAndNoExit(String message) {
        System.err.println(message);
    }
}