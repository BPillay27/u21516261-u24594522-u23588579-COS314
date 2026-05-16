
import java.util.List;
import java.util.Random;

public class OurTest {
	public static void main(String[] args) {
		String trainPath = "Breast_train.csv";
		String testPath = "Breast_test.csv";

		List<DataLoader.PatientRecord> train = DataLoader.loadAndEncodeData(trainPath);
		List<DataLoader.PatientRecord> test = DataLoader.loadAndEncodeData(testPath);

		int seed = 46;
		Random rand = new Random(seed);

		int populationSize = 200;
		int initialTreeDepth = 13;
		int maxOffspringDepth = 54;
		int tournamentSize = 10;
		double crossoverRate = 0.75;
		double mutationRate = 0.25;
		int mutationOffspringDepth = 13;
		int maxGenerations = 100;

		String outFile = "results_seed_" + seed + ".txt";
		DecisionTree.TreeNode best = LogicalGP.runGPWithLogging(
			train, test, populationSize, initialTreeDepth, maxOffspringDepth,
			tournamentSize, crossoverRate, mutationRate, mutationOffspringDepth,
			maxGenerations, outFile, rand
		);

		double trainAcc = evaluateAccuracy(best, train);
		double testAcc = evaluateAccuracy(best, test);

		System.out.println("Seed: " + seed);
		System.out.println("Train accuracy: " + trainAcc);
		System.out.println("Test accuracy: " + testAcc);
		System.out.println("Best Tree: " + best.printTree());
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
}

