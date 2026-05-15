import java.util.Random;
import java.util.List;

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

    /*
    * Flattens the tree  structure
    */
    public static void collectNodes(TreeNode root, List<TreeNode> nodeList) {
        if (root == null) return;
        nodeList.add(root);
        if (root instanceof AndNode) {
            collectNodes(((AndNode) root).leftChild, nodeList);
            collectNodes(((AndNode) root).rightChild, nodeList);
        } else if (root instanceof OrNode) {
            collectNodes(((OrNode) root).leftChild, nodeList);
            collectNodes(((OrNode) root).rightChild, nodeList);
        } else if (root instanceof NotNode) {
            collectNodes(((NotNode) root).child, nodeList);
        }
    }


    /**
     * Prunes a tree so it does not exceed the provided maximum offspring depth.
     * When a subtree would exceed `maxOffspringDepth`, it is replaced with a
     * terminal `FeatureNode` chosen at random.
     *
     * @param root the tree to prune
     * @param maxOffspringDepth maximum allowed depth for the pruned tree
     * @param rand source of randomness for creating replacement terminals
     * @return a pruned clone of the input tree
     */
    public static TreeNode pruneTree(TreeNode root, int maxOffspringDepth, Random rand) {
        if (root == null) return null;
        return pruneByDepth(root, maxOffspringDepth, rand, 0);
    }

    private static TreeNode pruneByDepth(TreeNode node, int maxDepth, Random rand, int depth) {
        if (node == null) return null;

        if (depth > maxDepth) {
            return new FeatureNode(rand.nextInt(52));
        }

        if (node instanceof AndNode) {
            AndNode a = (AndNode) node;
            return new AndNode(
                pruneByDepth(a.leftChild, maxDepth, rand, depth + 1),
                pruneByDepth(a.rightChild, maxDepth, rand, depth + 1)
            );
        } else if (node instanceof OrNode) {
            OrNode o = (OrNode) node;
            return new OrNode(
                pruneByDepth(o.leftChild, maxDepth, rand, depth + 1),
                pruneByDepth(o.rightChild, maxDepth, rand, depth + 1)
            );
        } else if (node instanceof NotNode) {
            NotNode n = (NotNode) node;
            return new NotNode(pruneByDepth(n.child, maxDepth, rand, depth + 1));
        } else {
            return node.cloneTree(); // FeatureNode or unknown terminal
        }
    }
}