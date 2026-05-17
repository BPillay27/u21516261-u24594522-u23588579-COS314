public class Metrics {

    public final double accuracy;
    public final double precision;
    public final double recall;
    public final double fMeasure;
    public final int    tp, fp, tn, fn;

    private Metrics(int tp, int fp, int tn, int fn) {
        this.tp = tp; this.fp = fp; this.tn = tn; this.fn = fn;

        int total    = tp + fp + tn + fn;
        this.accuracy  = total == 0 ? 0.0 : (double)(tp + tn) / total;
        this.precision = (tp + fp) == 0 ? 0.0 : (double) tp / (tp + fp);
        this.recall    = (tp + fn) == 0 ? 0.0 : (double) tp / (tp + fn);
        this.fMeasure  = (precision + recall) == 0.0 ? 0.0
                         : 2.0 * precision * recall / (precision + recall);
    }

    // Evaluate an individual against a loaded dataset.
    public static Metrics evaluate(Individual ind, DataLoader data) {
        int tp = 0, fp = 0, tn = 0, fn = 0;
        for (int i = 0; i < data.size; i++) {
            int predicted = ind.classify(data.features[i]);
            int actual    = data.labels[i];
            if      (predicted == 1 && actual == 1) tp++;
            else if (predicted == 1 && actual == 0) fp++;
            else if (predicted == 0 && actual == 0) tn++;
            else                                     fn++;
        }
        return new Metrics(tp, fp, tn, fn);
    }

// display the summary
    public String summary() {
        return String.format(
            "Acc=%.4f  F1=%.4f  Prec=%.4f  Rec=%.4f  [TP=%d FP=%d TN=%d FN=%d]",
            accuracy, fMeasure, precision, recall, tp, fp, tn, fn);
    }

    @Override
    public String toString() { return summary(); }
}
