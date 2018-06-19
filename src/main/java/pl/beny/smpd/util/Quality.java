package pl.beny.smpd.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Quality {

    public static List<List<Boolean>> checkBootstrap(int i, int n, int k) {
        List<List<Boolean>> results = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<Sample> samples = Database.getSamples();
        IntStream.range(0, i).forEach(j -> {
            List<Sample> training = IntStream.range(0, n).boxed()
                    .map(l -> samples.get(new Random().nextInt(samples.size())))
                    .collect(Collectors.toList());

            results.get(0).addAll(getResultsNN(samples, training));
            results.get(1).addAll(getResultsNM(samples, training));
            results.get(2).addAll(getResultsKNN(samples, training, k));
        });

        return results;
    }

    public static List<List<Boolean>> checkCrossvalidation(int parts, int k) {
        List<List<Boolean>> results = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<List<Sample>> samples = getSubsets(parts);
        IntStream.range(0, parts).forEach(i -> {
            List<Sample> training = getTraining(parts, i, samples);
            results.get(0).addAll(getResultsNN(samples.get(i), training));
            results.get(1).addAll(getResultsNM(samples.get(i), training));
            results.get(2).addAll(getResultsKNN(samples.get(i), training, k));
        });

        return results;
    }

    private static List<List<Sample>> getSubsets(int parts) {
        List<Sample> samples = new ArrayList<>(Database.getSamples());
        Collections.shuffle(samples);
        List<List<Sample>> subsets = new ArrayList<>();
        int partitionSize = Math.max(1, samples.size() / parts);
        IntStream.range(0, parts)
                .forEach(i -> subsets.add(samples.subList(
                        Math.min(i * partitionSize, samples.size()),
                        i + 1 == parts ? samples.size() : Math.min((i + 1) * partitionSize, samples.size()))));
        return subsets;
    }

    private static List<Boolean> getResultsNN(List<Sample> samples, List<Sample> training) {
        return samples
                .stream()
                .map(sample -> Classifiers.classifyNN(sample, training))
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsNM(List<Sample> samples, List<Sample> training) {
        return samples
                .stream()
                .map(sample -> Classifiers.classifyNM(sample, training))
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsKNN(List<Sample> samples, List<Sample> training, int k) {
        return samples
                .stream()
                .map(sample -> Classifiers.classifyKNN(sample, training, k))
                .collect(Collectors.toList());
    }

    private static List<Sample> getTraining(int parts, int part, List<List<Sample>> subsets) {
        return IntStream.range(0, parts)
                .filter(i -> i != part).boxed()
                .flatMap(i -> subsets.get(i).stream())
                .collect(Collectors.toList());
    }
}
