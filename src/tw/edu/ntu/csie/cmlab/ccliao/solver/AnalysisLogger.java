package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;

import java.util.LinkedList;
import java.util.List;

public class AnalysisLogger {

    private static long startTime;
    private static boolean notEnabled = false;
    private static List<Double> durations = new LinkedList<>();

    public static void logLineCombimationNumber(String header, List<BitArray>[] arrays) {
        if (AnalysisLogger.notEnabled) {
            return;
        }

        double logProduct = 0;
        List<String> sizes = new LinkedList<>();
        for (List<BitArray> array: arrays) {
            sizes.add(Integer.toString(array.size()));
            logProduct += Math.log10(array.size());
        }

        System.out.println(header + " combination number: " + String.join("x", sizes)+ " ~ 10^" + logProduct);
    }

    public static void startTiming() {
        if (AnalysisLogger.notEnabled) {
            return;
        }
        startTime = System.nanoTime();
    }

    public static void stopTiming(String header) {
        if (AnalysisLogger.notEnabled) {
            return;
        }
        double duration = (System.nanoTime() - startTime) / (double)1e6;
        System.out.format(header + " process takes: %.3f ms%n", duration);
        durations.add(duration);
    }

    public static void totalTime() {
        double totalDuration = durations.stream().reduce(0.0, Double::sum);
        System.out.format("total time: %.3f ms%n", totalDuration);
    }

    public static void divider() {
        if (AnalysisLogger.notEnabled) {
            return;
        }
        System.out.println("----------------------------------\n");
    }

    public static void setEnabled(boolean enabled) {
        AnalysisLogger.notEnabled = !enabled;
    }
}
