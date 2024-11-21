import java.io.*;
import java.util.*;

public class Hw3 {
    public static void main(String[] args) throws IOException {
        // Create a new instance of the class
        Hw3 hw3 = new Hw3();
        // Call the method to read the file and perform clustering
        hw3.readFile();
    }

    public void readFile() throws IOException {
        String filePath = "/Users/danielmontes/Documents/CSCD429/Homework 3/synthetic_control_data.txt";
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

    private List<double[]> initializeCentroids(List<double[]> data, int k) {
        List<double[]> centroids = new ArrayList<>();
        Random random = new Random();
        Set<Integer> chosenIndices = new HashSet<>();
        while (centroids.size() < k) {
            int index = random.nextInt(data.size());
            if (!chosenIndices.contains(index)) {
                centroids.add(data.get(index));
                chosenIndices.add(index);
            }
        }
        return centroids;
    }

    private List<Integer> kMeansClustering(List<double[]> data, List<double[]> centroids, int k) {
        List<Integer> assignments = new ArrayList<>(Collections.nCopies(data.size(), -1));
        boolean changed = true;

        while (changed) {
            changed = false;

            // Assign data points to the nearest centroid
            for (int i = 0; i < data.size(); i++) {
                double[] point = data.get(i);
                int closestCentroid = -1;
                double minDistance = Double.MAX_VALUE;

                for (int j = 0; j < centroids.size(); j++) {
                    double distance = euclideanDistance(point, centroids.get(j));
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCentroid = j;
                    }
                }

                if (assignments.get(i) != closestCentroid) {
                    assignments.set(i, closestCentroid);
                    changed = true;
                }
            }

            // Update centroids
            List<double[]> newCentroids = new ArrayList<>(Collections.nCopies(k, null));
            int[] counts = new int[k];

            for (int i = 0; i < data.size(); i++) {
                int cluster = assignments.get(i);
                if (newCentroids.get(cluster) == null) {
                    newCentroids.set(cluster, new double[data.get(0).length]);
                }
                double[] centroid = newCentroids.get(cluster);
                double[] point = data.get(i);

                for (int j = 0; j < point.length; j++) {
                    centroid[j] += point[j];
                }
                counts[cluster]++;
            }

            for (int i = 0; i < k; i++) {
                if (counts[i] > 0) {
                    double[] centroid = newCentroids.get(i);
                    for (int j = 0; j < centroid.length; j++) {
                        centroid[j] /= counts[i];
                    }
                } else {
                    newCentroids.set(i, centroids.get(i)); // Keep the old centroid if no points are assigned
                }
            }

            centroids = newCentroids;
        }

        return assignments;
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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/danielmontes/Documents/CSCD429/Homework 3/cluster_" + (i+1) + ".txt"))) {
                for (int j = 0; j < data.size(); j++) {
                    if (assignments.get(j) == i) {
                        writer.write(Arrays.toString(data.get(j)).replaceAll("[\\[\\],]", "") + "\n");
                    }
                }
            }
        }
    }
}