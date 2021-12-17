import threading
import time
import pandas as pd
from model import CNN

class HaloJavaInteraction:
    
    java_to_python = 'communication_gateway/java_to_python.txt'
    python_to_java = 'communication_gateway/python_to_java.txt'


    def __init__(self, poll_time_ms = 1, model_name = 'model'):
        # Member Variables
        self.poll_time_ms = poll_time_ms
        self.model_name = model_name
        # CNN
        self.cnn = CNN(file_name = self.model_name)
        self.cnn.load_model()

        # Start Response Thread
        threading.Thread(target=self.responder).start()

    # Interacts with File and acts on it with model methods
    def responder(self):
        while True:
            # Sleep
            time.sleep(self.poll_time_ms)
            # Check if there is a message
            if self.java_to_python_has_message():
                # Takes Care of Java to Python Interaction
                self.cnn.make_prediction_from_java()


    # Check if Java to Python has Message
    def java_to_python_has_message(self):
        # Read file as csv 
        try:
            df = pd.read_csv(self.java_to_python, header=None)
            # If there is a message
            return True
        except pd.errors.EmptyDataError:
            return False
        