package pl.beny.smpd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Database {

    public static final String ACER = "Acer";
    public static final String QUERCUS = "Quercus";
    private static final List<Sample> samples = new ArrayList<>();

    private static Database instance = new Database();

    private Database() {
        Scanner in;

        try {
            in = new Scanner(new File("src/main/resources/Maple_Oak"));
        } catch (Exception e) {
            System.out.println("file problem");
            return;
        }

        in.nextLine(); //skip first line
        while (in.hasNext()) {
            String arr[] = in.nextLine().split(",", 2);
            samples.add(new Sample(arr[0].split(" ")[0], Stream.of(arr[1].split(",")).map(Double::parseDouble).collect(Collectors.toList())));
        }
    }

    static List<Sample> getSamples() {
        return samples;
    }

    static List<Sample> getSamples(String className) {
        return samples.stream().filter(s -> className.equals(s.getClassName())).collect(Collectors.toList());
    }

    static List<Sample> getSamples(List<Sample> samples, String className) {
        return samples.stream().filter(s -> className.equals(s.getClassName())).collect(Collectors.toList());
    }

    static List<List<Double>> getByIndexes(String className, List<Integer> indexes) {
        return indexes
                .stream()
                .map(i -> samples.stream()
                        .filter(s -> className.equals(s.getClassName()))
                        .map(a -> a.getAttr().get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    static List<List<Double>> getByIndexes(List<Sample> samples, List<Integer> indexes) {
        return indexes
                .stream()
                .map(i -> samples.stream()
                        .map(a -> a.getAttr().get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

}
