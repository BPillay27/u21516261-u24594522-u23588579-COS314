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

    for (int attempt = 0; attempt < 100; attempt++) {
        ArrayList<Integer> selected = new ArrayList<Integer>();
        ArrayList<Integer> unselected = new ArrayList<Integer>();

        for (int i = 0; i < temp.length; i++) {
            if (temp[i]) {
                selected.add(i);
            } else {
                unselected.add(i);
            }
        }

        if (selected.isEmpty() && unselected.isEmpty()) {
            return temp;
        }

        boolean[] candidate = temp.clone();
        int moveType = localSearch.getRand().nextInt(3);

        if (moveType == 0) {
            if (selected.size() < 1 || unselected.size() < 1) {
                continue;
            }

            int removeIndex = selected.get(localSearch.getRand().nextInt(selected.size()));
            int addIndex = unselected.get(localSearch.getRand().nextInt(unselected.size()));

            candidate[removeIndex] = false;
            candidate[addIndex] = true;
        }

        else if (moveType == 1) {
            if (selected.size() < 1 || unselected.size() < 2) {
                continue;
            }

            int removeIndex = selected.get(localSearch.getRand().nextInt(selected.size()));

            int addPos1 = localSearch.getRand().nextInt(unselected.size());
            int addPos2;
            do {
                addPos2 = localSearch.getRand().nextInt(unselected.size());
            } while (addPos2 == addPos1);

            int addIndex1 = unselected.get(addPos1);
            int addIndex2 = unselected.get(addPos2);

            candidate[removeIndex] = false;
            candidate[addIndex1] = true;
            candidate[addIndex2] = true;
        }

        else {
            if (selected.size() < 2 || unselected.size() < 1) {
                continue;
            }

            int removePos1 = localSearch.getRand().nextInt(selected.size());
            int removePos2;
            do {
                removePos2 = localSearch.getRand().nextInt(selected.size());
            } while (removePos2 == removePos1);

            int removeIndex1 = selected.get(removePos1);
            int removeIndex2 = selected.get(removePos2);
            int addIndex = unselected.get(localSearch.getRand().nextInt(unselected.size()));

            candidate[removeIndex1] = false;
            candidate[removeIndex2] = false;
            candidate[addIndex] = true;
        }

        if (localSearch.getInstance().isValid(candidate) && !Arrays.equals(candidate, solution)) {
            return candidate;
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

        double worseAcceptanceChance = 0.6;

        for (int i = 0; i < ILS_iterations; i++) {
            boolean[] s_prime = perturb(s_star);

            if (Arrays.equals(s_prime, s_star)) {
                continue;
            }

            boolean[] s_star_prime = localSearch.search(s_prime);

            if (localSearch.getInstance().fitness(s_star_prime) > localSearch.getInstance().fitness(best)) {
                best = s_star_prime.clone();
            }

            if (!inHistory(s_star_prime)) {
                if (localSearch.getInstance().fitness(s_star_prime) > localSearch.getInstance().fitness(s_star)) {
                    s_star = s_star_prime.clone();
                } else if (localSearch.getRand().nextDouble() < worseAcceptanceChance) {
                    s_star = s_star_prime.clone();
                }

                history.add(s_star.clone());
                if (history.size() > 20) {
                    history.remove(0);
                }
            }
        }

        return best;
    }

    public double getFitness(boolean[] input) {
        return localSearch.getFitness(input);
    }
}