import java.util.Random;

public class DecisionTree {

    public interface TreeNode {
        boolean evaluate(boolean[] features);
        String printTree();
        TreeNode cloneTree();
    }

    public static class FeatureNode implements TreeNode {
        public int featureIndex;

        public FeatureNode(int featureIndex) {
            this.featureIndex = featureIndex;
        }

        @Override
        public boolean evaluate(boolean[] features) {
            return features[featureIndex];
        }

        @Override
        public String printTree() {
            return "(V" + featureIndex +")";
        }

        @Override
        public TreeNode cloneTree() {
            return new FeatureNode(this.featureIndex);
        }
    }


    public static class AndNode implements TreeNode {
        public TreeNode leftChild;
        public TreeNode rightChild;

        public AndNode(TreeNode leftChild, TreeNode rightChild) {
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        @Override
        public boolean evaluate(boolean[] features) {
            boolean leftResult = leftChild.evaluate(features);
            boolean rightResult = rightChild.evaluate(features);
            return leftResult && rightResult; 
        }

        @Override
        public String printTree() {
            return "(" + leftChild.printTree() + " AND " + rightChild.printTree() + ")";
        }

        @Override
        public TreeNode cloneTree() {
            return new AndNode(leftChild.cloneTree(), rightChild.cloneTree());
        }
    }

    public static class OrNode implements TreeNode {
        public TreeNode leftChild;
        public TreeNode rightChild;

        public OrNode(TreeNode leftChild, TreeNode rightChild) {
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        @Override
        public boolean evaluate(boolean[] features) {
            boolean leftResult = leftChild.evaluate(features);
            boolean rightResult = rightChild.evaluate(features);
            return leftResult || rightResult;
        }

        @Override
        public String printTree() {
            return "(" + leftChild.printTree() + " OR " + rightChild.printTree() + ")";
        }

        @Override
        public TreeNode cloneTree() {
            return new OrNode(leftChild.cloneTree(), rightChild.cloneTree());
        }
    }

    public static class NotNode implements TreeNode {
        public TreeNode child;

        public NotNode(TreeNode child) {
            this.child = child;
        }

        @Override
        public boolean evaluate(boolean[] features) {
            return !child.evaluate(features);
        }

        @Override
        public String printTree() {
            return "NOT(" + child.printTree() + ")";
        }

        @Override
        public TreeNode cloneTree() {
            return new NotNode(child.cloneTree());
        }
    }

    //Generation of the trees methods 

    /*
     * FULL Method: 
     */
    public static TreeNode generateFullTree(int maxDepth, Random rand) {
        if (maxDepth <= 0) {
            return new FeatureNode(rand.nextInt(52)); 
        } 
        
        // Otherwise, MUST pick a AND, OR, NOT and the terminal is impossible because this is a full method
        int functionType = rand.nextInt(3); 
        
        if (functionType == 0) {
            return new AndNode(
                generateFullTree(maxDepth - 1, rand), 
                generateFullTree(maxDepth - 1, rand)
            );
        } else if (functionType == 1) {
            return new OrNode(
                generateFullTree(maxDepth - 1, rand), 
                generateFullTree(maxDepth - 1, rand)
            );
        } else {
            return new NotNode(
                generateFullTree(maxDepth - 1, rand)
            );
        }
    }

    /*
     * GROW Method
     */
    public static TreeNode generateGrowTree(int maxDepth, Random rand) {
        //Hit the bottom
        if (maxDepth <= 0) {
            return new FeatureNode(rand.nextInt(52)); 
        } 
        
        // Choose between 4 options: AND, OR, NOT, or a Terminal
        int choice = rand.nextInt(4); 
        
        if (choice == 0) {
            return new AndNode(
                generateGrowTree(maxDepth - 1, rand), 
                generateGrowTree(maxDepth - 1, rand)
            );
        } else if (choice == 1) {
            return new OrNode(
                generateGrowTree(maxDepth - 1, rand), 
                generateGrowTree(maxDepth - 1, rand)
            );
        } else if (choice == 2) {
            return new NotNode(
                generateGrowTree(maxDepth - 1, rand)
            );
        } else {
            return new FeatureNode(rand.nextInt(52));
        }
    }
}