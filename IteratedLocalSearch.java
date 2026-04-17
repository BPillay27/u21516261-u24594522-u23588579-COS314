import java.util.ArrayList;
import java.util.Arrays;

public class IteratedLocalSearch {
    private LocalSearch localSearch;
    private int ILS_iterations;
    private ArrayList<boolean[]> history = new ArrayList<>();

    public IteratedLocalSearch(long seed, String file, int ILS_iterations, int LS_iterations) {
        localSearch = new LocalSearch(seed, file, LS_iterations);

        if (ILS_iterations < 0) {
            this.ILS_iterations = 0;
        } else {
            this.ILS_iterations = ILS_iterations;
        }
    }

    public LocalSearch getSearch() {
        return localSearch;
    }

    private boolean inHistory(boolean[] input) {
        for (int i = 0; i < history.size(); i++) {
            if (Arrays.equals(history.get(i), input)) {
                return true;
            }
        }

        return false;
    }

    // flips a quarter of the bits (rounded down). If a perturb cannot be found, it
    // just returns the original value.
    private boolean[] perturb(boolean[] solution) {
        int flips = Math.max(1, solution.length / 4);

        for (int attempt = 0; attempt < 20; attempt++) {
            boolean[] temp = solution.clone();
            ArrayList<Integer> used = new ArrayList<Integer>();

            while (used.size() < flips) {
                int index = localSearch.getRand().nextInt(temp.length);

                if (!used.contains(index)) {
                    temp[index] = !temp[index];
                    used.add(index);
                }
            }

            if (localSearch.getInstance().isValid(temp) && !inHistory(temp)) {
                history.add(temp.clone());
                return temp;
            }
        }

        return solution.clone();
    }

    public boolean[] search() {
        boolean[] s1 = localSearch.generateValidSolution();
        boolean[] s_star = localSearch.search(s1);
        history.add(s_star.clone());

        for (int i = 0; i < ILS_iterations; i++) {
            boolean[] s_prime = perturb(s_star);

            if (Arrays.equals(s_prime, s_star)) {
                continue;
            }
            boolean[] s_star_prime = localSearch.search(s_prime);

            // acceptance criterion here
            if (localSearch.getInstance().fitness(s_star_prime) > localSearch.getInstance().fitness(s_star)) {
                s_star = s_star_prime.clone();
            }

        }
        return s_star;
    }

    public double getFitness(boolean[] input) {
        return localSearch.getFitness(input);
    }
}