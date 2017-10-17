package com.volcano.mnistclassifier.classify

interface Classifier {
    fun name() : String

    fun recognize(pixels: FloatArray?) : Classification
}