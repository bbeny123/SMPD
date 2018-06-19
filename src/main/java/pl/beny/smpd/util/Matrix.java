package pl.beny.smpd.util;

import org.ojalgo.matrix.PrimitiveMatrix;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Matrix {

    public static double getLength(List<Double> matA, List<Double> matB) {
        return Math.sqrt(IntStream.range(0, matA.size()).mapToDouble(i -> Math.pow(matA.get(i) - matB.get(i), 2)).sum());
    }

    public static List<Double> getAverages(List<List<Double>> attr) {
        return attr.stream().map(row -> row.stream().mapToDouble(i -> i).average().orElse(0)).collect(Collectors.toList());
    }

    public static double getStd(List<Double> values, double avg) {
        return Math.sqrt(values.stream().mapToDouble(i -> Math.pow(i - avg, 2)).sum() / values.size());
    }

    public static double getCovarianceDeterminant(List<List<Double>> attr, List<Double> avg) {
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

}
