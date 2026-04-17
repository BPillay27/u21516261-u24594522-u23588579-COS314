import java.util.ArrayList;
import java.util.Random;

public class LocalSearch {
    private KnapsackInstance instance;
    private int numIterations;
    private Random rand;
    private long seed;

    public boolean[] generateValidSolution() {
        boolean[] output = new boolean[instance.getTotalItems()];
        ArrayList<Integer> used = new ArrayList<>();

        while (used.size() < output.length) {
            int index = rand.nextInt(output.length);

            if (used.contains(index)) {
                continue;
            }

            used.add(index);
            output[index] = true;

            if (!instance.isValid(output)) {
                output[index] = false;
            }
        }
        return output;
    }

    // seed and file is self-explanatory, numIterations is the stopping condition
    // for the loop (run for this many iterations)
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
            this.numIterations = 0;
            this.instance = null;
            this.rand = null;
            this.seed = 0;
        }
    }

    public LocalSearch(long seed, KnapsackInstance file, int numIterations) {
        try {
            
            this.seed = seed;
            this.rand = new Random(seed);
            this.instance =file;
            this.numIterations = numIterations;
        } catch (Exception e) {
            System.out.println("An error has occurred while making the Local Search. Error: " + e.getMessage());
            this.numIterations = 0;
            this.instance = null;
            this.rand = null;
            this.seed = 0;
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

    public Random getRand() {
        return rand;
    }

    private boolean[] getNeighbours(boolean[] solution) {
        boolean[] best = null;

        // single-bit neighbours
        for (int i = 0; i < solution.length; i++) {
            boolean[] neighbour = solution.clone();
            neighbour[i] = !neighbour[i];

            if (instance.isValid(neighbour)) {
                if (best == null || instance.fitness(neighbour) > instance.fitness(best)) {
                    best = neighbour;
                }
            }
        }

        // swap neighbours
        for (int i = 0; i < solution.length; i++) {
            if (!solution[i]) {
                continue;
            }

            for (int j = 0; j < solution.length; j++) {
                if (solution[j]) {
                    continue;
                }

                boolean[] neighbour = solution.clone();
                neighbour[i] = false;
                neighbour[j] = true;

                if (instance.isValid(neighbour)) {
                    if (best == null || instance.fitness(neighbour) > instance.fitness(best)) {
                        best = neighbour;
                    }
                }
            }
        }

        return best;
    }

    public boolean[] search(boolean[] solution) {
        for (int i = 0; i < numIterations; i++) {
            boolean[] bestNeighbour = getNeighbours(solution);

            if (bestNeighbour == null) {
                return solution;
            }

            if (instance.fitness(bestNeighbour) <= instance.fitness(solution)) {
                return solution;
            }

            solution = bestNeighbour.clone();
        }

        return solution;
    }

    public double getFitness(boolean[] input) {
        return instance.fitness(input);
    }
    // page 26 and page 41

}