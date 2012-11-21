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
        if (files.size() == 0) {
            BufferedReader reader = null;
            try {
                InputStreamReader in = new InputStreamReader(System.in);
                reader = new BufferedReader(in);
                String tmp;
                while ((tmp = reader.readLine()) != null) {
                    result.add(tmp);
                }
            } finally {
                BufferCloser.close(reader);
            }
        } else {
            for (int i = 0; i < files.size(); ++i) {
                FileInputStream in = null;
                InputStreamReader stream = null;
                BufferedReader reader = null;
                try {
                    in = new FileInputStream(files.get(i));
                    stream = new InputStreamReader(in);
                    reader = new BufferedReader(stream);
                    String tmp;
                    while ((tmp = reader.readLine()) != null) {
                        result.add(tmp);
                    }
                } finally {
                    BufferCloser.close(reader);
                    BufferCloser.close(stream);
                    BufferCloser.close(in);
                }
            }
        }
        return result;
    }
}
