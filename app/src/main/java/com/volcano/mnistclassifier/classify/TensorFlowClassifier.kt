package com.volcano.mnistclassifier.classify

import android.content.res.AssetManager
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.io.BufferedReader
import java.io.InputStreamReader

class TensorFlowClassifier : Classifier {
    private lateinit var tfHelper : TensorFlowInferenceInterface

    private lateinit var name : String
    private lateinit var inputName : String
    private lateinit var outputName : String

    private lateinit var labels : ArrayList<String>
    private lateinit var outputs : FloatArray
    private val outputNames = ArrayList<String>(1)

    companion object {
        private val THRESHOLD = 0.1
        private val INPUT_SIZE = 28L
        private val NUM_CLASSES = 10

        fun create(assetManager: AssetManager) : TensorFlowClassifier {
            // Initialize classifier
            val classifier = TensorFlowClassifier()

            // Set names
            classifier.name = "TensorFlow"
            classifier.inputName = "input"
            classifier.outputName = "output"

            // Read labels
            val labelFile = "labels.txt"
            classifier.labels = readLabels(assetManager, labelFile)

            // Set model path
            val modelPath = "optimized_mnist_convnet_tf.pb"
            classifier.tfHelper = TensorFlowInferenceInterface(assetManager, modelPath)
            classifier.outputs = kotlin.FloatArray(NUM_CLASSES)
            classifier.outputNames.add(classifier.outputName)

            return classifier
        }

        private fun readLabels(assetManager: AssetManager, labelFile: String) : ArrayList<String> {
            val isr = InputStreamReader(assetManager.open(labelFile))
            val br = BufferedReader(isr)

            val labels = ArrayList<String>()
            var line = br.readLine()
            while (line != null) {
                labels.add(line)
                line = br.readLine()
            }

            br.close()
            return labels
        }
    }

    override fun name() = name

    override fun recognize(pixels: FloatArray?) : Classification {
        tfHelper.feed(inputName, pixels, 1L, INPUT_SIZE, INPUT_SIZE, 1L)

        // Get output probability
        val probability = FloatArray(1)
        probability[0] = 1F
        tfHelper.feed("keep_prob", probability)

        // Get possible outputs
        tfHelper.run(outputNames.toTypedArray())

        // Get output
        tfHelper.fetch(outputName, outputs)

        val classification = Classification(0F, null)
        val size = outputs.size
        for (i in 0 until size) {
            if (outputs[i] > THRESHOLD && outputs[i] > classification.confidence) {
                classification.confidence = outputs[i]
                classification.label = labels[i]
            }
        }

        return classification
    }
}