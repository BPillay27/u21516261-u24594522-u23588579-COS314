import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GP_Engine {

    private static final int    POP_SIZE        = 200;
    private static final int    MAX_GENERATIONS = 100;
    private static final int    MIN_INIT_DEPTH  = 2;
    private static final int    MAX_INIT_DEPTH  = 6;
    private static final int    MAX_DEPTH       = 8;
    private static final int    TOURNAMENT_SIZE = 5;
    private static final double CROSSOVER_RATE  = 0.80;
    private static final double MUTATION_RATE   = 0.20;

    private final Random     rng;
    private final ArithmeticDataLoader trainData;
    private final boolean    verbose;
    private Individual[]     population;
    private Individual       bestIndividual;

    public GP_Engine(long seed, ArithmeticDataLoader trainData, boolean verbose) {
        this.rng       = new Random(seed);
        this.trainData = trainData;
        this.verbose   = verbose;
    }

    public Individual run() {
        // Initialise population with ramped half-and-half
        population = new Individual[POP_SIZE];
        for (int i = 0; i < POP_SIZE; i++) {
            population[i] = Individual.rampedHalfAndHalf(
                    rng, MIN_INIT_DEPTH, MAX_INIT_DEPTH, i, POP_SIZE);
        }

        evaluateAll();
        bestIndividual = findBest().deepCopy();
        printGeneration(0);

        for (int gen = 1; gen <= MAX_GENERATIONS; gen++) {
            Individual[] newPop = new Individual[POP_SIZE];

            newPop[0] = bestIndividual.deepCopy(); // elitism

            for (int i = 1; i < POP_SIZE; i++) {
                Individual offspring;

                if (rng.nextDouble() < CROSSOVER_RATE) {
                    offspring = crossover(tournamentSelect(), tournamentSelect());
                } else {
                    offspring = tournamentSelect().deepCopy();
                }

                if (rng.nextDouble() < MUTATION_RATE) {
                    offspring = mutate(offspring);
                }

                newPop[i] = offspring;
            }

            population = newPop;
            evaluateAll();

            Individual genBest = findBest();
            if (genBest.fitness > bestIndividual.fitness) {
                bestIndividual = genBest.deepCopy();
            }

            printGeneration(gen);
        }

        return bestIndividual;
    }

    private void evaluateAll() {
        for (Individual ind : population) {
            ind.fitness = Metrics.evaluate(ind, trainData).accuracy;
        }
    }

    private Individual findBest() {
        Individual best = population[0];
        for (Individual ind : population) {
            if (ind.fitness > best.fitness) best = ind;
        }
        return best;
    }


    private Individual tournamentSelect() {
        Individual best = population[rng.nextInt(POP_SIZE)];
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            Individual candidate = population[rng.nextInt(POP_SIZE)];
            if (candidate.fitness > best.fitness) best = candidate;
        }
        return best;
    }

    private Individual crossover(Individual p1, Individual p2) {
        Individual offspring = p1.deepCopy();

        List<Node> nodes1 = collectNodes(offspring.root);
        List<Node> nodes2 = collectNodes(p2.root);

        Node point = nodes1.get(rng.nextInt(nodes1.size()));
        Node donor = nodes2.get(rng.nextInt(nodes2.size())).deepCopy();

        // Swap subtree in place by overwriting the crossover point's contents
        overwrite(point, donor);

        // If max depth is exceeded, fall back to p1
        if (offspring.depth() > MAX_DEPTH) return p1.deepCopy();
        return offspring;
    }

   
    private static void overwrite(Node dst, Node src) {
        dst.type        = src.type;
        dst.operator    = src.operator;
        dst.left        = src.left;
        dst.right       = src.right;
        dst.featureIndex = src.featureIndex;
        dst.constant    = src.constant;
    }

    private Individual mutate(Individual ind) {
        Individual mutant = ind.deepCopy();
        List<Node> nodes  = collectNodes(mutant.root);
        Node target       = nodes.get(rng.nextInt(nodes.size()));

        if (target.isFunction()) {
            // Replace operator with a different one
            char newOp;
            do { newOp = Node.OPERATORS[rng.nextInt(Node.OPERATORS.length)]; }
            while (newOp == target.operator);
            target.operator = newOp;
        } else {
            // Replace terminal with a new random terminal
            Node fresh = randomTerminal(rng);
            target.type        = fresh.type;
            target.featureIndex = fresh.featureIndex;
            target.constant    = fresh.constant;
        }

        return mutant;
    }


    private static List<Node> collectNodes(Node node) {
        List<Node> list = new ArrayList<>();
        collectNodes(node, list);
        return list;
    }

    private static void collectNodes(Node node, List<Node> list) {
        list.add(node);
        if (node.isFunction()) {
            collectNodes(node.left, list);
            collectNodes(node.right, list);
        }
    }

    private static Node randomTerminal(Random rng) {
        if (rng.nextDouble() < 0.7) {
            return new Node(rng.nextInt(Node.NUM_FEATURES));
        }
        return new Node((rng.nextDouble() * 10.0) - 5.0);
    }

    private void printGeneration(int gen) {
        if (!verbose) return;
        Metrics m = Metrics.evaluate(bestIndividual, trainData);
        System.out.printf("Gen %3d | %s%n", gen, m.summary());
        System.out.println("         " + bestIndividual);
    }
}
