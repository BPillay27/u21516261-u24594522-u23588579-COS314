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

    public IteratedLocalSearch(long seed, KnapsackInstance file, int ILS_iterations, int LS_iterations) {
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

    private boolean[] perturb(boolean[] solution) {
        boolean[] temp = solution.clone();
        int flips = Math.max(1, solution.length / 5);

        ArrayList<Integer> selected = new ArrayList<Integer>();
        ArrayList<Integer> unselected = new ArrayList<Integer>();

        for (int i = 0; i < temp.length; i++) {
            if (temp[i]) {
                selected.add(i);
            } else {
                unselected.add(i);
            }
        }

        // First remove some chosen items
        int removals = Math.min(flips, selected.size());
        for (int i = 0; i < removals; i++) {
            int pos = localSearch.getRand().nextInt(selected.size());
            int index = selected.remove(pos);
            temp[index] = false;
        }

        // Then try to add some unchosen items back if they fit
        int additions = Math.min(flips, unselected.size());
        for (int i = 0; i < additions; i++) {
            int pos = localSearch.getRand().nextInt(unselected.size());
            int index = unselected.remove(pos);

            temp[index] = true;
            if (!localSearch.getInstance().isValid(temp)) {
                temp[index] = false;
            }
        }

        return temp;
    }

    public boolean[] search() {
        boolean[] s1 = localSearch.generateValidSolution();
        boolean[] s_star = localSearch.search(s1);
        boolean[] best = s_star.clone();
        history.clear();
        history.add(s_star.clone());

        double worseAcceptanceChance = 0.15;

        for (int i = 0; i < ILS_iterations; i++) {
            boolean[] s_prime = perturb(s_star);

            if (Arrays.equals(s_prime, s_star)) {
                continue;
            }

            boolean[] s_star_prime = localSearch.search(s_prime);

            if (inHistory(s_star_prime)) {
                continue;
            }

            if (localSearch.getInstance().fitness(s_star_prime) > localSearch.getInstance().fitness(best)) {
                best = s_star_prime.clone();
            }

            if (localSearch.getInstance().fitness(s_star_prime) > localSearch.getInstance().fitness(s_star)) {
                s_star = s_star_prime.clone();
            } else if (localSearch.getRand().nextDouble() < worseAcceptanceChance) {
                s_star = s_star_prime.clone();
            }

            history.add(s_star.clone());
        }

        return best;
    }

    public double getFitness(boolean[] input) {
        return localSearch.getFitness(input);
    }
}