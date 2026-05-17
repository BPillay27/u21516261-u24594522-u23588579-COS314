import java.io.IOException;
import java.util.Scanner;

public class Arithmetic {

    private static final int NUM_RUNS = 30;

    // Holds all user-configured GP parameters
    static class Params {
        int    minInitDepth  = 2;
        int    maxInitDepth  = 6;
        int    maxDepth      = 8;
        int    tournamentSize = 5;
        double crossoverRate = 0.80;
        double mutationRate  = 0.20;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Arithmetic GP Classifier (COS314)  ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Modes:");
        System.out.println("  1 - Training  (30 runs, find best seed)");
        System.out.println("  2 - Demo run  (verbose per-generation, single seed)");
        System.out.println("  3 - Testing   (evaluate best model on unseen test set)");
        System.out.print("Select mode: ");
        int mode = Integer.parseInt(sc.nextLine().trim());

        Params p = promptParams(sc);

        switch (mode) {
            case 1: runTraining(sc, p); break;
            case 2: runDemo(sc, p);     break;
            case 3: runTesting(sc, p);  break;
            default: System.out.println("Invalid mode."); break;
        }
    }

    // ── parameter prompt ──────────────────────────────────────────────────────

    private static Params promptParams(Scanner sc) {
        Params p = new Params();
        System.out.println("\n── GP Parameters (press Enter to use default) ──");
        p.minInitDepth   = promptInt(sc,    "Min initial tree depth",  p.minInitDepth);
        p.maxInitDepth   = promptInt(sc,    "Max initial tree depth",  p.maxInitDepth);
        p.maxDepth       = promptInt(sc,    "Max offspring depth",     p.maxDepth);
        p.tournamentSize = promptInt(sc,    "Tournament size",         p.tournamentSize);
        p.crossoverRate  = promptDouble(sc, "Crossover rate (0-1)",    p.crossoverRate);
        p.mutationRate   = promptDouble(sc, "Mutation rate  (0-1)",    p.mutationRate);
        System.out.println("────────────────────────────────────────");
        System.out.printf("  Pop size: 200 | Generations: 100 | Init depth: %d-%d%n",
                p.minInitDepth, p.maxInitDepth);
        System.out.printf("  Max depth: %d | Tournament: %d | Crossover: %.0f%% | Mutation: %.0f%%%n",
                p.maxDepth, p.tournamentSize, p.crossoverRate * 100, p.mutationRate * 100);
        System.out.println("────────────────────────────────────────\n");
        return p;
    }

    private static int promptInt(Scanner sc, String label, int def) {
        System.out.printf("  %-30s [%d]: ", label, def);
        String in = sc.nextLine().trim();
        return in.isEmpty() ? def : Integer.parseInt(in);
    }

    private static double promptDouble(Scanner sc, String label, double def) {
        System.out.printf("  %-30s [%.2f]: ", label, def);
        String in = sc.nextLine().trim();
        return in.isEmpty() ? def : Double.parseDouble(in);
    }

    // ── mode 1: 30 silent runs, identify best seed ────────────────────────────

    private static void runTraining(Scanner sc, Params p) throws IOException {
        System.out.print("Enter base seed: ");
        long baseSeed = Long.parseLong(sc.nextLine().trim());
        System.out.print("Enter training file path: ");
        String trainPath = sc.nextLine().trim();

        ArithmeticDataLoader trainData = ArithmeticDataLoader.load(trainPath);
        System.out.println("Loaded: " + trainData);

        Individual bestOverall = null;
        double     bestFitness = -1.0;
        long       bestSeed    = baseSeed;
        double[]   accuracies  = new double[NUM_RUNS];

        for (int run = 0; run < NUM_RUNS; run++) {
            long seed = baseSeed + run;
            System.out.printf("Run %2d/30 | seed=%-10d ", run + 1, seed);

            GP_Engine  engine = makeEngine(seed, trainData, false, p);
            Individual best   = engine.run();
            Metrics    m      = Metrics.evaluate(best, trainData);

            accuracies[run] = m.accuracy;
            System.out.printf("Acc=%.4f  F1=%.4f%n", m.accuracy, m.fMeasure);

            if (m.accuracy > bestFitness) {
                bestFitness = m.accuracy;
                bestOverall = best;
                bestSeed    = seed;
            }
        }

        double mean = 0, min = accuracies[0], max = accuracies[0];
        for (double a : accuracies) {
            mean += a;
            if (a < min) min = a;
            if (a > max) max = a;
        }
        mean /= NUM_RUNS;

        System.out.println("\n══════════════ 30-Run Summary ══════════════");
        System.out.printf("Best seed      : %d%n", bestSeed);
        System.out.printf("Best train acc : %.4f  (%.2f%%)%n", bestFitness, bestFitness * 100);
        System.out.printf("Mean acc       : %.4f  Min: %.4f  Max: %.4f%n", mean, min, max);
        System.out.println("Best expression: " + bestOverall);
        System.out.println("\nUse mode 2 or 3 with seed " + bestSeed + " for demo/testing.");
    }

