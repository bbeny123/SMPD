package pl.beny.smpd;

import org.ojalgo.matrix.PrimitiveMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Fisher {

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

    public String computeFisherP(int k) {
        Result result = new Result();
        combinations(k).parallelStream().forEach(indexes -> result.updateResult(indexes, computeFisher(indexes)));
        return result.indexes + " = " + result.result;
    }

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
        List<Double> avgA = getAverages(attrA);
        List<Double> avgB = getAverages(attrB);
        return computeNominator(avgA, avgB) / computeDenominator(attrA, attrB, avgA, avgB);
    }

    public static double computeNominator(List<Double> avgA, List<Double> avgB) {
        return Math.sqrt(IntStream.range(0, avgA.size()).mapToDouble(i -> Math.pow(avgA.get(i) - avgB.get(i), 2)).sum());
    }

    private static double computeDenominator(List<List<Double>> attrA, List<List<Double>> attrB, List<Double> avgA, List<Double> avgB) {
        return attrA.size() > 1 ? getCovarianceDeterminant(attrA, avgA) + getCovarianceDeterminant(attrB, avgB) : getStd(attrA.get(0), avgA.get(0)) + (getStd(attrB.get(0), avgB.get(0)));
    }

    public static List<Double> getAverages(List<List<Double>> attr) {
        return attr.stream().map(row -> row.stream().mapToDouble(i -> i).average().orElse(0)).collect(Collectors.toList());
    }

    private static double getStd(List<Double> values, double avg) {
        return Math.sqrt(values.stream().mapToDouble(i -> Math.pow(i - avg, 2)).sum() / values.size());
    }

    private static double getCovarianceDeterminant(List<List<Double>> attr, List<Double> avg) {
        return getCovariance(attr, avg).getDeterminant().get();
    }

    public static PrimitiveMatrix getCovariance(List<List<Double>> attr, List<Double> avg) {
        int size = attr.get(0).size();
        return getCovariance(PrimitiveMatrix.FACTORY.rows(attr.stream().map(l -> l.toArray(new Double[0])).toArray(Double[][]::new)), getAverageMatrix(avg, size), size);
    }

    private static PrimitiveMatrix getCovariance(PrimitiveMatrix values, PrimitiveMatrix avg, int size) {
        PrimitiveMatrix differential = getDifferential(values, avg);
        return differential.multiply(differential.transpose()).divide(size);
    }

    private static PrimitiveMatrix getDifferential(PrimitiveMatrix values, PrimitiveMatrix avg) {
        return values.subtract(avg);
    }

    public static PrimitiveMatrix getAverageMatrix(List<Double> avg, int size) {
        Double[][] arr = new Double[size][];
        IntStream.range(0, size).forEach(i -> arr[i] = avg.toArray(new Double[0]));
        return PrimitiveMatrix.FACTORY.columns(arr);
    }

    private static List<List<Integer>> combinations(int k) {
        return combinations(IntStream.range(0, 64).boxed().collect(Collectors.toList()), k).collect(Collectors.toList());
    }

    private static Stream<List<Integer>> combinations(List<Integer> list, int k) {
        return k == 0 ? Stream.of(Collections.emptyList()) : IntStream.range(0, list.size()).boxed().flatMap(i -> combinations(list.subList(i + 1, list.size()), k - 1).map(t -> pipe(list.get(i), t)));
    }

    private static List<Integer> pipe(Integer head, List<Integer> tail) {
        List<Integer> newList = new ArrayList<>(tail);
        newList.add(0, head);
        return newList;
    }

}
