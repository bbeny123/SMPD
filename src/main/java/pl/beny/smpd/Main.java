package pl.beny.smpd;

import pl.beny.smpd.util.Quality;

public class Main {

    public static void main(String[] args) {
        long startTime;
//
//        startTime = System.nanoTime();
//        System.out.println(new SFS().computeSFSP(5));
//        System.out.println((System.nanoTime() - startTime) / 1000000);
//
//        startTime = System.nanoTime();
//        System.out.println(new Fisher().computeFisherP(3));
//        System.out.println((System.nanoTime() - startTime) / 1000000);

//        startTime = System.nanoTime();
//        System.out.println(Classifiers.classifyNN(Database.getSamples(Database.ACER).get(1), Database.getSamples()));
//        System.out.println((System.nanoTime() - startTime) / 1000000);
//
        startTime = System.nanoTime();
        Quality.checkCrossvalidation(6, 3);
        System.out.println((System.nanoTime() - startTime) / 1000000);
//
//        startTime = System.nanoTime();
//        Quality.checkBootstrap(30, 200, 6);
//        System.out.println((System.nanoTime() - startTime) / 1000000);
    }

}
