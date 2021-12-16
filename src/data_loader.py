import glob
import numpy as np
import pandas as pd
import tensorflow as tf

class DataLoader:

    # Pipeline:
    # 1. Choose Next CSV from csv_locations
    # 2. Load CSV into array of tensors and labels (load_csv_info)
    # 3. Zip and shuffle (shuffle_loaded_data)
    # 4. Get Next Batch (get_next_batch)

    def __init__(self, data_location = 'generate_games/.data', batch_size = 32, test_percentage = 0.3, seed = 42, is_training = True):
        # Member Variables
        self.data_location = data_location
        self.batch_size = batch_size
        self.test_percentage = test_percentage
        self.seed = seed; np.random.seed(self.seed)

        # Load CSV Locations / Train Test Split
        self.csv_locations = glob.glob(self.data_location+"/*")[:]
        if is_training:
            self.csv_locations = self.csv_locations[:int(len(self.csv_locations)*(1-self.test_percentage))]
        else:
            self.csv_locations = self.csv_locations[int(len(self.csv_locations)*(1-self.test_percentage)):]

        # <-- Current Variables -->
        self.current_csv_index = 0 # csv_locations
        self.current_data_index = 0 # index within data, labels
        self.current_data = None # data within csv loaded
        self.current_labels = None # labels within csv loaded

        # Initializer
        self.load_next_csv(is_initial_load=True)

    def __len__(self):
        return len(self.csv_locations)

    # 2. Load CSV into array of tensors and labels (load_csv_info)
    def load_csv_info(self, csv_file_path):
        # Load CSV
        df = pd.read_csv(csv_file_path, header=None, encoding='utf-8')

        # Get Labels
        labels = df.iloc[:, 1].values
        for i in range(len(labels)):
            if labels[i] == 'c':
                labels[i] = np.array([1, 0]) 
            elif labels[i] == 'l':
                labels[i] = np.array([0, 1])
            else:
                print("ERROR: Label not correct (not 'l' or 'c')")
        labels = np.array(labels)

        # Get Data (Convert to tensor)
        data = df.iloc[:, 0].values
        for i in range(len(data)):
            data[i] = self.convert_string_to_tensor(data[i])
            if data[i].shape != (12, 8, 8):
                print("ERROR: Data shape not correct")
        data = np.array(data)
        
        return data, labels

    # 3. Zip and shuffle (shuffle_loaded_data)
    def shuffle_loaded_data(self, data, labels):
        # Shuffle Data
        zipped_data_labels = list(zip(data, labels))
        np.random.shuffle(zipped_data_labels)
        data, labels = zip(*zipped_data_labels)
        data = np.array(data)
        labels = np.array(labels)
        return data, labels


    # 4. Get Next Batch (get_next_batch)
    def get_next_batch(self):
        # See If Still Room In CSV
        if (self.current_data_index + self.batch_size) > len(self.current_data):
            self.load_next_csv()
        
        # Get Batches Out of Current, Properly Loaded, Data
        data_batch = np.array(self.current_data[self.current_data_index : self.current_data_index + self.batch_size])
        label_batch = np.array(self.current_labels[self.current_data_index : self.current_data_index + self.batch_size])

        # Increase Current Data Index
        self.current_data_index += self.batch_size


        return self.current_csv_index, (tf.convert_to_tensor(data_batch, dtype=tf.float32), tf.convert_to_tensor(label_batch, dtype=tf.int32))


    # (Helper) Load Next CSV
    def load_next_csv(self, is_initial_load = False):
        # Update Current Indices
        self.current_data_index = 0
        # If not the initial load, go to next csv
        if not is_initial_load:
            self.current_csv_index += 1
        # If at end of csv_locations, reset to 0
        if self.current_csv_index >= len(self.csv_locations):
            self.current_csv_index = 0
        # Data and Labels from CSV
        data, labels = self.load_csv_info(self.csv_locations[self.current_csv_index])
        # Shuffle Data and Labels and set as Current Data and Labels
        self.current_data, self.current_labels = self.shuffle_loaded_data(data, labels)

    # (Helper) Convert String to Tensor
    # Takes String from CSV and Converts to numpy array shape = (12, 8, 8)
    def convert_string_to_tensor(self, string):
        bits = list(string)
        bits = [int(letter) for letter in bits]
        bits = np.array(bits).reshape(12, 8, 8)
        return bits


    def reset(self, shuffle_csvs = True):
        self.current_csv_index = 0
        self.current_data_index = 0
        self.current_data = None
        self.current_labels = None 
        if shuffle_csvs:
            np.random.shuffle(self.csv_locations)
        self.load_next_csv(is_initial_load=True)