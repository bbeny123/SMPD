package pl.beny.smpd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Crossvalidation {

    public static void checkCrossvalidation(int parts, int k) {
        List<List<Sample>> subsets = getSubsets(parts);
        List<List<Boolean>> results = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        IntStream.range(0, parts).parallel().forEach(i -> {
            List<Sample> training = getTraining(parts, i, subsets);
            results.get(0).addAll(getResultsNN(subsets, training, i, Database.ACER));
            results.get(0).addAll(getResultsNN(subsets, training, i, Database.QUERCUS));
            results.get(1).addAll(getResultsNM(subsets, training, i, Database.ACER));
            results.get(1).addAll(getResultsNM(subsets, training, i, Database.QUERCUS));
            results.get(2).addAll(getResultsKNN(subsets, training, i, Database.ACER, k));
            results.get(2).addAll(getResultsKNN(subsets, training, i, Database.QUERCUS, k));
        });

        results.forEach(result -> {
            long correct = result.stream().filter(r -> r).count();
            long incorrect = result.stream().filter(r -> !r).count();
            long size = result.size();
            String msg = String.format("TESTY: %d\nPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\nNIEPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\n", size, correct, (double) correct / size * 100, incorrect, (double) incorrect / size * 100);
            System.out.println(msg);
        });
    }

    private static List<Boolean> getResultsNN(List<List<Sample>> subsets, List<Sample> training, int part, String className) {
        List<String> correct = Arrays.asList(className, "AMBIGUOUS");
        return Database.getSamples(subsets.get(part), className)
                .stream()
                .map(sample -> correct.contains(Classificators.classifyNN(sample, training)))
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsNM(List<List<Sample>> subsets, List<Sample> training, int part, String className) {
        List<String> correct = Arrays.asList(className, "AMBIGUOUS");
        return Database.getSamples(subsets.get(part), className)
                .stream()
                .map(sample -> correct.contains(Classificators.classifyNM(sample, training)))
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsKNN(List<List<Sample>> subsets, List<Sample> training, int part, String className, int k) {
        List<String> correct = Arrays.asList(className, "AMBIGUOUS");
        return Database.getSamples(subsets.get(part), className)
                .stream()
                .map(sample -> correct.contains(Classificators.classifyKNN(sample, training, k)))
                .collect(Collectors.toList());
    }

    private static List<Sample> getTraining(int parts, int part, List<List<Sample>> subsets) {
        return IntStream.range(0, parts)
                .filter(i -> i != part).boxed()
                .flatMap(i -> subsets.get(i).stream())
                .collect(Collectors.toList());
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

}
