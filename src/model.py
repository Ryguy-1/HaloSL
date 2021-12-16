import numpy as np
import pandas as pd
# Tensorflow
import tensorflow as tf
import tensorflow.keras.models as models
import tensorflow.keras.layers as layers
import tensorflow.keras.losses as losses
import tensorflow.keras.optimizers as optimizers
import tensorflow.keras.utils as utils

class CNN:

    # Random -> Base = 50.89% -> 66.89% max -> 65 after roughly 20% of data
    # Saved: 'model' = 66.7% accurate -> Location: Blue Desktop

    java_to_python = 'communication_gateway/java_to_python.txt'
    python_to_java = 'communication_gateway/python_to_java.txt'

    def __init__(self, file_name = 'model', num_classes=2):
        self.input_shape = (12, 8, 8)
        self.num_classes = num_classes
        self.file_name = file_name

        self.model = models.Sequential()

        # Shape = (12, 8, 8)
        self.model.add(layers.Conv2D(filters=32, kernel_size=3, strides=(1, 1), input_shape = self.input_shape, data_format='channels_first', activation = 'relu'))
        self.model.add(layers.BatchNormalization())
        self.model.add(layers.Dropout(0.5))

        # Shape = (32, 6, 6)
        self.model.add(layers.Conv2D(filters=96, kernel_size=3, strides=(1, 1), data_format='channels_first', activation='relu'))
        self.model.add(layers.BatchNormalization())
        self.model.add(layers.Dropout(0.5))

        # Shape = (96, 6, 6)
        self.model.add(layers.Flatten())

        # Shape = 96 * 6 * 6 = (3,456,)
        self.model.add(layers.Dense(units=2048, activation = 'relu'))
        self.model.add(layers.BatchNormalization())
        self.model.add(layers.Dropout(0.4))

        # Shape = (2048,)
        self.model.add(layers.Dense(units=512, activation = 'relu'))
        self.model.add(layers.BatchNormalization())
        self.model.add(layers.Dropout(0.3))

        # Shape = (512,)
        self.model.add(layers.Dense(units=32, activation = 'relu'))

        # Shape = (32,)
        self.model.add(layers.Dense(units=self.num_classes, activation = 'softmax'))


        # <--- Model Compile --->
        self.loss = losses.CategoricalCrossentropy()
        self.optimizer = optimizers.Adam(learning_rate=0.00001)

        self.model.compile(
            loss = self.loss,
            optimizer = self.optimizer,
            metrics = ['accuracy']
        )

    def save_model(self):
        self.model.save(filepath = f'src/.models/{self.file_name}')

    def load_model(self):
        self.model = models.load_model(filepath = f'src/.models/{self.file_name}')

    def make_prediction_from_java(self):
        data, toMove = self.read_java_to_python_as_csv() # Returns Tensor Objects
        data = tf.convert_to_tensor(data, dtype=tf.float32)

        # <--- Make Prediction --->
        prediction = self.model.predict(data)
        # Capital is index 0 of prediction, lower case is index 1 (CHANGE THIS LINE IF WE ARE PREDICTING FOR MULTIPLE LABELS)
        index = 0
        if toMove[0] == 'l':
            index = 1
        values = []
        for pred in prediction:
            values.append(pred[index])
        # Find Index of max value using numpy
        max_index = np.argmax(values)
        # Write the index to python_to_java.txt
        with open(self.python_to_java, 'w') as f:
            f.write(str(max_index))
        # Erase Java to python file
        with open(self.java_to_python, 'w') as f:
            f.write('')


    # Returns a numpy array of shape (moves, 12, 8, 8)
    def read_java_to_python_as_csv(self):
        csv = pd.read_csv(self.java_to_python, header=None)
        # Get First Column
        moves = csv.iloc[:, 0]
        # Get Second Column
        toMove = csv.iloc[:, 1]
        # Convert to Tensor
        moves = [self.convert_string_to_tensor(move) for move in moves]
        moves = np.array(moves)
        toMove = np.array(toMove)
        print(moves.shape)
        print(len(moves))
        return moves, toMove


        # (Helper) Convert String to Tensor
    # Takes String from CSV and Converts to numpy array shape = (12, 8, 8)
    def convert_string_to_tensor(self, string):
        bits = list(string)
        bits = [int(letter) for letter in bits]
        bits = np.array(bits).reshape(12, 8, 8)
        return bits