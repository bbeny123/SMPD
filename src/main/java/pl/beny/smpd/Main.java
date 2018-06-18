package pl.beny.smpd;

public class Main {

    public static void main(String[] args) {
        long startTime;

        startTime = System.nanoTime();
        System.out.println(new SFS().computeSFSP(2));
        System.out.println((System.nanoTime() - startTime) / 1000000);

        startTime = System.nanoTime();
        System.out.println(new Fisher().computeFisherP(2));
        System.out.println((System.nanoTime() - startTime) / 1000000);

        startTime = System.nanoTime();
        System.out.println(Classificators.classifyNN(Database.getSamples(Database.ACER).get(1), Database.getSamples()));
        System.out.println((System.nanoTime() - startTime) / 1000000);

        startTime = System.nanoTime();
        Crossvalidation.checkCrossvalidation(6, 5);
        System.out.println((System.nanoTime() - startTime) / 1000000);

        startTime = System.nanoTime();
        Bootstrap.checkBootstrap(20, 50, 4);
        System.out.println((System.nanoTime() - startTime) / 1000000);
    }

}
