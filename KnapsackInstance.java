import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class KnapsackInstance {
    private int totalItems;
    private double capacity;
    private KnapsackItem[] items;

    public KnapsackInstance(String file) {
        try {
            Scanner scan = new Scanner(new File(file));
            ArrayList<KnapsackItem> temp = new ArrayList<KnapsackItem>();

            if (scan.hasNextInt()) {
                totalItems = scan.nextInt();
            } else {
                scan.close();
                throw new IllegalArgumentException("Invalid total items");
            }

            if (scan.hasNextDouble()) {
                capacity = scan.nextDouble();
            } else {
                scan.close();
                throw new IllegalArgumentException("Invalid capacity");
            }

            while (scan.hasNextDouble()) {
                double value = scan.nextDouble();

                if (scan.hasNextDouble()) {
                    double weight = scan.nextDouble();
                    temp.add(new KnapsackItem(value, weight));
                } else {
                    scan.close();
                    throw new IllegalArgumentException("Invalid entry. Missing weight");
                }
            }

            if (temp.size() != totalItems) {
                scan.close();
                throw new IllegalArgumentException("Expected " + totalItems + " items, but found " + temp.size());
            }
            items = temp.toArray(new KnapsackItem[0]);

            scan.close();
        } catch (Exception e) {
            totalItems = 0;
            capacity = 0;
            items = new KnapsackItem[0];
            System.out.println("Encountered a problem while reading the file. File format may be incorrect. Error: "
                    + e.getMessage());
        }
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getCapacity() {
        return capacity;
    }

    public KnapsackItem[] getItems() {
        return items;
    }

    public boolean isValid(boolean[] valid) {
        double totalWeight = 0.0;
        for (int i = 0; i < valid.length; i++) {
            if (valid[i] == true) {
                totalWeight += items[i].getWeight();
            }
        }

        return totalWeight <= capacity;
    }
}