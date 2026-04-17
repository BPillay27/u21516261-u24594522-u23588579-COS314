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
        pop = new boolean[55][prob.getTotalItems()];
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
        /*
            for(int i=0;i<pop.length;i++){
                if(rand.nextDouble()<0.15){
                    int idx = rand.nextInt(prob.getTotalItems());
                    survival[i][idx] = !survival[i][idx];
                }
            }
        */
       survival=Mutation(survival);


        // Population update: keep best from parents and children
            popUpdate(survival);
            
            update();
        }
        return best;
    }

    private boolean[][] Mutation(boolean[][] survival){
        int n = prob.getTotalItems();
        double bitMutRate = 1.0 / Math.max(1, n);
        for (int i = 0; i < survival.length; i++) {
            if (survival[i] == null) continue;
            if (i == 0) continue;
            boolean mutated = false;
            for (int b = 0; b < n; b++) {
                if (rand.nextDouble() < bitMutRate) {
                    survival[i][b] = !survival[i][b];
                    mutated = true;
                }
            }
            if (mutated && !prob.isValid(survival[i])) {
                
                ArrayList<Integer> chosen = new ArrayList<>();
                KnapsackItem[] items = prob.getItems();
                for (int k = 0; k < n; k++) if (survival[i][k]) chosen.add(k);
                chosen.sort((a, b) -> Double.compare(
                    items[a].getValue()/items[a].getWeight(),
                    items[b].getValue()/items[b].getWeight())); // ascending
                while (!prob.isValid(survival[i]) && !chosen.isEmpty()) {
                    survival[i][chosen.remove(0)] = false;
                }
            }
        }
        return survival;
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

    private void update(boolean[][] survival){

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
        boolean[][] children = new boolean[group.length][n];
        KnapsackItem[] items = prob.getItems();
        double cap = prob.getCapacity();

        for (int p = 0; p + 1 < group.length; p += 2) {
            boolean[] parent1 = group[p];
            boolean[] parent2 = group[p+1];
            if (parent1 == null || parent2 == null) {
                // copy parents through if one missing
                children[p] = (parent1 != null) ? parent1.clone() : new boolean[n];
                children[p+1] = (parent2 != null) ? parent2.clone() : new boolean[n];
                continue;
            }

            boolean[] child1 = new boolean[n];
            boolean[] child2 = new boolean[n];

            double used1 = 0.0;
            double used2 = 0.0;

            // keep intersection (items both parents include)
            for (int i = 0; i < n; i++) {
                if (parent1[i] && parent2[i]) {
                    child1[i] = true;
                    child2[i] = true;
                    used1 += items[i].getWeight();
                    used2 += items[i].getWeight();
                }
            }

            // candidate list: items present in at least one parent but not yet in child
            ArrayList<Integer> cand = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((parent1[i] || parent2[i]) && !child1[i]) cand.add(i);
            }

            // sort candidates by value/weight ratio descending
            cand.sort((a, b) -> Double.compare(items[b].getValue() / items[b].getWeight(),
                                               items[a].getValue() / items[a].getWeight()));

            // greedy fill for child1
            for (int idx : cand) {
                if (used1 + items[idx].getWeight() <= cap) {
                    child1[idx] = true;
                    used1 += items[idx].getWeight();
                }
            }

            // for child2, shuffle candidate order slightly to diversify then greedy fill
            ArrayList<Integer> cand2 = new ArrayList<>(cand);
            cand2.sort((a, b) -> {
                double ra = items[a].getValue() / items[a].getWeight() + rand.nextDouble() * 1e-6;
                double rb = items[b].getValue() / items[b].getWeight() + rand.nextDouble() * 1e-6;
                return Double.compare(rb, ra);
            });
            for (int idx : cand2) {
                if (used2 + items[idx].getWeight() <= cap) {
                    child2[idx] = true;
                    used2 += items[idx].getWeight();
                }
            }
            /*
            // final safety: if any child still overweight, repair by removing lowest ratio selected items
            if (!prob.isValid(child1)) {
                ArrayList<Integer> chosen = new ArrayList<>();
                for (int i = 0; i < n; i++) if (child1[i]) chosen.add(i);
                chosen.sort((a, b) -> Double.compare(items[a].getValue() / items[a].getWeight(),
                                                      items[b].getValue() / items[b].getWeight()));
                while (!prob.isValid(child1) && !chosen.isEmpty()) {
                    int remove = chosen.remove(0);
                    child1[remove] = false;
                }
            }
            */
            if (!prob.isValid(child2)) {
                ArrayList<Integer> chosen = new ArrayList<>();
                for (int i = 0; i < n; i++) if (child2[i]) chosen.add(i);
                chosen.sort((a, b) -> Double.compare(items[a].getValue() / items[a].getWeight(),
                                                      items[b].getValue() / items[b].getWeight()));
                while (!prob.isValid(child2) && !chosen.isEmpty()) {
                    int remove = chosen.remove(0);
                    child2[remove] = false;
                }
            }

            children[p] = child1;
            children[p+1] = child2;
        }

        // if odd number, copy last parent through
        if (group.length % 2 == 1) {
            children[group.length - 1] = (group[group.length - 1] != null) ? group[group.length - 1].clone() : new boolean[n];
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
