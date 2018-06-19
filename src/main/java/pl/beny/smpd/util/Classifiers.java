package pl.beny.smpd.util;

import org.ojalgo.matrix.PrimitiveMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Classifiers {

    public static boolean classifyNN(Sample sample, List<Sample> samples) {
        double distanceA = classifyNN(sample, samples, Database.ACER);
        double distanceB = classifyNN(sample, samples, Database.QUERCUS);
        return distanceA == distanceB || sample.getClassName().equals(distanceA > distanceB ? Database.QUERCUS : Database.ACER);
    }

    private static double classifyNN(Sample sample, List<Sample> samples, String className) {
        return Database.getSamples(samples, className)
                .stream()
                .mapToDouble(s -> (IntStream.range(0, 64)
                        .mapToDouble(i -> Math.pow(sample.getAttr().get(i) - s.getAttr().get(i), 2))
                        .sum()))
                .min()
                .orElse(-1);
    }

    public static boolean classifyKNN(Sample sample, List<Sample> samples, int k) {
        List<Double> distanceA = classifyKNN(sample, samples, k, Database.ACER);
        List<Double> distanceB = classifyKNN(sample, samples, k, Database.QUERCUS);

        double maxDistance = Stream.concat(distanceA.stream(), distanceB.stream())
                .mapToDouble(i -> i)
                .sorted()
                .limit(k)
                .max()
                .orElse(-1);

        long numberA = distanceA.stream().filter(i -> i <= maxDistance).count();
        long numberB = distanceB.stream().filter(i -> i <= maxDistance).count();
        return distanceA == distanceB || sample.getClassName().equals(numberB > numberA ? Database.QUERCUS : Database.ACER);
    }

    private static List<Double> classifyKNN(Sample sample, List<Sample> samples, int k, String className) {
        List<Double> distance = Database.getSamples(samples, className)
                .stream()
                .mapToDouble(s -> (IntStream.range(0, 64)
                        .mapToDouble(i -> Math.pow(sample.getAttr().get(i) - s.getAttr().get(i), 2))
                        .sum()))
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        if (distance.isEmpty()) {
            return new ArrayList<>();
        }

        Double max = distance.get(Math.min(distance.size(), k) - 1);
        return distance.stream()
                .filter(d -> d <= max)
                .collect(Collectors.toList());
    }

    public static boolean classifyNM(Sample sample, List<Sample> samples) {
        double distanceA = classifyNM(sample, samples, Database.ACER);
        double distanceB = classifyNM(sample, samples, Database.QUERCUS);
        return distanceA == distanceB || sample.getClassName().equals(distanceA > distanceB ? Database.QUERCUS : Database.ACER);
    }

    private static double classifyNM(Sample sample, List<Sample> samples, String className) {
        return Matrix.getLength(sample.getAttr(),
                Matrix.getAverages(
                        Database.getByIndexes(
                                Database.getSamples(samples, className),
                                IntStream.range(0, 64).boxed().collect(Collectors.toList()))));
    }

    public static String classifyMahalanobis(Sample sample, List<Sample> samples) {
        double mahalanobisA = classifyMahalanobis(sample, samples, Database.ACER);
        double mahalanobisB = classifyMahalanobis(sample, samples, Database.QUERCUS);
        return mahalanobisA == mahalanobisB ? "AMBIGUOUS" : mahalanobisA > mahalanobisB ? Database.QUERCUS : Database.ACER;
    }

    private static double classifyMahalanobis(Sample sample, List<Sample> samples, String className) {
        List<List<Double>> attr = Database.getByIndexes(
                Database.getSamples(samples, className),
                IntStream.range(0, 64).boxed().collect(Collectors.toList()));

        List<Double> avg = Matrix.getAverages(attr);
        PrimitiveMatrix differential = PrimitiveMatrix.FACTORY
                .columns(sample.getAttr().toArray(new Double[0]))
                .subtract(Matrix.getAverageMatrix(avg, 1));

        PrimitiveMatrix covariance = Matrix.getCovariance(attr, avg)
                .invert();

        return differential.transpose()
                .multiply(covariance)
                .multiply(differential)
                .getDeterminant().get();
    }

}
