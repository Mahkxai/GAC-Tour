package com.bignerdranch.android.gactour

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.Button
import com.github.chrisbanes.photoview.PhotoView
import java.lang.Math.sqrt

class MapPhotoView(context: Context, attrs: AttributeSet?) : PhotoView(context, attrs) {

    private val pins = mutableListOf<Button>()

    init {
        setOnMatrixChangeListener {
            for (button in pins) {
                val originalX = button.tag as FloatArray
                updatePinPosition(button, originalX[0], originalX[1])
            }
        }
    }

    fun addPin(button: Button, x: Float, y: Float) {
        pins.add(button)
        updatePinPosition(button, x, y)
    }

    private fun updatePinPosition(button: Button, x: Float, y: Float) {
        val matrix = Matrix(imageMatrix)
        val points = floatArrayOf(x, y)
        matrix.mapPoints(points)
        button.translationX = points[0] - button.width / 2
        button.translationY = points[1] - button.height / 2
        button.tag = floatArrayOf(x, y)
    }

}
