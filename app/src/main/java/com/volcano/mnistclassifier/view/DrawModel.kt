package com.volcano.mnistclassifier.view

class DrawModel(val width: Int, val height: Int) {
    private var lines = ArrayList<Line>()
    private var currentLine: Line? = null

    fun startLine(point: Point) {
        currentLine = Line()
        currentLine!!.addPoint(point)
        lines.add(currentLine!!)
    }

    fun endLine() {
        currentLine = null
    }

    fun addPoint(point: Point) {
        currentLine!!.addPoint(point)
    }

    fun clear() {
        lines.clear()
        currentLine = null
    }

    fun getLine(index: Int) = lines[index]

    fun size() = lines.size

    data class Point(val x: Float, val y: Float)

    class Line {
        private val mPoints = ArrayList<Point>()

        fun addPoint(point: Point) {
            mPoints.add(point)
        }

        fun getPointAt(index: Int) : Point {
            return mPoints[index]
        }

        fun size() = mPoints.size
    }
}