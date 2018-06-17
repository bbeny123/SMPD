package pl.beny.smpd;

import org.ojalgo.matrix.PrimitiveMatrix;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Classificators {

    public static String classifyNN(List<Double> sample) {
        double distanceA = classifyNN(sample, Database.ACER);
        double distanceB = classifyNN(sample, Database.QUERCUS);
        return distanceA == distanceB ? "AMBIGUOUS" : distanceA > distanceB ? Database.QUERCUS : Database.ACER;
    }

    private static double classifyNN(List<Double> sample, String className) {
        return Database.getDatabase(className).stream().mapToDouble(s -> (IntStream.range(0, 64).mapToDouble(i -> Math.pow(sample.get(i) - s.get(i), 2)).sum())).min().orElse(-1);
    }

    public static String classifyKNN(List<Double> sample, int k) {
        List<Double> distanceA = classifyKNN(sample, k, Database.ACER);
        List<Double> distanceB = classifyKNN(sample, k, Database.QUERCUS);
        double maxDistance = Stream.concat(distanceA.stream(), distanceB.stream()).mapToDouble(i -> i).sorted().limit(k).max().orElse(-1);
        long numberA = distanceA.stream().filter(i -> i <= maxDistance).count();
        long numberB = distanceB.stream().filter(i -> i <= maxDistance).count();
        return numberA == numberB ? "AMBIGUOUS" : numberB > numberA ? Database.QUERCUS : Database.ACER;
    }

    private static List<Double> classifyKNN(List<Double> sample, int k, String className) {
        List<Double> distance = Database.getDatabase(className).stream().mapToDouble(s -> (IntStream.range(0, 64).mapToDouble(i -> Math.pow(sample.get(i) - s.get(i), 2)).sum())).sorted().boxed().collect(Collectors.toList());
        Double max = distance.get(k >= distance.size() ? distance.size() - 1 : k - 1);
        return distance.stream().filter(d -> d <= max).collect(Collectors.toList());
    }

    public static String classifyMN(List<Double> sample) {
        double distanceA = classifyMN(sample, Database.ACER);
        double distanceB = classifyMN(sample, Database.QUERCUS);
        return distanceA == distanceB ? "AMBIGUOUS" : distanceA > distanceB ? Database.QUERCUS : Database.ACER;
    }

    private static double classifyMN(List<Double> sample, String className) {
        return Fisher.computeNominator(sample, Fisher.getAverages(Database.getByIndexes(className, IntStream.range(0, 64).boxed().collect(Collectors.toList()))));
    }

    public static String classifyMahalanobis(List<Double> sample) {
        double mahalanobisA = classifyMahalanobis(sample, Database.ACER);
        double mahalanobisB = classifyMahalanobis(sample, Database.QUERCUS);
        return mahalanobisA == mahalanobisB ? "AMBIGUOUS" : mahalanobisA > mahalanobisB ? Database.QUERCUS : Database.ACER;
    }

    private static double classifyMahalanobis(List<Double> sample, String className) {
        List<List<Double>> attr = Database.getByIndexes(className, IntStream.range(0, 64).boxed().collect(Collectors.toList()));
        List<Double> avg = Fisher.getAverages(attr);
        PrimitiveMatrix differential = PrimitiveMatrix.FACTORY.columns(sample.toArray(new Double[0])).subtract(Fisher.getAverageMatrix(avg, 1));
        PrimitiveMatrix covariance = Fisher.getCovariance(attr, avg).invert();
        return differential.transpose().multiply(covariance).multiply(differential).getDeterminant().get();
    }


}
