package pl.beny.smpd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Database {

    public static final String ACER = "Acer";
    public static final String QUERCUS = "Quercus";
    private static final Map<String, List<List<Double>>> matrix = Map.of(ACER, new ArrayList<>(), QUERCUS, new ArrayList<>());

    private static Database instance = new Database();

    private Database() {
        Scanner in;

        try {
            in = new Scanner(new File("src/main/resources/Maple_Oak"));
        } catch (Exception e) {
            System.out.println("file problem");
            return;
        }

        while (in.hasNext()) {
            String arr[] = in.nextLine().split(",", 2);
            matrix.getOrDefault(arr[0].split(" ")[0], new ArrayList<>()).add(Stream.of(arr[1].split(",")).map(Double::parseDouble).collect(Collectors.toList()));
        }
    }

    static Map<String, List<List<Double>>> getDatabase() {
        return matrix;
    }

    static List<List<Double>> getByIndexes(String className, List<Integer> indexes) {
        return indexes.stream().map(i -> matrix.get(className).stream().map(a -> a.get(i)).collect(Collectors.toList())).collect(Collectors.toList());
    }

}
