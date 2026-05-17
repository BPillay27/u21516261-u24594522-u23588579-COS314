public class Node {

    public enum Type { FUNCTION, VARIABLE, CONSTANT }

    public static final String[] FEATURE_NAMES = {
        "age", "menopause", "tumor_size", "inv_nodes",
        "node_caps", "deg_malig", "breast", "breast_quad", "irradiat"
    };

    public static final char[] OPERATORS   = { '+', '-', '*', '/' };
    public static final int    NUM_FEATURES = 9;


    public Type   type;
    public char   operator;     // FUNCTION nodes only
    public Node   left, right;  // FUNCTION nodes only
    public int    featureIndex; // VARIABLE nodes only
    public double constant;     // CONSTANT nodes only

    public Node(char operator, Node left, Node right) {
        this.type     = Type.FUNCTION;
        this.operator = operator;
        this.left     = left;
        this.right    = right;
    }

    public Node(int featureIndex) {
        this.type         = Type.VARIABLE;
        this.featureIndex = featureIndex;
    }

    public Node(double constant) {
        this.type     = Type.CONSTANT;
        this.constant = constant;
    }


    public boolean isFunction() { return type == Type.FUNCTION; }
    public boolean isTerminal() { return type != Type.FUNCTION; }


    public double evaluate(double[] features) {
        switch (type) {
            case VARIABLE: return features[featureIndex];
            case CONSTANT: return constant;
            case FUNCTION:
                double l = left.evaluate(features);
                double r = right.evaluate(features);
                switch (operator) {
                    case '+': return l + r;
                    case '-': return l - r;
                    case '*': return l * r;
                    case '/': return (r == 0.0) ? 1.0 : l / r; // protected division
                }
        }
        return 0.0;
    }


    public int depth() {
        if (isTerminal()) return 0;
        return 1 + Math.max(left.depth(), right.depth());
    }

    // Total number of nodes in this subtree. 
    public int size() {
        if (isTerminal()) return 1;
        return 1 + left.size() + right.size();
    }

    public Node deepCopy() {
        switch (type) {
            case VARIABLE: return new Node(featureIndex);
            case CONSTANT: return new Node(constant);
            case FUNCTION: return new Node(operator, left.deepCopy(), right.deepCopy());
        }
        return null;
    }


    @Override
    public String toString() {
        switch (type) {
            case VARIABLE: return FEATURE_NAMES[featureIndex];
            case CONSTANT: return String.format("%.3f", constant);
            case FUNCTION: return "(" + left + " " + operator + " " + right + ")";
        }
        return "";
    }
}
