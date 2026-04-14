public class KnapsackItem {
    private double weight;
    private double value;

    public KnapsackItem(double value, double weight) {
        this.weight = weight;
        this.value = value;
    }

    public double getWeight() {
        return weight;
    }

    public double getValue() {
        return value;
    }
}