import java.util.List;
import java.util.Collections;

public class LinesSort implements Runnable {
    private List<String> data;

    public LinesSort(List<String> data) {
        this.data = data;
    }

    @Override
    public void run() {
        Collections.sort(data);
    }
}