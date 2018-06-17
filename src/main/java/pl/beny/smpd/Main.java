package pl.beny.smpd;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.nanoTime();
   //     System.out.println(new SFS().computeSFSP(6));
        long b = (System.nanoTime() - startTime) / 1000000;
        System.out.println(b);
        startTime = System.nanoTime();
    }

}
