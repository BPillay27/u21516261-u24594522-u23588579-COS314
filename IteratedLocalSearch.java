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

    // private boolean[] perturb(boolean[] solution) {
    // boolean[] temp = solution.clone();
    // int flips = Math.max(1, solution.length / 5);

    // ArrayList<Integer> selected = new ArrayList<Integer>();
    // ArrayList<Integer> unselected = new ArrayList<Integer>();

    // for (int i = 0; i < temp.length; i++) {
    // if (temp[i]) {
    // selected.add(i);
    // } else {
    // unselected.add(i);
    // }
    // }

    // // First remove some chosen items
    // int removals = Math.min(flips, selected.size());
    // for (int i = 0; i < removals; i++) {
    // int pos = localSearch.getRand().nextInt(selected.size());
    // int index = selected.remove(pos);
    // temp[index] = false;
    // }

    // // Then try to add some unchosen items back if they fit
    // int additions = Math.min(flips, unselected.size());
    // for (int i = 0; i < additions; i++) {
    // int pos = localSearch.getRand().nextInt(unselected.size());
    // int index = unselected.remove(pos);

    // temp[index] = true;
    // if (!localSearch.getInstance().isValid(temp)) {
    // temp[index] = false;
    // }
    // }

    // return temp;
    // }

    // private boolean[] perturb(boolean[] solution) {
    // boolean[] temp = solution.clone();

    // ArrayList<Integer> selected = new ArrayList<Integer>();
    // ArrayList<Integer> unselected = new ArrayList<Integer>();

    // for (int i = 0; i < temp.length; i++) {
    // if (temp[i]) {
    // selected.add(i);
    // } else {
    // unselected.add(i);
    // }
    // }

    // if (selected.size() == 0 || unselected.size() < 2) {
    // return temp;
    // }

    // int attempts = 100;

    // for (int a = 0; a < attempts; a++) {
    // boolean[] candidate = temp.clone();

    // int removePos = localSearch.getRand().nextInt(selected.size());
    // int removeIndex = selected.get(removePos);

    // int addPos1 = localSearch.getRand().nextInt(unselected.size());
    // int addIndex1 = unselected.get(addPos1);

    // int addPos2;
    // do {
    // addPos2 = localSearch.getRand().nextInt(unselected.size());
    // } while (addPos2 == addPos1);

    // int addIndex2 = unselected.get(addPos2);

    // candidate[removeIndex] = false;
    // candidate[addIndex1] = true;
    // candidate[addIndex2] = true;

    // if (localSearch.getInstance().isValid(candidate)) {
    // return candidate;
    // }
    // }

    // return temp;
    // }

    // private boolean[] perturb(boolean[] solution) {
    //     boolean[] temp = solution.clone();

    //     int exchanges = Math.max(1, solution.length / 50); // for 100 items, this gives 2

    //     for (int e = 0; e < exchanges; e++) {
    //         ArrayList<Integer> selected = new ArrayList<Integer>();
    //         ArrayList<Integer> unselected = new ArrayList<Integer>();

    //         for (int i = 0; i < temp.length; i++) {
    //             if (temp[i]) {
    //                 selected.add(i);
    //             } else {
    //                 unselected.add(i);
    //             }
    //         }

    //         if (selected.size() == 0 || unselected.size() < 2) {
    //             break;
    //         }

    //         boolean changed = false;

    //         for (int attempt = 0; attempt < 100; attempt++) {
    //             boolean[] candidate = temp.clone();

    //             int removePos = localSearch.getRand().nextInt(selected.size());
    //             int removeIndex = selected.get(removePos);

    //             int addPos1 = localSearch.getRand().nextInt(unselected.size());
    //             int addIndex1 = unselected.get(addPos1);

    //             int addPos2;
    //             do {
    //                 addPos2 = localSearch.getRand().nextInt(unselected.size());
    //             } while (addPos2 == addPos1);

    //             int addIndex2 = unselected.get(addPos2);

    //             candidate[removeIndex] = false;
    //             candidate[addIndex1] = true;
    //             candidate[addIndex2] = true;

    //             if (localSearch.getInstance().isValid(candidate)) {
    //                 temp = candidate;
    //                 changed = true;
    //                 break;
    //             }
    //         }

    //         if (!changed) {
    //             break;
    //         }
    //     }

    //     return temp;
    // }

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

        // 1-for-1
        if (moveType == 0) {
            if (selected.size() < 1 || unselected.size() < 1) {
                continue;
            }

            int removeIndex = selected.get(localSearch.getRand().nextInt(selected.size()));
            int addIndex = unselected.get(localSearch.getRand().nextInt(unselected.size()));

            candidate[removeIndex] = false;
            candidate[addIndex] = true;
        }

        // 1-for-2
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

        // 2-for-1
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

    // public boolean[] search() {
    // boolean[] s1 = localSearch.generateValidSolution();
    // boolean[] s_star = localSearch.search(s1);
    // boolean[] best = s_star.clone();
    // history.clear();
    // history.add(s_star.clone());

    // double worseAcceptanceChance = 0.15;

    // for (int i = 0; i < ILS_iterations; i++) {
    // boolean[] s_prime = perturb(s_star);

    // if (Arrays.equals(s_prime, s_star)) {
    // continue;
    // }

    // boolean[] s_star_prime = localSearch.search(s_prime);

    // if (inHistory(s_star_prime)) {
    // continue;
    // }

    // if (localSearch.getInstance().fitness(s_star_prime) >
    // localSearch.getInstance().fitness(best)) {
    // best = s_star_prime.clone();
    // }

    // if (localSearch.getInstance().fitness(s_star_prime) >
    // localSearch.getInstance().fitness(s_star)) {
    // s_star = s_star_prime.clone();
    // } else if (localSearch.getRand().nextDouble() < worseAcceptanceChance) {
    // s_star = s_star_prime.clone();
    // }

    // history.add(s_star.clone());
    // }

    // return best;
    // }

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