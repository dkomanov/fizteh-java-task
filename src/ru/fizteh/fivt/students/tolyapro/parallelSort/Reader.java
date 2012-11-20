package ru.fizteh.fivt.students.tolyapro.ParallelSort;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.fizteh.fivt.students.tolyapro.wordCounter.BufferCloser;

public class Reader {

    ArrayList<String> files;

    public Reader(ArrayList<String> fileNames) {
        files = fileNames;
    }

    public ArrayList<String> getStrings() throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < files.size(); ++i) {
            FileInputStream in = new FileInputStream(files.get(i));
            InputStreamReader stream = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(stream);
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                result.add(tmp);
            }
            BufferCloser.close(reader);
        }
        return result;
    }

}
