package pl.beny.smpd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bootstrap {

    public static void checkBootstrap(int i, int n, int k) {
        List<Sample> samples = Database.getSamples();
        List<List<Boolean>> results = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IntStream.range(0, i).boxed().parallel().forEach(j -> {
            List<Sample> training = IntStream.range(0, n).boxed()
                    .map(l -> samples.get(new Random().nextInt(samples.size())))
                    .collect(Collectors.toList());

            results.get(0).addAll(getResultsNN(samples, training));
            results.get(1).addAll(getResultsNM(samples, training));
            results.get(2).addAll(getResultsKNN(samples, training, k));
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
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classificators.classifyNN(sample, training);
                    return sample.getClassName().equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsNM(List<Sample> samples, List<Sample> training) {
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classificators.classifyNM(sample, training);
                    return sample.getClassName().equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

    private static List<Boolean> getResultsKNN(List<Sample> samples, List<Sample> training, int k) {
        return samples
                .stream()
                .map(sample -> {
                    String classified = Classificators.classifyKNN(sample, training, k);
                    return sample.getClassName().equals(classified) || "AMBIGUOUS".equals(classified);
                })
                .collect(Collectors.toList());
    }

}
