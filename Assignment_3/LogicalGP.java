import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class LogicalGP {

    public static DecisionTree.TreeNode runGP(List<DataLoader.PatientRecord> train,
                                             int populationSize,
                                             int initialTreeDepth,
                                             int maxOffspringDepth,
                                             int tournamentSize,
                                             double crossoverRate,
                                             double mutationRate,
                                             int mutationOffspringDepth,
                                             int maxGenerations,
                                             Random rand) {
        int maxOff = maxOffspringDepth;

        // Initialize population (ramped half-and-half), prune to initial depth and evaluate immediately
        List<DecisionTree.TreeNode> population = generateInitialPopulation(populationSize, initialTreeDepth, rand);
        List<Double> fitness = new ArrayList<>();
        DecisionTree.TreeNode overallBest = null;
        double overallBestAcc = -1.0;

        for (int i = 0; i < population.size(); i++) {
            DecisionTree.TreeNode pr = DecisionTree.pruneTree(population.get(i), initialTreeDepth, rand);
            population.set(i, pr);
            double acc = evaluateAccuracy(pr, train);
            fitness.add(acc);
            if (acc > overallBestAcc) {
                overallBestAcc = acc;
                overallBest = pr.cloneTree();
            }
        }

        // Generation 0: best among initial population (siblings)
        int idx0 = 0;
        for (int i = 1; i < fitness.size(); i++) if (fitness.get(i) > fitness.get(idx0)) idx0 = i;
        DecisionTree.TreeNode genBest = population.get(idx0);
        double genBestAcc = fitness.get(idx0);
        System.out.println("Generation 0: bestAccuracy=" + genBestAcc + ", bestTree=" + (genBest == null ? "null" : genBest.printTree()));

        for (int gen = 1; gen <= maxGenerations; gen++) {
            List<DecisionTree.TreeNode> nextGen = new ArrayList<>();
            List<Double> nextFitness = new ArrayList<>();

            while (nextGen.size() < populationSize) {
                DecisionTree.TreeNode parent1 = tournamentSelect(population, fitness, tournamentSize, rand).cloneTree();
                DecisionTree.TreeNode parent2 = tournamentSelect(population, fitness, tournamentSize, rand).cloneTree();

                DecisionTree.TreeNode child1 = parent1;
                DecisionTree.TreeNode child2 = parent2;

                boolean didCrossover = false;
                if (rand.nextDouble() < crossoverRate) {
                    DecisionTree.TreeNode[] off = crossoverTrees(parent1, parent2, rand);
                    child1 = off[0];
                    child2 = off[1];
                    didCrossover = true;
                }

                if (!didCrossover && rand.nextDouble() < mutationRate) {
                    child1 = mutateTree(child1, mutationOffspringDepth, rand);
                }
                if (!didCrossover && rand.nextDouble() < mutationRate && nextGen.size() + 1 < populationSize) {
                    child2 = mutateTree(child2, mutationOffspringDepth, rand);
                }

                child1 = DecisionTree.pruneTree(child1, maxOff, rand);
                double acc1 = evaluateAccuracy(child1, train);
                nextGen.add(child1);
                nextFitness.add(acc1);

                if (nextGen.size() < populationSize) {
                    child2 = DecisionTree.pruneTree(child2, maxOff, rand);
                    double acc2 = evaluateAccuracy(child2, train);
                    nextGen.add(child2);
                    nextFitness.add(acc2);
                }
            }

            // best among this generation (siblings)
            int idx = 0;
            for (int i = 1; i < nextFitness.size(); i++) if (nextFitness.get(i) > nextFitness.get(idx)) idx = i;
            genBest = nextGen.get(idx);
            genBestAcc = nextFitness.get(idx);

            // update overall best if needed
            if (genBestAcc > overallBestAcc) {
                overallBestAcc = genBestAcc;
                overallBest = genBest.cloneTree();
            }

            population = nextGen;
            fitness = nextFitness;

            System.out.println("Generation " + gen + ": bestAccuracy=" + genBestAcc + ", bestTree=" + (genBest == null ? "null" : genBest.printTree()));
        }

        // Return overall best pruned tree
        return overallBest;
    }

    /**
     * Same as runGP but logs parameters and per-generation best individual + fitness
     * into the provided output file. Also logs final train/test accuracies.
     */
    public static DecisionTree.TreeNode runGPWithLogging(List<DataLoader.PatientRecord> train,
                                                         List<DataLoader.PatientRecord> test,
                                                         int populationSize,
                                                         int initialTreeDepth,
                                                         int maxOffspringDepth,
                                                         int tournamentSize,
                                                         double crossoverRate,
                                                         double mutationRate,
                                                         int mutationOffspringDepth,
                                                         int maxGenerations,
                                                         String outPath,
                                                         Random rand) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(outPath, true))) {
            pw.println("--- GP Run ---");
            pw.println("populationSize=" + populationSize);
            pw.println("initialTreeDepth=" + initialTreeDepth);
            pw.println("maxOffspringDepth=" + maxOffspringDepth);
            pw.println("tournamentSize=" + tournamentSize);
            pw.println("crossoverRate=" + crossoverRate);
            pw.println("mutationRate=" + mutationRate);
            pw.println("mutationOffspringDepth=" + mutationOffspringDepth);
            pw.println("maxGenerations=" + maxGenerations);
            pw.println();

            int maxOff = maxOffspringDepth;

            // Initialize population (ramped half-and-half), prune to initial depth and evaluate immediately
            List<DecisionTree.TreeNode> population = generateInitialPopulation(populationSize, initialTreeDepth, rand);
            List<Double> fitness = new ArrayList<>();
            DecisionTree.TreeNode overallBest = null;
            double overallBestAcc = -1.0;

            for (int i = 0; i < population.size(); i++) {
                DecisionTree.TreeNode pr = DecisionTree.pruneTree(population.get(i), initialTreeDepth, rand);
                population.set(i, pr);
                double acc = evaluateAccuracy(pr, train);
                fitness.add(acc);
                if (acc > overallBestAcc) {
                    overallBestAcc = acc;
                    overallBest = pr.cloneTree();
                }
            }

            // Generation 0: prepare fitness but do not log per-generation bests to file
            int idx0 = 0;
            for (int i = 1; i < fitness.size(); i++) if (fitness.get(i) > fitness.get(idx0)) idx0 = i;
            DecisionTree.TreeNode genBest = population.get(idx0);
            double genBestAcc = fitness.get(idx0);

            for (int gen = 1; gen <= maxGenerations; gen++) {
                List<DecisionTree.TreeNode> nextGen = new ArrayList<>();
                List<Double> nextFitness = new ArrayList<>();

                while (nextGen.size() < populationSize) {
                    DecisionTree.TreeNode parent1 = tournamentSelect(population, fitness, tournamentSize, rand).cloneTree();
                    DecisionTree.TreeNode parent2 = tournamentSelect(population, fitness, tournamentSize, rand).cloneTree();

                    DecisionTree.TreeNode child1 = parent1;
                    DecisionTree.TreeNode child2 = parent2;

                    boolean didCrossover = false;
                    if (rand.nextDouble() < crossoverRate) {
                        DecisionTree.TreeNode[] off = crossoverTrees(parent1, parent2, rand);
                        child1 = off[0];
                        child2 = off[1];
                        didCrossover = true;
                    }

                    if (!didCrossover && rand.nextDouble() < mutationRate) {
                        child1 = mutateTree(child1, mutationOffspringDepth, rand);
                    }
                    if (!didCrossover && rand.nextDouble() < mutationRate && nextGen.size() + 1 < populationSize) {
                        child2 = mutateTree(child2, mutationOffspringDepth, rand);
                    }

                    child1 = DecisionTree.pruneTree(child1, maxOff, rand);
                    double acc1 = evaluateAccuracy(child1, train);
                    nextGen.add(child1);
                    nextFitness.add(acc1);

                    if (nextGen.size() < populationSize) {
                        child2 = DecisionTree.pruneTree(child2, maxOff, rand);
                        double acc2 = evaluateAccuracy(child2, train);
                        nextGen.add(child2);
                        nextFitness.add(acc2);
                    }
                }

                // best among this generation (siblings)
                int idx = 0;
                for (int i = 1; i < nextFitness.size(); i++) if (nextFitness.get(i) > nextFitness.get(idx)) idx = i;
                genBest = nextGen.get(idx);
                genBestAcc = nextFitness.get(idx);

                if (genBestAcc > overallBestAcc) {
                    overallBestAcc = genBestAcc;
                    overallBest = genBest.cloneTree();
                }

                population = nextGen;
                fitness = nextFitness;
            }

            double finalTrain = evaluateAccuracy(overallBest, train);
            double finalTest = (test == null) ? -1.0 : evaluateAccuracy(overallBest, test);
            pw.println();
            pw.println("Final best (pruned) trainAccuracy=" + finalTrain + ", testAccuracy=" + finalTest);
            pw.println("Best Tree: " + (overallBest == null ? "null" : overallBest.printTree()));
            pw.println("--- End Run ---\n");
            pw.flush();
            return overallBest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<DecisionTree.TreeNode> generateInitialPopulation(int populationSize, int maxDepth, Random rand) {
        List<DecisionTree.TreeNode> pop = new ArrayList<>();
        if (maxDepth <= 0) maxDepth = 1;

        int basePerDepth = populationSize / maxDepth;
        int remainder = populationSize % maxDepth;
        boolean giveExtraToFull = true;

        for (int d = 1; d <= maxDepth; d++) {
            int count = basePerDepth + (d <= remainder ? 1 : 0);
            if (count <= 0) continue;

            int fullCount = count / 2;
            int growCount = count / 2;
            if (count % 2 == 1) {
                if (giveExtraToFull) fullCount++;
                else growCount++;
                giveExtraToFull = !giveExtraToFull;
            }

            for (int i = 0; i < fullCount; i++) pop.add(DecisionTree.generateFullTree(d, rand));
            for (int i = 0; i < growCount; i++) pop.add(DecisionTree.generateGrowTree(d, rand));
        }

        while (pop.size() < populationSize) pop.add(DecisionTree.generateGrowTree(maxDepth, rand));
        if (pop.size() > populationSize) pop = new ArrayList<>(pop.subList(0, populationSize));
        return pop;
    }

    private static double evaluateAccuracy(DecisionTree.TreeNode tree, List<DataLoader.PatientRecord> data) {
        if (tree == null || data == null || data.isEmpty()) return 0.0;
        int correct = 0;
        for (DataLoader.PatientRecord r : data) {
            boolean pred = tree.evaluate(r.features);
            boolean actual = (r.targetClass == 1);
            if (pred == actual) correct++;
        }
        return (double) correct / data.size();
    }

    private static DecisionTree.TreeNode tournamentSelect(List<DecisionTree.TreeNode> pop, List<Double> fitness, int tSize, Random rand) {
        int n = pop.size();
        double bestFit = -1.0;
        DecisionTree.TreeNode best = null;
        for (int i = 0; i < tSize; i++) {
            int idx = rand.nextInt(n);
            if (fitness.get(idx) > bestFit || best == null) {
                bestFit = fitness.get(idx);
                best = pop.get(idx);
            }
        }
        return best;
    }

    private static DecisionTree.TreeNode[] crossoverTrees(DecisionTree.TreeNode a, DecisionTree.TreeNode b, Random rand) {
        DecisionTree.TreeNode aClone = a.cloneTree();
        DecisionTree.TreeNode bClone = b.cloneTree();

        List<List<Integer>> pathsA = getAllPaths(aClone);
        List<List<Integer>> pathsB = getAllPaths(bClone);
        if (pathsA.isEmpty() || pathsB.isEmpty()) return new DecisionTree.TreeNode[] {aClone, bClone};

        List<Integer> pA = pathsA.get(rand.nextInt(pathsA.size()));
        List<Integer> pB = pathsB.get(rand.nextInt(pathsB.size()));

        DecisionTree.TreeNode subA = getSubtreeAtPath(aClone, pA);
        DecisionTree.TreeNode subB = getSubtreeAtPath(bClone, pB);

        DecisionTree.TreeNode newA = replaceSubtreeAtPath(aClone, pA, subB.cloneTree());
        DecisionTree.TreeNode newB = replaceSubtreeAtPath(bClone, pB, subA.cloneTree());

        return new DecisionTree.TreeNode[] {newA, newB};
    }

    private static DecisionTree.TreeNode mutateTree(DecisionTree.TreeNode root, int mutateDepth, Random rand) {
        DecisionTree.TreeNode clone = root.cloneTree();
        List<List<Integer>> paths = getAllPaths(clone);
        if (paths.isEmpty()) return clone;
        List<Integer> p = paths.get(rand.nextInt(paths.size()));
        DecisionTree.TreeNode newSub = DecisionTree.generateGrowTree(mutateDepth, rand);
        return replaceSubtreeAtPath(clone, p, newSub);
    }

    private static List<List<Integer>> getAllPaths(DecisionTree.TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        collectPaths(root, new ArrayList<>(), out);
        return out;
    }

    private static void collectPaths(DecisionTree.TreeNode node, List<Integer> cur, List<List<Integer>> out) {
        if (node == null) return;
        out.add(new ArrayList<>(cur));
        if (node instanceof DecisionTree.AndNode) {
            DecisionTree.AndNode a = (DecisionTree.AndNode) node;
            cur.add(0);
            collectPaths(a.leftChild, cur, out);
            cur.remove(cur.size() - 1);
            cur.add(1);
            collectPaths(a.rightChild, cur, out);
            cur.remove(cur.size() - 1);
        } else if (node instanceof DecisionTree.OrNode) {
            DecisionTree.OrNode o = (DecisionTree.OrNode) node;
            cur.add(0);
            collectPaths(o.leftChild, cur, out);
            cur.remove(cur.size() - 1);
            cur.add(1);
            collectPaths(o.rightChild, cur, out);
            cur.remove(cur.size() - 1);
        } else if (node instanceof DecisionTree.NotNode) {
            DecisionTree.NotNode n = (DecisionTree.NotNode) node;
            cur.add(0);
            collectPaths(n.child, cur, out);
            cur.remove(cur.size() - 1);
        }
    }

    private static DecisionTree.TreeNode getSubtreeAtPath(DecisionTree.TreeNode root, List<Integer> path) {
        DecisionTree.TreeNode cur = root;
        for (int step : path) {
            if (cur == null) return null;
            if (cur instanceof DecisionTree.AndNode) {
                cur = (step == 0) ? ((DecisionTree.AndNode) cur).leftChild : ((DecisionTree.AndNode) cur).rightChild;
            } else if (cur instanceof DecisionTree.OrNode) {
                cur = (step == 0) ? ((DecisionTree.OrNode) cur).leftChild : ((DecisionTree.OrNode) cur).rightChild;
            } else if (cur instanceof DecisionTree.NotNode) {
                cur = ((DecisionTree.NotNode) cur).child;
            } else {
                return cur;
            }
        }
        return cur;
    }

    private static DecisionTree.TreeNode replaceSubtreeAtPath(DecisionTree.TreeNode root, List<Integer> path, DecisionTree.TreeNode replacement) {
        if (path.isEmpty()) return replacement;
        DecisionTree.TreeNode cur = root.cloneTree();
        replaceHelper(cur, path, 0, replacement);
        return cur;
    }

    private static void replaceHelper(DecisionTree.TreeNode node, List<Integer> path, int idx, DecisionTree.TreeNode replacement) {
        if (idx >= path.size()) return;
        int step = path.get(idx);
        if (node instanceof DecisionTree.AndNode) {
            DecisionTree.AndNode a = (DecisionTree.AndNode) node;
            if (idx == path.size() - 1) {
                if (step == 0) a.leftChild = replacement;
                else a.rightChild = replacement;
            } else {
                DecisionTree.TreeNode next = (step == 0) ? a.leftChild : a.rightChild;
                replaceHelper(next, path, idx + 1, replacement);
            }
        } else if (node instanceof DecisionTree.OrNode) {
            DecisionTree.OrNode o = (DecisionTree.OrNode) node;
            if (idx == path.size() - 1) {
                if (step == 0) o.leftChild = replacement;
                else o.rightChild = replacement;
            } else {
                DecisionTree.TreeNode next = (step == 0) ? o.leftChild : o.rightChild;
                replaceHelper(next, path, idx + 1, replacement);
            }
        } else if (node instanceof DecisionTree.NotNode) {
            DecisionTree.NotNode n = (DecisionTree.NotNode) node;
            if (idx == path.size() - 1) {
                n.child = replacement;
            } else {
                replaceHelper(n.child, path, idx + 1, replacement);
            }
        }
    }

}
