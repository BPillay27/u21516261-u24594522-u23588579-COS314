import java.util.*
public class GA{
    private KnapsackInstance prob;
    private boolean[] best;
    private boolean[][] pop;
    private long seed;

    public GA(KnapsackInstance prob,long seed){
        this.prob=prob;
        best=boolean[prob.getTotalItems()];
        pop=boolean[8][prob.getTotalItems()];
        this.seed=seed;
    }

    public boolean[] findSol(){
        initialpop(); //The random starting population

        
    }

    private void initialpop(){
        Random rand = new Random(this.seed);
        for(int i=0;i<pop.length;i++){
            for(int j=0; j<pop[i].length; j++){
                if (rand.nextDouble() < 0.15) {
                    pop[i][j]=true;
                }else{
                    pop[i][j]=false;
                }
            }
            if(prob.isValid(pop[i])){
                --i;// Stop the for loop from continuing until this is valid
            }
        }
    }
}