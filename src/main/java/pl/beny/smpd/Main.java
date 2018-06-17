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

        startTime = System.nanoTime();
        System.out.println(Classificators.classifyMN(Database.getDatabase(Database.ACER).get(1)));
        System.out.println((System.nanoTime() - startTime) / 1000000);
    }

}
