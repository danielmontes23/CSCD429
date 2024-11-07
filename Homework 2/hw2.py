import csv

# Load data from CSV file
def load_data(filename):
    data = []
    with open(filename, 'r') as file:
        reader = csv.reader(file, quotechar='"')
        for row in reader:
            data.append(row)
    return data

# Calculate Hamming distance
def hamming_distance(gene1, gene2):
    return sum(1 for x, y in zip(gene1, gene2) if x != y)

# K-NN Algorithm (using Hamming distance)
def knn_predict(test_data, train_data, k):
    predictions = []
    for test_gene in test_data:
        distances = []
        for train_gene in train_data:
            # Calculate Hamming distance, exclude Localization (last element) for distance
            dist = hamming_distance(test_gene[:-1], train_gene[:-1])
            distances.append((dist, train_gene[-1]))  # Include localization for prediction

        # Sort by distance and select top k
        distances.sort(key=lambda x: x[0])
        k_nearest = [loc for _, loc in distances[:k]]

        # Majority vote without Counter
        vote_count = {}
        for loc in k_nearest:
            if loc in vote_count:
                vote_count[loc] += 1
            else:
                vote_count[loc] = 1
        predicted_loc = max(vote_count, key=vote_count.get)
        predictions.append(predicted_loc)
    return predictions

# Accuracy function
def accuracy(predictions, key):
    actual_value = {row["GeneID"]: row["Localization"] for row in key}
    correct = sum(1 for gene_id, localization in predictions if localization == actual_value.get(gene_id))
    return (correct / len(predictions)) * 100

# Load predictions from results.txt
def load_predictions(filename):
    with open(filename, 'r') as file:
        return [(row[0], row[1]) for row in csv.reader(file, quotechar='"')]

# Load true labels from keys.txt
def load_key(filename):
    with open(filename, 'r') as file:
        return [{"GeneID": row[0], "Localization": row[1]} for row in csv.reader(file, quotechar='"')]

# Main K-NN Classifier Class
class GeneKNNClassifier:
    def __init__(self, k=8):
        self.k = k  # Number of nearest neighbors
        self.training_data = []

    def fit(self, training_file):
        # Load the training data using the load_data function
        self.training_data = load_data(training_file)
        return True

    def predict(self, test_instance):
        # Using knn_predict for prediction
        return knn_predict([test_instance], self.training_data, self.k)[0]

def main():
    try:
        # Initialize classifier and fit it to the training data
        knn = GeneKNNClassifier(k=101)
        if not knn.fit('gene_files/Genes_relation.data'):
            print("Error, now Exiting.")
            return

        # Load test data
        test_data = load_data('gene_files/Genes_relation.test')
        if not test_data:
            print("Error loading test data, now Exiting.")
            return

        # Make predictions
        predictions = []
        for test_instance in test_data:
            gene_id = test_instance[0]
            prediction = knn.predict(test_instance)
            predictions.append((gene_id, prediction))

        # Save predictions to results.txt
        with open('results.txt', 'w') as file:
            writer = csv.writer(file)
            for gene_id, loc in predictions:
                writer.writerow([gene_id, loc])

        print("Predictions were written into results.txt file")

        # Load keys (true labels) from keys.txt
        key = load_key('gene_files/keys.txt')

        # Calculate accuracy
        print(f"Accuracy: {accuracy(predictions, key):.2f}%")

    except Exception as e:
        print(f"An error has occurred: {e}")


if __name__ == "__main__":
    main()