    // ── mode 2: single verbose run (for demo day) ─────────────────────────────

    private static void runDemo(Scanner sc, Params p) throws IOException {
        System.out.print("Enter seed: ");
        long seed = Long.parseLong(sc.nextLine().trim());
        System.out.print("Enter training file path: ");
        String trainPath = sc.nextLine().trim();

        ArithmeticDataLoader trainData = ArithmeticDataLoader.load(trainPath);
        System.out.println("Loaded: " + trainData);
        System.out.println();

        long       start   = System.currentTimeMillis();
        GP_Engine  engine  = makeEngine(seed, trainData, true, p);
        Individual best    = engine.run();
        long       elapsed = System.currentTimeMillis() - start;

        System.out.println("\n══════════════ Demo Result ══════════════");
        System.out.println("Best expression : " + best);
        System.out.println("Training metrics: " + Metrics.evaluate(best, trainData).summary());
        System.out.printf ("Runtime         : %.2f seconds%n", elapsed / 1000.0);
    }

    // ── mode 3: evaluate best model on unseen test set ────────────────────────

    private static void runTesting(Scanner sc, Params p) throws IOException {
        System.out.print("Enter best seed: ");
        long seed = Long.parseLong(sc.nextLine().trim());
        System.out.print("Enter training file path: ");
        String trainPath = sc.nextLine().trim();
        System.out.print("Enter test file path: ");
        String testPath = sc.nextLine().trim();

        ArithmeticDataLoader trainData = ArithmeticDataLoader.load(trainPath);
        ArithmeticDataLoader testData  = ArithmeticDataLoader.load(testPath);
        System.out.println("Train: " + trainData);
        System.out.println("Test : " + testData);

        System.out.println("\nRe-evolving with seed " + seed + " (silent)...");
        long       start   = System.currentTimeMillis();
        GP_Engine  engine  = makeEngine(seed, trainData, false, p);
        Individual best    = engine.run();
        long       elapsed = System.currentTimeMillis() - start;

        Metrics trainM = Metrics.evaluate(best, trainData);
        Metrics testM  = Metrics.evaluate(best, testData);

        System.out.println("\n══════════════ Classification Results ══════════════");
        System.out.println("Best expression : " + best);
        System.out.println();
        System.out.printf("%-20s %-12s %-12s %-12s %-12s%n",
                "Algorithm", "Train(%)", "Test(%)", "F-measure", "Runtime(s)");
        System.out.printf("%-20s %-12.2f %-12.2f %-12.4f %-12.2f%n",
                "GP Arithmetic",
                trainM.accuracy * 100,
                testM.accuracy  * 100,
                testM.fMeasure,
                elapsed / 1000.0);

        System.out.println("\nTest detail: " + testM.summary());

        System.out.println("\n── Per-instance test predictions ──");
        System.out.printf("%-10s %-10s %-10s%n", "Instance", "Actual", "Predicted");
        for (int i = 0; i < testData.size; i++) {
            int predicted = best.classify(testData.features[i]);
            int actual    = testData.labels[i];
            System.out.printf("%-10d %-10d %-10d%s%n",
                    i + 1, actual, predicted,
                    predicted != actual ? "  X" : "");
        }
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private static GP_Engine makeEngine(long seed, ArithmeticDataLoader data,
                                        boolean verbose, Params p) {
        return new GP_Engine(seed, data, verbose,
                p.minInitDepth, p.maxInitDepth, p.maxDepth,
                p.tournamentSize, p.crossoverRate, p.mutationRate);
    }
}
