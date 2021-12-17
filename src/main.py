from data_loader import DataLoader
from model import CNN
import tensorflow as tf
import numpy as np
from java_interaction import HaloJavaInteraction

# Local Hyperparameter(s)
test_percentage = 0.1

def main():
    data_loader = DataLoader(data_location = 'generate_games/.data', batch_size = 512, test_percentage = test_percentage, seed = 42, is_training = True)
    test_loader = DataLoader(data_location = 'generate_games/.data', batch_size = 1024, test_percentage = test_percentage, seed = 42, is_training = False)
    cnn = CNN()

    # Train 
    train(cnn = cnn, data_loader=data_loader, test_loader = test_loader, epochs = 1)



# Train using custom training loop
def train(cnn = None, data_loader = None, test_loader = None, epochs = 100):
    if data_loader is None or cnn is None or test_loader is None:
        return

    def get_test_accuracy(test_loader = None):
        if test_loader is None:
            return
        
        # Get Test Accuracy
        step = 0

        # Accuracy Metrics
        total = 0
        correct = 0
        while test_loader.current_csv_index is not (len(test_loader)-1):
            # Load Batch
            _, (x_batch_test, y_batch_test)  = test_loader.get_next_batch()
            # Predict
            predictions = cnn.model.predict(x_batch_test)
            # Get Accuracy
            correct += np.sum(np.argmax(predictions, axis=1) == np.argmax(y_batch_test, axis=1))
            total += len(x_batch_test)

            step += 1
            if step % 100 == 0 and step!=0:
                print(f'CSV_Num = {test_loader.current_csv_index} / {len(test_loader)-1}, Data_Num = {test_loader.current_data_index} / {len(test_loader.current_data)-1}')
                
        test_loader.reset() # Artificial Reset to Make Sure
        return correct/total

    # Initial Accuracy
    print("Running Baseline Accuracy Test...")
    print(f'Accuracy: {round(get_test_accuracy(test_loader)*100, 2)}%') 

    loss_last_300 = []
    for epoch in range(epochs):
        print("\nStart of epoch %d" % (epoch,))

        ############# FROM TENSORFLOW DOCUMENTATION #############
        step = 0
        # Iterate over the batches of the dataset.
        while data_loader.current_csv_index is not (len(data_loader)-1):
            _, (x_batch_train, y_batch_train)  = data_loader.get_next_batch()
            # Open a GradientTape to record the operations run
            # during the forward pass, which enables auto-differentiation.
            with tf.GradientTape() as tape:

                # Run the forward pass of the layer.
                # The operations that the layer applies
                # to its inputs are going to be recorded
                # on the GradientTape.
                logits = cnn.model(x_batch_train)  # Logits for this minibatch

                # Compute the loss value for this minibatch.
                loss_value = cnn.loss(y_batch_train, logits)

            # Use the gradient tape to automatically retrieve
            # the gradients of the trainable variables with respect to the loss.
            grads = tape.gradient(loss_value, cnn.model.trainable_weights)

            # Run one step of gradient descent by updating
            # the value of the variables to minimize the loss.
            cnn.optimizer.apply_gradients(zip(grads, cnn.model.trainable_weights))

            # Log every 200 batches.
            loss_last_300.append(loss_value)
            if step % 300 == 0 and step!=0:
                print(f'Epoch = {epoch}, Average Loss Last 300 = {np.mean(np.array(loss_last_300))}, CSV_Num = {data_loader.current_csv_index} / {len(data_loader)-1}, Data_Num = {data_loader.current_data_index} / {len(data_loader.current_data)-1}')
                loss_last_300 = []
            if step % 6000 == 0 and step!=0:
                print(f'Accuracy: {round(get_test_accuracy(test_loader)*100, 2)}%')
                # Save Model
                cnn.save_model()
            step+=1
        
        data_loader.reset() # Artificial Reset to Make Sure
        print("End of epoch %d" % (epoch,))
        

def java_response():
    java_interaction = HaloJavaInteraction(poll_time_ms=1, model_name='model')

def test_position():
    cnn = CNN()
    cnn.load_model()
    board_string = "100000010000000000000000000000000000000000000000000000000000000001000010000000000000000000000000000000000000000000000000000000000010010000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000011111001000000000000011000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000100000000000000000000000000000000000000000000000000000000010000100000000000000000000000000000000000000000000000000000000000100100000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000001000000000001111011100000000"
    tensor = cnn.convert_string_to_tensor(board_string)
    tensor = np.array(tensor)
    tensor = tf.expand_dims(tensor, 0)
    print(tensor)
    prediction = cnn.model.predict(tf.convert_to_tensor(tensor))
    print(prediction)

if __name__ == "__main__":
    # main()
    # cnn = CNN()
    # cnn.load_model()
    # cnn.make_prediction_from_java() # Working properly. I believe java is almost working properly if not working properly already.
    # java_response()
    test_position()




