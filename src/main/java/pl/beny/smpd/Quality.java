package pl.beny.smpd;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Quality {

    public static void checkBootstrap(int i, int n, int k) {
        List<List<Boolean>> results = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<Sample> samples = Database.getSamples();
        IntStream.range(0, i).parallel().forEach(j -> {
            List<Sample> training = IntStream.range(0, n).boxed()
                    .map(l -> samples.get(new Random().nextInt(samples.size())))
                    .collect(Collectors.toList());

            results.get(0).addAll(getResultsNN(samples, training));
            results.get(1).addAll(getResultsNM(samples, training));
            results.get(2).addAll(getResultsKNN(samples, training, k));
        });

        printResult(results);
    }

    public static void checkCrossvalidation(int parts, int k) {
        List<List<Boolean>> results = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<List<Sample>> samples = getSubsets(parts);
        IntStream.range(0, parts).parallel().forEach(i -> {
            List<Sample> training = getTraining(parts, i, samples);
            results.get(0).addAll(getResultsNN(samples.get(i), training));
            results.get(1).addAll(getResultsNM(samples.get(i), training));
            results.get(2).addAll(getResultsKNN(samples.get(i), training, k));
        });

        printResult(results);
    }

    private static List<List<Sample>> getSubsets(int parts) {
        List<Sample> samples = Database.getSamples();
        Collections.shuffle(samples);
        List<List<Sample>> subsets = new ArrayList<>();
        int partitionSize = Math.max(1, samples.size() / parts);
        IntStream.range(0, parts)
                .forEach(i -> subsets.add(samples.subList(
                        i * partitionSize,
                        i + 1 == parts ? samples.size() : Math.min((i + 1) * partitionSize, samples.size()))));
        return subsets;
    }

    private static List<Boolean> getResultsNN(List<Sample> samples, List<Sample> training) {
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classifiers.classifyNN(sample, training);
                    return sample.getClassName().equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsNM(List<Sample> samples, List<Sample> training) {
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classifiers.classifyNM(sample, training);
                    return sample.getClassName().equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsKNN(List<Sample> samples, List<Sample> training, int k) {
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classifiers.classifyKNN(sample, training, k);
                    return sample.getClassName().equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Sample> getTraining(int parts, int part, List<List<Sample>> subsets) {
        return IntStream.range(0, parts)
                .filter(i -> i != part).boxed()
                .flatMap(i -> subsets.get(i).stream())
                .collect(Collectors.toList());
    }

    private static void printResult(List<List<Boolean>> results) {
        results.forEach(result -> {
            long correct = result.stream().filter(r -> r).count();
            long incorrect = result.stream().filter(r -> !r).count();
            long size = result.size();
            String msg = String.format("TESTY: %d\nPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\nNIEPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\n", size, correct, (double) correct / size * 100, incorrect, (double) incorrect / size * 100);
            System.out.println(msg);
        });
    }
}
