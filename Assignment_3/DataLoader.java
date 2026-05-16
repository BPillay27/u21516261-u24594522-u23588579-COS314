import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static class PatientRecord {
        public int targetClass; // 0 (no-recurrence) or 1 (recurrence) this is my accuracy
        public boolean[] features; 

        public PatientRecord(int targetClass, boolean[] features) {
            this.targetClass = targetClass;
            this.features = features;
        }
    }

    public static List<PatientRecord> loadAndEncodeData(String filePath) {
        List<PatientRecord> dataset = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(","); //The csv has 1,2 for content
                
                int targetClass = Integer.parseInt(values[0].trim());
                
                int[] rawFeatures = new int[9];
                for (int i = 0; i < 9; i++) {
                    rawFeatures[i] = Integer.parseInt(values[i + 1].trim());
                }
                
                boolean[] booleanFeatures = encodeToBoolean(rawFeatures);
                dataset.add(new PatientRecord(targetClass, booleanFeatures));
            }
        } catch (IOException e) {
            System.err.println("Error loading " + filePath + ": " + e.getMessage());
        }
        return dataset;
    }

    private static boolean[] encodeToBoolean(int[] rawFeatures) {

        int totalBits = 6 + 3 + 15 + 10 + 3 + 4 + 2 + 6 + 3; //Each integer gets its own bit in the logic circuit
        boolean[] encoded = new boolean[totalBits];
        
        int offset = 0;
        
        encoded[offset + rawFeatures[0]] = true; 
        offset += 6;
        encoded[offset + rawFeatures[1]] = true;
         offset += 3;
        encoded[offset + rawFeatures[2]] = true;
         offset += 15;
        encoded[offset + rawFeatures[3]] = true;
         offset += 10;
        encoded[offset + rawFeatures[4]] = true;
         offset += 3;
        encoded[offset + rawFeatures[5]] = true;
         offset += 4;
        encoded[offset + rawFeatures[6]] = true;
         offset += 2; 
        encoded[offset + rawFeatures[7]] = true;
         offset += 6;
        encoded[offset + rawFeatures[8]] = true;
        
        return encoded;
    }

    public static void printRep() {
        System.out.println("--- Boolean Feature Mapping (V 0-51) ---");
        System.out.println("V 0-5   : Age ranges (e.g., 20-29 up to 70-79)");
        System.out.println("V 6-8   : Menopause status (premeno, ge40, lt40)");
        System.out.println("V 9-23  : Tumor Size binned ranges");
        System.out.println("V 24-33 : Inv Nodes binned ranges");
        System.out.println("V 34-36 : Node-Caps (no, yes, ?)");
        System.out.println("V 37-40 : Degree of Malignancy (1, 2, 3)");
        System.out.println("V 41-42 : Breast (left, right)");
        System.out.println("V 43-48 : Breast Quad (left low, right up, left up, right low, central, ?)");
        System.out.println("V 49-51 : Irradiat (no, yes, ?)");
        System.out.println("----------------------------------------");
    }
}