package pl.beny.smpd.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Fisher {

    public static String computeFisher(int k) {
        List<Integer> indexes = new ArrayList<>();
        double result = 0;
        for (List<Integer> i : combinations(k)) {
            double temp = computeFisher(i);
            if (temp > result) {
                result = temp;
                indexes = i;
            }
        }
        return indexes + " = " + result;
    }

    public static double computeFisher(List<Integer> indexes) {
        return computeFisher(Database.getByIndexes(Database.ACER, indexes), Database.getByIndexes(Database.QUERCUS, indexes));
    }

    private static double computeFisher(List<List<Double>> attrA, List<List<Double>> attrB) {
        List<Double> avgA = Matrix.getAverages(attrA);
        List<Double> avgB = Matrix.getAverages(attrB);
        return Matrix.getLength(avgA, avgB) / computeDenominator(attrA, attrB, avgA, avgB);
    }

    private static double computeDenominator(List<List<Double>> attrA, List<List<Double>> attrB, List<Double> avgA, List<Double> avgB) {
        return attrA.size() > 1 ?
                Matrix.getCovariance(attrA, avgA).add(Matrix.getCovariance(attrB, avgB)).getDeterminant().get() :
//                Matrix.getCovarianceDeterminant(attrA, avgA) + Matrix.getCovarianceDeterminant(attrB, avgB) :
                Matrix.getStd(attrA.get(0), avgA.get(0)) + (Matrix.getStd(attrB.get(0), avgB.get(0)));
    }

    private static List<List<Integer>> combinations(int k) {
        return combinations(IntStream.range(0, 64).boxed().collect(Collectors.toList()), k)
                .collect(Collectors.toList());
    }

    private static Stream<List<Integer>> combinations(List<Integer> list, int k) {
        return k == 0 ?
                Stream.of(Collections.emptyList()) :
                IntStream.range(0, list.size()).boxed()
                        .flatMap(i -> combinations(list.subList(i + 1, list.size()), k - 1)
                                .map(t -> pipe(list.get(i), t)));
    }

    private static List<Integer> pipe(Integer head, List<Integer> tail) {
        List<Integer> newList = new ArrayList<>(tail);
        newList.add(0, head);
        return newList;
    }

    public String computeFisherP(int k) {
        Result result = new Result();
        combinations(k).parallelStream().forEach(indexes -> result.updateResult(indexes, computeFisher(indexes)));
        return result.indexes + " = " + result.result;
    }

    private class Result {
        private List<Integer> indexes;
        private double result;

        private void updateResult(List<Integer> indexes, double result) {
            if (result > this.result) {
                this.indexes = indexes;
                this.result = result;
            }
        }
    }

}
