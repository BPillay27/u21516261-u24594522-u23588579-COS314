import java.io.*;
import java.util.Scanner;

public class KnapsackInstance {
    private int totalItems;
    private double capacity;
    private KnapsackItem[] items;

    public KnapsackInstance(String file) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(file));
        totalItems = scan.nextInt();
        capacity = scan.nextDouble();
        items = new KnapsackItem[totalItems];
        for (int i = 0; i < totalItems; i++) {
            int weight = scan.nextInt();
            int value = scan.nextInt();
            items[i] = new KnapsackItem(weight, value);
        }
        scan.close();
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
            if (valid[i]) {
                totalWeight += items[i].getWeight();
            }
        }

        return totalWeight <= capacity;
    }

    
}