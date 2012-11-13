package misc.shell;

import java.io.*;

public class MovableFile {
    private File file;

    public MovableFile(String pathname) {
        try {
            file = new File(pathname).getCanonicalFile();
        } catch (IOException e) {
            System.out.println("Severe IO error.");
            System.exit(1);
        }
    }

    public void move(String target) {
        try {
            File t = new File(target);
            if (t.isAbsolute()) {
                file = t.getCanonicalFile();
            } else {
                file = new File(file.getAbsolutePath() + "/" + target).getCanonicalFile();
            }
        } catch (IOException e) {
            System.out.println("Severe IO error.");
            System.exit(1);
        }
    }

    public File getFile() {
        return file;
    }
}