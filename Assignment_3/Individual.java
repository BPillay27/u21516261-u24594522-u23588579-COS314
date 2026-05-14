import java.util.Random;

public class Individual {

    public Node   root;
    public double fitness; // classification accuracy on training set [0.0, 1.0]


    public Individual(Node root) {
        this.root    = root;
        this.fitness = 0.0;
    }


    public double evaluate(double[] features) {
        return root.evaluate(features);
    }

    /** Classification rule: output > 0 → recurrence (1), else no-recurrence (0). */
    public int classify(double[] features) {
        return evaluate(features) > 0.0 ? 1 : 0;
    }

    public int  depth()    { return root.depth(); }
    public int  size()     { return root.size();  }

    public Individual deepCopy() {
        Individual copy = new Individual(root.deepCopy());
        copy.fitness = this.fitness;
        return copy;
    }

    @Override
    public String toString() { return root.toString(); }

    // ── tree generation ───────────────────────────────────────────────────────

   //Full method strat
    public static Individual generateFull(Random rng, int maxDepth) {
        return new Individual(buildFull(rng, maxDepth));
    }

    //Grow method strat
    public static Individual generateGrow(Random rng, int maxDepth) {
        return new Individual(buildGrow(rng, maxDepth));
    }

    //Ramped half-and-half method strat
    public static Individual rampedHalfAndHalf(Random rng, int minDepth,
                                                int maxDepth, int index,
                                                int populationSize) {
        int levels  = maxDepth - minDepth + 1;
        int slot    = index % levels;          // which depth level
        int depth   = minDepth + slot;
        boolean full = (index / levels) % 2 == 0; // alternate full / grow
        return full ? generateFull(rng, depth) : generateGrow(rng, depth);
    }


    private static Node buildFull(Random rng, int depth) {
        if (depth == 0) return randomTerminal(rng);
        char op    = Node.OPERATORS[rng.nextInt(Node.OPERATORS.length)];
        Node left  = buildFull(rng, depth - 1);
        Node right = buildFull(rng, depth - 1);
        return new Node(op, left, right);
    }

    private static Node buildGrow(Random rng, int depth) {
        if (depth == 0) return randomTerminal(rng);
        // at non-leaf positions: 50% chance of terminal, 50% of function
        boolean makeTerminal = rng.nextBoolean();
        if (makeTerminal) return randomTerminal(rng);
        char op    = Node.OPERATORS[rng.nextInt(Node.OPERATORS.length)];
        Node left  = buildGrow(rng, depth - 1);
        Node right = buildGrow(rng, depth - 1);
        return new Node(op, left, right);
    }

    private static Node randomTerminal(Random rng) {
        if (rng.nextDouble() < 0.7) {
            return new Node(rng.nextInt(Node.NUM_FEATURES));
        } else {
            double val = (rng.nextDouble() * 10.0) - 5.0; // range [-5, 5]
            return new Node(val);
        }
    }
}
