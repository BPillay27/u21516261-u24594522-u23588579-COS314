import java.util.*;

public class Main {
    public static void main(String[] args) {
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
            
            // System.out.println(inst.getCapacity()+"<-This is the capacity & this is the no. of items-> "+inst.getTotalItems());

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
            printsimpleResult("knapPI_1_100_1000_1",simple,inst);
            printsimpleResult("f8_l-d_kp_23_10000", simple8, inst8);
            printsimpleResult("f9_l-d_kp_5_80", simple9, inst9);
            printsimpleResult("f10_l-d_kp_20_879", simple10, inst10);

            

        } catch (Exception e) {
            System.out.println("Error running algorithms: " + e.getMessage());
            e.printStackTrace();
        }
    }

        private static void printsimpleResult(String file, GA al,KnapsackInstance inst){
            boolean[] solution = al.getSol(30);

            if (solution == null) {
                System.out.println("GA returned no valid solution (null) for :"+ file);
            } else {
                System.out.println("The result for "+file+" from GA: " + String.format("%.4f", inst.fitness(solution));
                )
            }
        }
    

   
}
