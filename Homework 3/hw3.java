import java.io.*;
import java.util.*;

public class Homework3 {
    public static void main(String[] args) throws IOException {
        // Create a new instance of the class
        Homework3 hw3 = new Homework3();
        // Call the method to read the file and perform clustering
        hw3.readFile();
    }

    public void readFile() throws IOException {
        String filePath = "/workspaces/CSCD429/Homework 3/synthetic_control_data.txt";
        List<double[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");
                double[] row = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
                data.add(row);
            }
        }

        int k = 6; // Number of clusters
        List<double[]> centroids = initializeCentroids(data, k);
        List<Integer> assignments = kMeansClustering(data, centroids, k);

        saveClusters(data, assignments, k);
    }


    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private void saveClusters(List<double[]> data, List<Integer> assignments, int k) throws IOException {
        for (int i = 0; i < k; i++) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("/workspaces/CSCD429/Homework 3/cluster_" + i + ".txt"))) {
                for (int j = 0; j < data.size(); j++) {
                    if (assignments.get(j) == i) {
                        writer.write(Arrays.toString(data.get(j)).replaceAll("[\\[\\],]", "") + "\n");
                    }
                }
            }
        }
    }
}