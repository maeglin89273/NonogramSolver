package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;

import java.util.LinkedList;
import java.util.List;

public class AnalysisLogger {

    private static long startTime;

    public static void logLineCombimationNumber(String header, List<BitArray>[] arrays) {
        double logProduct = 0;
        List<String> sizes = new LinkedList<>();
        for (List<BitArray> array: arrays) {
            sizes.add(Integer.toString(array.size()));
            logProduct += Math.log10(array.size());
        }

        System.out.println(header + " combination number: " + String.join("x", sizes)+ " ~ 10^" + logProduct);
    }

    public static void startTiming() {
        startTime = System.nanoTime();
    }

    public static void stopTiming(String header) {
        long duration = System.nanoTime() - startTime;
        System.out.format(header + " process takes: %d ns%n", duration);

    }

    public static void divider() {
        System.out.println("----------------------------------\n");
    }
}
