import java.util.ArrayList;
import java.util.Random;

public class LocalSearch {
    private KnapsackInstance instance;
    private int numIterations;
    private Random rand;
    private long seed;

    public boolean[] generateValidSolution() {
        boolean[] temp = new boolean[instance.getTotalItems()];

        do {
            for (int i = 0; i < instance.getTotalItems(); i++) {
                temp[i] = rand.nextBoolean();
            }
        } while (!instance.isValid(temp));

        return temp;
    }

    // seed and file is self-explanatory, numIterations is the stopping condition
    // for the loop (run for this many iterations) <3
    public LocalSearch(long seed, String file, int numIterations) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Invalid file");
            }

            if (numIterations < 0) {
                throw new IllegalArgumentException("Invalid number of iterations");
            }

            this.seed = seed;
            this.rand = new Random(seed);
            this.instance = new KnapsackInstance(file);
            this.numIterations = numIterations;
        } catch (Exception e) {
            System.out.println("An error has occurred while making the Local Search. Error: " + e.getMessage());
            numIterations = 0;
            instance = null;
            rand = null;
            seed = 0;
        }
    }

    public long getSeed() {
        return seed;
    }

    public KnapsackInstance getInstance() {
        return instance;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public void setNumIteration(int num) {
        if (num < 0) {
            return;
        }

        numIterations = num;
    }

    public Random getRand(){
        return rand;
    }

    // Get the valid neighbours of the current solution.
    private boolean[] getNeighbours(boolean[] solution) {
        ArrayList<boolean[]> neighbours = new ArrayList<>();

        for (int i = 0; i < solution.length; i++) {
            boolean[] neighbour = solution.clone();
            neighbour[i] = !neighbour[i];

            if (instance.isValid(neighbour)) {
                neighbours.add(neighbour);
            }
        }

        if (neighbours.isEmpty()) {
            return null;
        }

        boolean[] best = neighbours.get(0);
        for (int i = 0; i < neighbours.size(); i++) {
            if (instance.fitness(best) <= instance.fitness(neighbours.get(i))) {
                best = neighbours.get(i);
            }
        }
        return best;
    }

    public boolean[] search(boolean[] solution) {
        for (int i = 0; i < numIterations; i++) {
            boolean[] bestNeighbour = getNeighbours(solution);

            if (instance.fitness(bestNeighbour) <= instance.fitness(solution)) {
                return solution;
            }

            solution = bestNeighbour;
        }

        return solution;
    }
    // page 26 and page 41

}