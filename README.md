# MNISTClassifier
This app detect the number you draw on a cnvas and detect what number you've drawn. It uses TensorFlow to classify the bitmap created by canvas.

## MNIST Classifier on TensorFlow 
MNIST For ML Beginners https://www.tensorflow.org/versions/r0.10/tutorials/mnist/beginners/index.html
Deep MNIST for Experts https://www.tensorflow.org/versions/r0.10/tutorials/mnist/pros/index.html


## Train Model
The script for traning model is located at https://github.com/AliMehrpour/MNISTClassifier/blob/master/model/mnist_convnet_keras.ipynb. After training complete, the output file `.pb` should be moved to assets folder of android app. 

## Train + Test + Inference Processes
Training and test processed should be done on server (or laptop) and the exported model which contains weight and biases should be copied to mobile app.

The android app will fed the input data (28x28 pixels) into the trained model and get back the result. This has been done via [TensorFlowInferenceInterface](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/contrib/android/java/org/tensorflow/contrib/android/TensorFlowInferenceInterface.java) in Android
