package pl.beny.smpd;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        long startTime;

//        startTime = System.nanoTime();
//        System.out.println(new SFS().computeSFSP(2));
//        System.out.println((System.nanoTime() - startTime) / 1000000);
//
//        startTime = System.nanoTime();
//        System.out.println(new Fisher().computeFisherP(2));
//        System.out.println((System.nanoTime() - startTime) / 1000000);
//
//        startTime = System.nanoTime();
//        System.out.println(Classificators.classifyNM(Database.getDatabase(Database.ACER).get(1)));
//        System.out.println((System.nanoTime() - startTime) / 1000000);

        startTime = System.nanoTime();
        Quality.checkCrossvalidation(6, 5);
        System.out.println((System.nanoTime() - startTime) / 1000000);
    }

}
