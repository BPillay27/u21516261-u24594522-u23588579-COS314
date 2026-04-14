import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class KnapsackInstance {
    private int totalItems;
    private double capacity;
    private KnapsackItem[] items;

    public KnapsackInstance(String file) {
        try {
            // Make the scanner object and temporary list.
            Scanner scan = new Scanner(new File(file));
            scan.useLocale(Locale.US); // This is so the scanner read 0.928 as a double. Instead of needing 0,928
            ArrayList<KnapsackItem> temp = new ArrayList<KnapsackItem>();

            // Check for total items and throws an error if not there.
            if (scan.hasNextInt()) {
                totalItems = scan.nextInt();
            } else {
                scan.close();
                throw new IllegalArgumentException("Invalid total items");
            }

            // Check for capacity and throws an error if not there.
            if (scan.hasNextDouble()) {
                capacity = scan.nextDouble();
            } else {
                scan.close();
                throw new IllegalArgumentException("Invalid capacity");
            }

            // now loop through the rest of the file.
            while (scan.hasNextDouble()) {
                double value = scan.nextDouble();

                // Gets the weight and adds to the temp list, if invalid (value, no weight) it
                // throws an error
                if (scan.hasNextDouble()) {
                    double weight = scan.nextDouble();
                    temp.add(new KnapsackItem(value, weight));
                } else {
                    scan.close();
                    throw new IllegalArgumentException("Invalid entry. Missing weight");
                }
            }

            // The total items should match the items we have extracted from the file,
            // otherwise invalid.
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
        if (valid == null || items == null || valid.length != items.length) {
            return false;
        }

        double totalWeight = 0.0;
        for (int i = 0; i < valid.length; i++) {
            if (valid[i] == true) {
                totalWeight += items[i].getWeight();
            }
        }

        return totalWeight <= capacity;
    }
}