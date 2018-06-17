package pl.beny.smpd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Crossvalidation {

    public static void checkCrossvalidation(int parts, int k) {
        List<List<List<Double>>> subsetsA = getSubsets(parts, Database.ACER);
        List<List<List<Double>>> subsetsB = getSubsets(parts, Database.QUERCUS);
        List<List<Boolean>> results = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IntStream.range(0, parts).parallel().forEach(i -> {
            List<List<Double>> samplesA = getTraining(parts, i, subsetsA);
            List<List<Double>> samplesB = getTraining(parts, i, subsetsB);
            results.get(0).addAll(getResultsNN(subsetsA, samplesA, samplesB, i, Database.ACER));
            results.get(0).addAll(getResultsNN(subsetsB, samplesA, samplesB, i, Database.QUERCUS));
            results.get(1).addAll(getResultsNM(subsetsA, samplesA, samplesB, i, Database.ACER));
            results.get(1).addAll(getResultsNM(subsetsB, samplesA, samplesB, i, Database.QUERCUS));
            results.get(2).addAll(getResultsKNN(subsetsA, samplesA, samplesB, i, Database.ACER, k));
            results.get(2).addAll(getResultsKNN(subsetsB, samplesA, samplesB, i, Database.QUERCUS, k));
        });
        results.forEach(result -> {
            long correct = result.stream().filter(r -> r).count();
            long incorrect = result.stream().filter(r -> !r).count();
            long size = result.size();
            String msg = String.format("TESTY: %d\nPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\nNIEPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\n", size, correct, (double) correct / size * 100, incorrect, (double) incorrect / size * 100);
            System.out.println(msg);
        });
    }

    private static List<Boolean> getResultsNN(List<List<List<Double>>> subset, List<List<Double>> samplesA , List<List<Double>> samplesB, int part, String className) {
        List<String> correct = Arrays.asList(className, "AMBIGUOUS");
        return subset.get(part)
                .stream()
                .map(sample -> correct.contains(Classificators.classifyNN(sample, samplesA, samplesB)))
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsNM(List<List<List<Double>>> subset, List<List<Double>> samplesA , List<List<Double>> samplesB, int part, String className) {
        List<String> correct = Arrays.asList(className, "AMBIGUOUS");
        return subset.get(part)
                .stream()
                .map(sample -> correct.contains(Classificators.classifyNM(sample, samplesA, samplesB)))
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsKNN(List<List<List<Double>>> subset, List<List<Double>> samplesA , List<List<Double>> samplesB, int part, String className, int k) {
        List<String> correct = Arrays.asList(className, "AMBIGUOUS");
        return subset.get(part)
                .stream()
                .map(sample -> correct.contains(Classificators.classifyKNN(sample, samplesA, samplesB, k)))
                .collect(Collectors.toList());
    }

    private static List<List<Double>> getTraining(int parts, int part, List<List<List<Double>>> subsets) {
        return IntStream.range(0, parts).filter(i -> i != part).boxed().flatMap(i -> subsets.get(i).stream()).collect(Collectors.toList());
    }

    private static List<List<List<Double>>> getSubsets(int parts, String className) {
        List<List<Double>> samples = Database.getDatabase(className);
        Collections.shuffle(samples);
        List<List<List<Double>>> subsets = new ArrayList<>();
        int partitionSize = Math.max(1, samples.size() / parts);
        for (int i = 0; i < parts; i++) {
            subsets.add(samples.subList(i * partitionSize, i + 1 == parts ? samples.size() : Math.min((i + 1) * partitionSize, samples.size())));
        }
        return subsets;
    }

}
