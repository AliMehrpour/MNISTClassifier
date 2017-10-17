package com.volcano.mnistclassifier.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView(context: Context?, attrs: AttributeSet?) : View(context, attrs), View.OnTouchListener {
    private lateinit var model : DrawModel

    private val paint = Paint()
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private val matrixNormal = Matrix()
    private val matrixInverse = Matrix()

    private var initialized = false
    private var drawnLinesSize = 0

    fun setModel(model: DrawModel) {
        this.model = model

        setOnTouchListener(this)
    }

    fun reset() {
        drawnLinesSize = 0

        model.clear()

        bitmap?.let {
            paint.color = Color.WHITE
            val width = it.width
            val height = it.height
            canvas!!.drawRect(Rect(0, 0, width, height), paint)
        }

        invalidate()
    }

    fun onResume() {
        createBitmap()
    }

    fun onPause() {
        releaseBitmap()
    }

    fun getPixels() : FloatArray? {
        bitmap?.let {
            val width = it.width
            val height = it.height

            val size = width * height
            val pixels = IntArray(size)
            it.getPixels(pixels, 0, width, 0, 0, width, height)

            val retPixels = FloatArray(size)
            for (i in 0 until size) {
                val pixel = pixels[i]
                val b = pixel.or(0xff)
                retPixels[i] = ((0xff - b) / 255.0).toFloat()
            }

            return retPixels
        }

        return null
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (bitmap == null) {
            return
        }

        if (!initialized) {
            initialize()
        }

        // Render model
        render()

        // Draw updated bitmap
        canvas?.drawBitmap(bitmap, matrixNormal, paint)

        drawnLinesSize = model.size()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                processTouchDown(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                processTouchMove(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_UP -> {
                processTouchUp(event.x, event.y)
                return true
            }
        }

        return false
    }

    private fun initialize() {
        val modelWidth = model.width
        val modelHeight = model.height

        val scaleW = width / modelWidth
        val scaleH = height / modelHeight

        val scale = (if (scaleW > scaleH) scaleW else scaleH).toFloat()
        val newCx = modelWidth * scale / 2
        val newCy = modelHeight * scale / 2
        val dx = width / 2 - newCx
        val dy = height / 2 - newCy

        matrixNormal.setScale(scale, scale)
        matrixNormal.postTranslate(dx, dy)
        matrixNormal.invert(matrixInverse)

        paint.flags != Paint.ANTI_ALIAS_FLAG

        initialized = true
    }

    private fun render() {
        paint.color = Color.BLACK

        val start = Math.min(0, drawnLinesSize)
        val size = model.size()

        if (start <= 0 && size <= 0) {
            return
        }

        for (i in start until size) {
            val line = model.getLine(i)
            val lineSize = line.size()
            if (lineSize < 1) {
                continue
            }

            // Store first line's point
            val point = line.getPointAt(0)
            var lastX = point.x
            var lastY = point.y

            for (j in (1 until lineSize)) {
                val point = line.getPointAt(j)
                val x = point.x
                val y = point.y
                canvas?.drawLine(lastX, lastY, x, y, paint)
                lastX = x
                lastY = y
            }
        }
    }

    private fun createBitmap() {
        bitmap?.recycle()

        bitmap = Bitmap.createBitmap(model.width, model.height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

        reset()
    }

    private fun releaseBitmap() {
        bitmap?.recycle()
        bitmap = null
        canvas = null

        reset()
    }

    private fun processTouchDown(x: Float, y: Float) {
        val point = PointF()
        calculatePosition(x, y, point)
        model.startLine(DrawModel.Point(point.x, point.y))
    }

    private fun processTouchMove(x: Float, y: Float) {
        val point = PointF()
        calculatePosition(x, y, point)
        model.addPoint(DrawModel.Point(point.x, point.y))
        invalidate()
    }

    private fun processTouchUp(x: Float, y: Float) {
        model.endLine()
    }

    private fun calculatePosition(x: Float, y: Float, out: PointF) {
        val points = FloatArray(2)
        points[0] = x
        points[1] = y
        matrixInverse.mapPoints(points)
        out.x = points[0]
        out.y = points[1]
    }
}