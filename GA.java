import java.util.*;

public class GA{
    private KnapsackInstance prob;
    private boolean[] best;
    private boolean[][] pop;
    private long seed;
    private Random rand;

    public GA(KnapsackInstance prob,long seed){
        this.prob=prob;
        best=new boolean[prob.getTotalItems()];
        pop=new boolean[8][prob.getTotalItems()];
        //Population has a size of 8
        this.seed=seed;
    }

    public boolean[] findSol(){
        rand = new Random(this.seed);
        Double pc=0.75;
        initialpop(); //The random starting population

        //Fitness function is the prob.fit()
        
        boolean[][] survival; //This is the variable that contains the population that are selected
        for(int j=0; j<10; j++){//Termination
            survival = Selection();//Selection

        //Crossover
            if(rand.nextDouble()<pc){
                survival=Crossover(survival);
            }

        //Mutation
            for(int i=0;i<6;i++){
                if(rand.nextDouble()<0.1){
                    survival[i][rand.nextInt(prob.getTotalItems()-1)]=!survival[i][rand.nextInt(prob.getTotalItems()-1)];
                }
            }
        //Population update
            pop=survival;
            update();
        }
        return best;
    }

    private void update(){
        for(int i=0;i<6;i++){
            if(best==null && pop[i]!=null){
                best=pop[i];
            }else
            if(best!=null && pop!=null && pop[i]!=null && prob.isValid(pop[i])  && prob.fitness(best)<prob.fitness(pop[i])){
                best=pop[i];
            }
        }
    }

    private boolean[][] Selection(){//Roulette Wheel Selection

        boolean[][] group = new boolean[pop.length][];
        TreeMap<Double, boolean[]> wheel = new TreeMap<>();
        double percent = 0.0;

        for (int i = 0; i < pop.length; i++) {
            
            double f = prob.fitness(pop[i]);
            wheel.put(percent, pop[i]);
            percent += f;
            
        }

        for (int i = 0; i < group.length; i++) {
            double pos = rand.nextDouble() * percent;
            Map.Entry<Double, boolean[]> e = wheel.floorEntry(pos);
            if (e == null) e = wheel.firstEntry();
            group[i] = e.getValue();
        }

        return group;
    }

     private boolean[][] Crossover(boolean[][] group){//Roulette Wheel Selection
        boolean hold=false;
        for(int i=0; i<(prob.getTotalItems()/2);i++){
            hold =group[0][i];
            group[1][i]=hold;
            hold =group[2][i];
            group[3][i]=hold;
            hold =group[4][i];
            group[5][i]=hold;
        }
        return group;
    }

    private void initialpop(){
        
        for(int i=0;i<pop.length;i++){
            for(int j=0; j<pop[i].length; j++){
                if (rand.nextDouble() < 0.15) {
                    pop[i][j]=true;
                }else{
                    pop[i][j]=false;
                }
            }
            if(!prob.isValid(pop[i]) ){
            }else{
                if(best==null){
                    best=pop[i];
                }else if(best!=null && prob.fitness(best)<prob.fitness(pop[i])){
                    best=pop[i];
                }
                 
            }
        }
    }
}