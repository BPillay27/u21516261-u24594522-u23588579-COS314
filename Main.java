import java.util.*;

public class Main {
    private void testGA() {
        String file = "Datasets/knapPI_1_100_1000_1";
        long seed = 23;
        Scanner scanner = new java.util.Scanner(System.in);

        System.out.print("Enter seed (press Enter to use default 1234): ");
        String line = scanner.next();

        System.out.println(line);
        scanner.close();
        if (line != null && !line.trim().isEmpty()) {
            try {
                seed = Long.parseLong(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid seed input, using default 1234.");
            }
        }

        try {

            KnapsackInstance inst = new KnapsackInstance(file);
            KnapsackInstance inst1 = new KnapsackInstance("Datasets/f1_l-d_kp_10_269");
            KnapsackInstance inst2 = new KnapsackInstance("Datasets/f2_l-d_kp_20_878");
            KnapsackInstance inst3 = new KnapsackInstance("Datasets/f3_l-d_kp_4_20");
            KnapsackInstance inst4 = new KnapsackInstance("Datasets/f4_l-d_kp_4_11");
            KnapsackInstance inst5 = new KnapsackInstance("Datasets/f5_l-d_kp_15_375");
            KnapsackInstance inst6 = new KnapsackInstance("Datasets/f6_l-d_kp_10_60");
            KnapsackInstance inst7 = new KnapsackInstance("Datasets/f7_l-d_kp_7_50");
            KnapsackInstance inst8 = new KnapsackInstance("Datasets/f8_l-d_kp_23_10000");
            KnapsackInstance inst9 = new KnapsackInstance("Datasets/f9_l-d_kp_5_80");
            KnapsackInstance inst10 = new KnapsackInstance("Datasets/f10_l-d_kp_20_879");
            // System.out.println("Got to KnapsackInstance");

            // System.out.println(inst.getCapacity()+"<-This is the capacity & this is the
            // no. of items-> "+inst.getTotalItems());

            GA simple = new GA(inst, seed);
            GA simple1 = new GA(inst1, seed);
            GA simple2 = new GA(inst2, seed);
            GA simple3 = new GA(inst3, seed);
            GA simple4 = new GA(inst4, seed);
            GA simple5 = new GA(inst5, seed);
            GA simple6 = new GA(inst6, seed);
            GA simple7 = new GA(inst7, seed);
            GA simple8 = new GA(inst8, seed);
            GA simple9 = new GA(inst9, seed);
            GA simple10 = new GA(inst10, seed);

            printsimpleResult("f1_l-d_kp_10_269", simple1, inst1);
            printsimpleResult("f2_l-d_kp_20_878", simple2, inst2);
            printsimpleResult("f3_l-d_kp_4_20", simple3, inst3);
            printsimpleResult("f4_l-d_kp_4_11", simple4, inst4);
            printsimpleResult("f5_l-d_kp_15_375", simple5, inst5);
            printsimpleResult("f6_l-d_kp_10_60", simple6, inst6);
            printsimpleResult("f7_l-d_kp_7_50", simple7, inst7);
            printsimpleResult("knapPI_1_100_1000_1", simple, inst);
            printsimpleResult("f8_l-d_kp_23_10000", simple8, inst8);
            printsimpleResult("f9_l-d_kp_5_80", simple9, inst9);
            printsimpleResult("f10_l-d_kp_20_879", simple10, inst10);

        } catch (Exception e) {
            System.out.println("Error running algorithms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testILS() {
        try {
            long seed = 1234;

            // Just Al at ITL_iterations = 100, LS_iterations = 100
            int ITL_iterations = 100;
            int LS_iterations = 100;

            ArrayList<IteratedLocalSearch> list = new ArrayList<>();

            IteratedLocalSearch ITL_1 = new IteratedLocalSearch(seed, "Datasets/f1_l-d_kp_10_269", ITL_iterations,
                    LS_iterations);
            list.add(ITL_1);

            IteratedLocalSearch ITL_2 = new IteratedLocalSearch(seed, "Datasets/f2_l-d_kp_20_878", ITL_iterations,
                    LS_iterations);
            list.add(ITL_2);

            IteratedLocalSearch ITL_3 = new IteratedLocalSearch(seed, "Datasets/f3_l-d_kp_4_20", ITL_iterations,
                    LS_iterations);
            list.add(ITL_3);

            IteratedLocalSearch ITL_4 = new IteratedLocalSearch(seed, "Datasets/f4_l-d_kp_4_11", ITL_iterations,
                    LS_iterations);
            list.add(ITL_4);

            IteratedLocalSearch ITL_5 = new IteratedLocalSearch(seed, "Datasets/f5_l-d_kp_15_375", ITL_iterations,
                    LS_iterations);
            list.add(ITL_5);

            IteratedLocalSearch ITL_6 = new IteratedLocalSearch(seed, "Datasets/f6_l-d_kp_10_60", ITL_iterations,
                    LS_iterations);
            list.add(ITL_6);

            IteratedLocalSearch ITL_7 = new IteratedLocalSearch(seed, "Datasets/f7_l-d_kp_7_50", ITL_iterations,
                    LS_iterations);
            list.add(ITL_7);

            IteratedLocalSearch ITL_8 = new IteratedLocalSearch(seed, "Datasets/f8_l-d_kp_23_10000", ITL_iterations,
                    LS_iterations);
            list.add(ITL_8);

            IteratedLocalSearch ITL_9 = new IteratedLocalSearch(seed, "Datasets/f9_l-d_kp_5_80", ITL_iterations,
                    LS_iterations);
            list.add(ITL_9);

            IteratedLocalSearch ITL_10 = new IteratedLocalSearch(seed, "Datasets/f10_l-d_kp_20_879", ITL_iterations,
                    LS_iterations);
            list.add(ITL_10);

            IteratedLocalSearch ITL_11 = new IteratedLocalSearch(seed, "Datasets/knapPI_1_100_1000_1", ITL_iterations,
                    LS_iterations);
            list.add(ITL_11);

            int count = 1;
            for (IteratedLocalSearch ITL : list) {
                String info = "";
                if (count == 11) {
                    info = "PI: ";
                } else {
                    info = "f" + count + ": ";
                }
                System.out.println(info + ITL.getFitness(ITL.search()));
                count++;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while testing ILS. Message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // testILS();
        String file = "Datasets/knapPI_1_100_1000_1";
        long seed = 23;
        Scanner scanner = new java.util.Scanner(System.in);

        System.out.print("Enter seed (press Enter to use default 1234): ");
        String line = scanner.next();

        System.out.println(line);
        scanner.close();
        if (line != null && !line.trim().isEmpty()) {
            try {
                seed = Long.parseLong(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid seed input, using default 1234.");
            }
        }

        try {

            KnapsackInstance inst = new KnapsackInstance(file);
            KnapsackInstance inst1 = new KnapsackInstance("Datasets/f1_l-d_kp_10_269");
            KnapsackInstance inst2 = new KnapsackInstance("Datasets/f2_l-d_kp_20_878");
            KnapsackInstance inst3 = new KnapsackInstance("Datasets/f3_l-d_kp_4_20");
            KnapsackInstance inst4 = new KnapsackInstance("Datasets/f4_l-d_kp_4_11");
            KnapsackInstance inst5 = new KnapsackInstance("Datasets/f5_l-d_kp_15_375");
            KnapsackInstance inst6 = new KnapsackInstance("Datasets/f6_l-d_kp_10_60");
            KnapsackInstance inst7 = new KnapsackInstance("Datasets/f7_l-d_kp_7_50");
            KnapsackInstance inst8 = new KnapsackInstance("Datasets/f8_l-d_kp_23_10000");
            KnapsackInstance inst9 = new KnapsackInstance("Datasets/f9_l-d_kp_5_80");
            KnapsackInstance inst10 = new KnapsackInstance("Datasets/f10_l-d_kp_20_879");
            // System.out.println("Got to KnapsackInstance");

            // System.out.println(inst.getCapacity()+"<-This is the capacity & this is the
            // no. of items-> "+inst.getTotalItems());

            GA simple = new GA(inst, seed);
            GA simple1 = new GA(inst1, seed);
            GA simple2 = new GA(inst2, seed);
            GA simple3 = new GA(inst3, seed);
            GA simple4 = new GA(inst4, seed);
            GA simple5 = new GA(inst5, seed);
            GA simple6 = new GA(inst6, seed);
            GA simple7 = new GA(inst7, seed);
            GA simple8 = new GA(inst8, seed);
            GA simple9 = new GA(inst9, seed);
            GA simple10 = new GA(inst10, seed);

            printsimpleResult("f1_l-d_kp_10_269", simple1, inst1);
            printsimpleResult("f2_l-d_kp_20_878", simple2, inst2);
            printsimpleResult("f3_l-d_kp_4_20", simple3, inst3);
            printsimpleResult("f4_l-d_kp_4_11", simple4, inst4);
            printsimpleResult("f5_l-d_kp_15_375", simple5, inst5);
            printsimpleResult("f6_l-d_kp_10_60", simple6, inst6);
            printsimpleResult("f7_l-d_kp_7_50", simple7, inst7);
            printsimpleResult("f8_l-d_kp_23_10000", simple8, inst8);
            printsimpleResult("f9_l-d_kp_5_80", simple9, inst9);
            printsimpleResult("f10_l-d_kp_20_879", simple10, inst10);
            printsimpleResult("knapPI_1_100_1000_1", simple, inst);

            int ITL_iterations = 100;
            int LS_iterations = 70;

            ArrayList<IteratedLocalSearch> list = new ArrayList<>();

            IteratedLocalSearch ITL_1 = new IteratedLocalSearch(seed, inst1, ITL_iterations,
                    LS_iterations);
            list.add(ITL_1);

            IteratedLocalSearch ITL_2 = new IteratedLocalSearch(seed, inst2, ITL_iterations,
                    LS_iterations);
            list.add(ITL_2);

            IteratedLocalSearch ITL_3 = new IteratedLocalSearch(seed, inst3, ITL_iterations,
                    LS_iterations);
            list.add(ITL_3);

            IteratedLocalSearch ITL_4 = new IteratedLocalSearch(seed, inst4, ITL_iterations,
                    LS_iterations);
            list.add(ITL_4);

            IteratedLocalSearch ITL_5 = new IteratedLocalSearch(seed, inst5, ITL_iterations,
                    LS_iterations);
            list.add(ITL_5);

            IteratedLocalSearch ITL_6 = new IteratedLocalSearch(seed, inst6, ITL_iterations,
                    LS_iterations);
            list.add(ITL_6);

            IteratedLocalSearch ITL_7 = new IteratedLocalSearch(seed, inst7, ITL_iterations,
                    LS_iterations);
            list.add(ITL_7);

            IteratedLocalSearch ITL_8 = new IteratedLocalSearch(seed, inst8, ITL_iterations,
                    LS_iterations);
            list.add(ITL_8);

            IteratedLocalSearch ITL_9 = new IteratedLocalSearch(seed, inst9, ITL_iterations,
                    LS_iterations);
            list.add(ITL_9);

            IteratedLocalSearch ITL_10 = new IteratedLocalSearch(seed, inst10, ITL_iterations,
                    LS_iterations);
            list.add(ITL_10);

            IteratedLocalSearch ITL_11 = new IteratedLocalSearch(seed, inst, ITL_iterations,
                    LS_iterations);
            list.add(ITL_11);

            String[] files = { "f1_l-d_kp_10_269", "f2_l-d_kp_20_878", "f3_l-d_kp_4_20",
                    "f4_l-d_kp_4_11", "f5_l-d_kp_15_375", "f6_l-d_kp_10_60",
                    "f7_l-d_kp_7_50", "f8_l-d_kp_23_10000", "f9_l-d_kp_5_80",
                    "f10_l-d_kp_20_879", "knapPI_1_100_1000_1" };

            int count = 0;
            for (IteratedLocalSearch ITL : list) {
                String info = files[count] + " : ";

                System.out.println(info + ITL.getFitness(ITL.search()));
                count++;
            }

        } catch (Exception e) {
            System.out.println("Error running algorithms: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void printsimpleResult(String file, GA al, KnapsackInstance inst) {
        boolean[] solution = al.getSol(100);

        if (solution == null) {
            System.out.println("GA returned no valid solution (null) for :" + file);
        } else {
            System.out.println("The result for " + file + " from GA: " + inst.fitness(solution));

        }
    }

    private static String makeBitString(boolean[] input) {
        String output = "";

        for (int i = 0; i < input.length; i++) {
            output += (input[i]) ? "1" : "0";
        }

        return output;
    }

}
