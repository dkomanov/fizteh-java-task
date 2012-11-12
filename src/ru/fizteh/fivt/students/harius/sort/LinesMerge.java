import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LinesMerge implements Callable<List<String>> {
    private List<String>[] datas;

    public LinesMerge(List<String>... datas) {
        this.datas = datas;
    }

    @Override
    public List<String> call() {
        List<String> result = new ArrayList<String>();
        int[] indices = new int[datas.length];
        for(;;) {
            int best = -1;
            for (int array = 0; array < datas.length; ++array) {
                if (indices[array] != datas[array].size()) {
                    String peek = datas[array].get(indices[array]);
                    if (best == -1 || peek.compareTo(datas[best].get(indices[best])) < 0) {
                        best = array;
                    }
                }
            }
            if (best == -1) {
                break;
            }
            result.add(datas[best].get(indices[best]));
            ++indices[best];
        }
        return result;
    }
}