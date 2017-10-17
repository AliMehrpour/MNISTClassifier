package com.volcano.mnistclassifier

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import com.volcano.mnistclassifier.classify.Classifier
import com.volcano.mnistclassifier.classify.TensorFlowClassifier
import com.volcano.mnistclassifier.view.DrawModel
import com.volcano.mnistclassifier.view.DrawView

class MainActivity : AppCompatActivity() {
    private val PIXEL_WIDTH = 28

    private lateinit var model : DrawModel
    private lateinit var classifier : Classifier

    private lateinit var drawView : DrawView
    private lateinit var clearButton : Button
    private lateinit var detectButton : Button
    private lateinit var resultText : TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create classifier
        classifier = TensorFlowClassifier.create(assets)

        // Create model
        model = DrawModel(PIXEL_WIDTH, PIXEL_WIDTH)

        // Finds views
        drawView = findViewById(R.id.draw_view)
        drawView.setModel(model)

        resultText = findViewById(R.id.text_result)

        clearButton = findViewById(R.id.button_clear)
        clearButton.setOnClickListener({
            drawView.reset()
        })

        detectButton = findViewById(R.id.button_detect)
        detectButton.setOnClickListener({
            val pixels = drawView.getPixels()

            val classification = classifier.recognize(pixels)
            if (!TextUtils.isEmpty(classification.label)) {
                resultText.text = "${classification.label} with ${classification.confidence * 100}% confidence"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        drawView.onResume()
    }

    override fun onPause() {
        super.onPause()
        drawView.onPause()
    }
}
