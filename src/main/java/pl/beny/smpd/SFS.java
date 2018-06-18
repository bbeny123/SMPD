package pl.beny.smpd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SFS {

    private class Result {
        private List<Integer> indexes = new ArrayList<>();
        private int index;
        private double result;

        private void updateResult(int index, double result) {
            if (result > this.result) {
                this.index = index;
                this.result = result;
            }
        }

        private void newIteration() {
            result = 0;
        }

        private void iterationEnd() {
            indexes.add(index);
        }
    }

    public String computeSFSP(int k) {
        Result result = new Result();
        IntStream.range(0, k).boxed().forEach(i -> {
            result.newIteration();
            IntStream.range(0, 64).boxed()
                    .filter(j -> !result.indexes.contains(j))
                    .parallel()
                    .forEach(index -> result.updateResult(index, computeFisher(result.indexes, index)));
            result.iterationEnd();
        });
        return result.indexes + " = " + result.result;
    }

    public static String computeSFS(int k) {
        List<Integer> indexes = new ArrayList<>();
        double result = 0;
        for (int i = 0, index = -1; i < k; i++, indexes.add(index), index = -1) {
            result = 0;
            for (int j = 0; j < 64; j++) {
                if (indexes.contains(j)) continue;
                double temp = computeFisher(indexes, j);
                if (temp > result) {
                    result = temp;
                    index = j;
                }
            }
        }
        return indexes + " = " + result;
    }

    private static double computeFisher(List<Integer> indexes, int index) {
        List<Integer> temp = new ArrayList<>(indexes);
        temp.add(index);
        return Fisher.computeFisher(temp);
    }
}
