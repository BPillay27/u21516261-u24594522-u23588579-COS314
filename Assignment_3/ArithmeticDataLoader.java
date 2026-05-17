import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArithmeticDataLoader {

    public final double[][] features;
    public final int[]      labels;
    public final int        size;

    private ArithmeticDataLoader(double[][] features, int[] labels) {
        this.features = features;
        this.labels   = labels;
        this.size     = labels.length;
    }

    public static ArithmeticDataLoader load(String filepath) throws IOException {
        List<double[]> featureList = new ArrayList<>();
        List<Integer>  labelList  = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                int label = Integer.parseInt(parts[0].trim());
                double[] row = new double[Node.NUM_FEATURES];
                for (int i = 0; i < Node.NUM_FEATURES; i++) {
                    row[i] = Double.parseDouble(parts[i + 1].trim());
                }
                labelList.add(label);
                featureList.add(row);
            }
        }

        double[][] features = featureList.toArray(new double[0][]);
        int[]      labels   = new int[labelList.size()];
        for (int i = 0; i < labels.length; i++) labels[i] = labelList.get(i);

        return new ArithmeticDataLoader(features, labels);
    }

    // Counts instances of a given class label.
    public int countClass(int classLabel) {
        int count = 0;
        for (int l : labels) if (l == classLabel) count++;
        return count;
    }

    @Override
    public String toString() {
        return String.format("Dataset: %d instances  (class 0: %d, class 1: %d)",
                size, countClass(0), countClass(1));
    }
}
