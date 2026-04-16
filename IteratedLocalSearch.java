import java.util.ArrayList;
import java.util.Arrays;

public class IteratedLocalSearch {
    private LocalSearch localSearch;
    private int ILS_iterations;
    private ArrayList<boolean[]> history = new ArrayList<>();

    public IteratedLocalSearch(long seed, String file, int LS_iterations, int ILS_iterations) {
        localSearch = new LocalSearch(seed, file, LS_iterations);
        this.ILS_iterations = ILS_iterations;
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

    // flips a quarter of the bits (rounded up). If a perturb cannot be found, it
    // just returns the original value.
    private boolean[] perturb(boolean[] solution) {
        boolean[] temp = solution.clone();
        int flips = Math.max(1, temp.length / 4);
        ArrayList<Integer> used = new ArrayList<>();

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

        return solution.clone();
    }

    public boolean[] search() {
        boolean[] s1 = localSearch.generateValidSolution();
        boolean[] s_star = localSearch.search(s1);

        for (int i = 0; i < ILS_iterations; i++) {
            boolean[] s_prime = perturb(s_star);
            boolean[] s_star_prime = localSearch.search(s_prime);

            // acceptance criterion here
            if (localSearch.getInstance().fitness(s_star_prime) > localSearch.getInstance().fitness(s_star)) {
                s_star = s_star_prime.clone();
            }

        }
        return s_star;

    }
}