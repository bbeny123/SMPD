package pl.beny.smpd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bootstrap {

    private static class Sample {
        private String className;
        private List<Double> attr;

        private Sample(String className, List<Double> attr) {
            this.className = className;
            this.attr = attr;
        }
    }

    public static void checkBootstrap(int i, int n, int k) {
        List<Sample> samples = prepareSamples();
        List<List<Boolean>> results = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IntStream.range(0, i).boxed().parallel().forEach(j -> {
            results.get(0).addAll(getResultsNN(samples, IntStream.range(0, n).boxed().map(l -> samples.get(new Random().nextInt(samples.size()))).collect(Collectors.toList())));
            results.get(1).addAll(getResultsNM(samples, IntStream.range(0, n).boxed().map(l -> samples.get(new Random().nextInt(samples.size()))).collect(Collectors.toList())));
            results.get(2).addAll(getResultsKNN(samples, IntStream.range(0, n).boxed().map(l -> samples.get(new Random().nextInt(samples.size()))).collect(Collectors.toList()), k));
        });
        results.forEach(result -> {
            long correct = result.stream().filter(r -> r).count();
            long incorrect = result.stream().filter(r -> !r).count();
            long size = result.size();
            String msg = String.format("TESTY: %d\nPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\nNIEPOPRAWNIE ZAKLASYFIKOWANYCH: %d - %.2f%%\n", size, correct, (double) correct / size * 100, incorrect, (double) incorrect / size * 100);
            System.out.println(msg);
        });
    }

    private static List<Boolean> getResultsNN(List<Sample> samples, List<Sample> training) {
        List<List<Double>> samplesA = training.stream().filter(s -> s.className.equals(Database.ACER)).map(s -> s.attr).collect(Collectors.toList());
        List<List<Double>> samplesB = training.stream().filter(s -> s.className.equals(Database.QUERCUS)).map(s -> s.attr).collect(Collectors.toList());
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classificators.classifyNN(sample.attr, samplesA, samplesB);
                    return sample.className.equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsNM(List<Sample> samples, List<Sample> training) {
        List<List<Double>> samplesA = training.stream().filter(s -> s.className.equals(Database.ACER)).map(s -> s.attr).collect(Collectors.toList());
        List<List<Double>> samplesB = training.stream().filter(s -> s.className.equals(Database.QUERCUS)).map(s -> s.attr).collect(Collectors.toList());
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classificators.classifyNM(sample.attr, samplesA, samplesB);
                    return sample.className.equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsKNN(List<Sample> samples, List<Sample> training, int k) {
        List<List<Double>> samplesA = training.stream().filter(s -> s.className.equals(Database.ACER)).map(s -> s.attr).collect(Collectors.toList());
        List<List<Double>> samplesB = training.stream().filter(s -> s.className.equals(Database.QUERCUS)).map(s -> s.attr).collect(Collectors.toList());
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classificators.classifyKNN(sample.attr, samplesA, samplesB, k);
                    return sample.className.equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Sample> prepareSamples() {
        return Database.getDatabase().entrySet().stream().flatMap(k -> k.getValue().stream().map(v -> new Sample(k.getKey(), v))).collect(Collectors.toList());
    }

}
