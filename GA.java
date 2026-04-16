import java.util.*;

public class GA{
    private KnapsackInstance prob;
    private boolean[] best;
    private boolean[][] pop;
    private long seed;
    private Random rand;

    public GA(KnapsackInstance prob,long seed){
        this.prob=prob;
        best = null;
        pop = new boolean[32][prob.getTotalItems()];
        this.seed=seed;
    }

    public boolean[] getSol(int termination){
        rand = new Random(this.seed);
        Double pc=0.85;
        initialpop(); //The random starting population

        //Fitness function is the prob.fit()
        
        boolean[][] survival; //This is the variable that contains the population that are selected
        for(int j=0; j<termination; j++){//Termination
            survival = Selection();//Selection

        //Crossover
            if(rand.nextDouble()<pc){
                survival=Crossover(survival);
            }

        //Mutation
            for(int i=0;i<pop.length;i++){
                if(rand.nextDouble()<0.3){
                    int idx = rand.nextInt(prob.getTotalItems());
                    survival[i][idx] = !survival[i][idx];
                }
            }
        // Population update: keep best from parents and children
            popUpdate(survival);
            
            update();
        }
        return best;
    }

    private void popUpdate(boolean[][] survival){
            ArrayList<boolean[]> pool = new ArrayList<>();
            for (int i = 0; i < pop.length; i++) {
                if (pop[i] != null) pool.add(pop[i].clone());
            }
            for (int i = 0; i < survival.length; i++) {
                if (survival[i] != null) pool.add(survival[i].clone());
            }

            // sort by fitness descending
            Collections.sort(pool, (a, b) -> Double.compare(prob.fitness(b), prob.fitness(a)));

            // build new population keeping the top individuals
            boolean[][] newPop = new boolean[pop.length][pop[0].length];
            
            for (int fill = 0; fill < pop.length ; fill++) {
                newPop[fill] = pool.get(fill).clone();
            }

            pop = newPop;
    }

    private void update(){
        for(int i=0;i<pop.length;i++){
            if(best==null && pop[i]!=null && prob.isValid(pop[i]) ){
                best=pop[i];
            }else
            if(best!=null && pop!=null && pop[i]!=null && prob.isValid(pop[i])  && prob.fitness(best)<prob.fitness(pop[i])){
                best=pop[i];
            }
        }
    }


    private boolean[][] Selection(){//Roulette Wheel Selection & Elitism

        int elitism = 1;
        boolean[][] group = new boolean[pop.length][pop[0].length];

        
        Integer[] idx = new Integer[pop.length];
        for (int i = 0; i < pop.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(prob.fitness(pop[b]), prob.fitness(pop[a])));

        // Preserve elite at the front of the group
        group[0]=pop[idx[0]].clone();

        TreeMap<Double, boolean[]> wheel = new TreeMap<>();
        double percent = 0.0;

        for (int i = 0; i < pop.length; i++) {
            
            double f = prob.fitness(pop[i]);
            wheel.put(percent, pop[i]);
            percent += f;
        }

        for (int i = 1; i < group.length; i++) {
            double pos = rand.nextDouble() * percent;
            Map.Entry<Double, boolean[]> e = wheel.floorEntry(pos);
            if (e == null) e = wheel.firstEntry();
            group[i] = e.getValue().clone();
        }

        return group;

    }

     private boolean[][] Crossover(boolean[][] group){
        int n = prob.getTotalItems();
        boolean[][] children=new boolean[pop.length][n];

        for (int p = 0; p + 1 < group.length; p += 2) {
            boolean[] parent1 = group[p];
            boolean[] parent2 = group[p+1];
            if (parent1 == null || parent2 == null) continue;

            boolean[] child1 = new boolean[n];
            boolean[] child2 = new boolean[n];
            
            for(int i=0; i<n; i++){
                if(i%2==0){
                    child1[i]=parent1[i];
                    child2[i]=parent2[i];
                }else{
                    child1[i]=parent2[i];
                    child2[i]=parent1[i];
                }
            }

            children[p]=child1;
            children[p+1]=child2;

            
        }
        if(pop.length%2==1){
            children[pop.length-1]=group[pop.length-1];
        }

        return children;
    }

    private void initialpop(){
        int loops=0;
        
        for(int i=0;i<pop.length;i++){
            loops=pop[i].length;
            int flips=((loops/4)<=0)? 1 : loops/4;
            for(int j=0;j<flips;j++){
                Integer bit=rand.nextInt(loops);
                pop[i][bit]=true;
            }
            
        
            // If individual is invalid, try to repair by removing random selected items
            if (!prob.isValid(pop[i])) {
                ArrayList<Integer> trues = new ArrayList<>();
                for (int k = 0; k < pop[i].length; k++) if (pop[i][k]) trues.add(k);
                while (!prob.isValid(pop[i]) && !trues.isEmpty()) {
                    int toRemove = trues.remove(rand.nextInt(trues.size()));
                    pop[i][toRemove] = false;
                }
            }else{
                if (best == null) {
                    best = pop[i];
                } else if (best != null && prob.fitness(best) < prob.fitness(pop[i])) {
                    best = pop[i];
                }
            }
        }
    }
}
