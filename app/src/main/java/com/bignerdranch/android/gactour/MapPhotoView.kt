package com.bignerdranch.android.gactour

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import com.github.chrisbanes.photoview.PhotoView
import java.util.zip.InflaterOutputStream
import kotlin.math.sqrt

class MapPhotoView(context: Context, attrs: AttributeSet?) : PhotoView(context, attrs) {

    private val pins = mutableListOf<Button>()
    // Calculate scale factor to translate coordinate position to map position
    private val coordinatesThreeflags = Triple(-93.969900, 44.324488, 50f)
    private val coordinatesArb = Triple(-93.975110, 44.320171, 50f)
    private val posThreeflags = Pair(3273.0, -1518.0)
    private val posArb = Pair(1230.0, -1693.0)
    private val imgSize = Pair(4346.0, 2735.0)
    private val refLoc = Pair(coordinatesThreeflags.first, coordinatesThreeflags.second)
//    private val refLoc : Pair<Float, Float> = Pair(coordinatesThreeflags.first.toFloat(), coordinatesThreeflags.second.toFloat())


    init {
        setOnMatrixChangeListener {
            for (button in pins) {
                val originalX = button.tag as FloatArray
                updatePinPosition(button, originalX[0].toDouble(), originalX[1].toDouble())
            }
        }
    }

    fun addPin(button: Button, x: Double, y: Double) {
        pins.add(button)
        button.tag = floatArrayOf(x.toFloat(), y.toFloat())

        val buildingLoc = Pair(x, y)

        val rotatedLoc = rotatePoint(buildingLoc, getRotationAngle().toDouble(), refLoc)
        val newPos = scalePoint(rotatedLoc)

//        val newPos = scalePoint(buildingLoc)
//        val rotatedLoc = rotatePoint(newPos, getRotationAngle().toDouble(), refLoc)

//        Log.d("MapActivity", "Before: $x $y")
//        Log.d("MapActivity", "After: $rotatedLoc")
        Log.d("MapActivity", "Final: $newPos")

        updatePinPosition(button, newPos.first, newPos.second)
    }

    private fun getRotationAngle(): Double {
        val calculateAngle: (Double, Double, Double, Double) -> Double = { x1, y1, x2, y2 ->
            Math.atan2((y2 - y1), (x2 - x1)).toDouble()
        }

        val realAngle = calculateAngle(coordinatesThreeflags.first, coordinatesThreeflags.second,
            coordinatesArb.first, coordinatesArb.second)
//        Log.d("MapActivity", "real ${Math.toDegrees(realAngle.toDouble())}")


        val imageAngle = calculateAngle(posThreeflags.first, posThreeflags.second,
            posArb.first, posArb.second)

//        Log.d("MapActivity", "Pos ${posThreeflags.first} ${posThreeflags.second} ${posArb.first} ${posArb.second}")
//        Log.d("MapActivity", "img ${Math.toDegrees(imageAngle.toDouble())}")


        return realAngle - imageAngle
    }

    fun rotatePoint(
        buildingLoc: Pair<Double, Double>, angle: Double, refLoc: Pair<Double, Double>
    ): Pair<Double, Double> {

        val (x0, y0) = refLoc
        val (x1, y1) = buildingLoc
        val rotationAngle = angle * -1f
        // Translate point to origin
        val translatedX = x1 - x0
        val translatedY = y1 - y0

        // Rotate the point
        val xnew = translatedX * Math.cos(rotationAngle) - translatedY * Math.sin(rotationAngle)
        val ynew = translatedX * Math.sin(rotationAngle) + translatedY * Math.cos(rotationAngle)

        // Translate the point back
        val finalX = xnew + x0
        val finalY = ynew + y0

        return Pair(finalX.toDouble(), finalY.toDouble())
    }


    private fun scalePoint(pos: Pair<Double, Double>): Pair<Double, Double> {
        val drawable = this.drawable
        val imageWidth = drawable.intrinsicWidth.toDouble()
        val imageHeight = drawable.intrinsicHeight.toDouble()

        val posDistX = posThreeflags.first - posArb.first
        val posDistY = posThreeflags.second - posArb.second
        val coordinatesDistanceX = coordinatesThreeflags.first - coordinatesArb.first
        val coordinatesDistanceY = coordinatesThreeflags.second - coordinatesArb.second
        val imgScaleX = imageWidth / imgSize.first
        val imgScaleY = imageHeight / imgSize.second
        val mapScaleX = posDistX/coordinatesDistanceX
        val mapScaleY = posDistY/coordinatesDistanceY
        val screenScaleX =  mapScaleX * imgScaleX
        val screenScaleY =  mapScaleY * imgScaleY

//        val square = { x: Double -> x * x }
        val distance = { dx: Double, dy: Double -> sqrt(dx * dx + dy * dy) }
        val posDistance = distance(posDistX,  posDistY)
        val coordDistance = distance(coordinatesDistanceX, coordinatesDistanceY)
        val imgDistance = distance(imgSize.first, imgSize.second)
        val scrnDistance = distance(imageWidth, imageHeight)
        val imgScale =  scrnDistance / imgDistance
        val mapScale = posDistance / coordDistance
        val screenScale =  mapScale * imgScale * 0.87f

//        val posDistance = sqrt(square(posDistX) + square(posDistY))
//        val coordDistance = sqrt(square(coordinatesDistanceX) + square(coordinatesDistanceY))
//        val imgDistance = sqrt(square())

        val xDiff = pos.first - refLoc.first
        val yDiff = pos.second - refLoc.second

        val finalX = ( (xDiff * screenScale) + (posThreeflags.first*imgScale) )
        val finalY = ( (yDiff * screenScale) + (posThreeflags.second*imgScale) )

        Log.d("MapActivity", "Diff: $xDiff $yDiff")
        Log.d("MapActivity", "IMG: $imgScaleX $imgScaleY MAP: $mapScaleX $mapScaleY SCR: $screenScaleX $screenScaleY")
        Log.d("MapActivity", "Before: $pos After: ${Pair(finalX, -1* finalY)}")

        return Pair(finalX, -1 * finalY)
    }

    private fun updatePinPosition(button: Button, x: Double, y: Double) {
        val matrix = Matrix(imageMatrix)
        val points = floatArrayOf(x.toFloat(), y.toFloat())
        matrix.mapPoints(points)

        button.translationX = points[0] - button.width / 2
        button.translationY = points[1] - button.height / 2
        button.tag = floatArrayOf(x.toFloat(), y.toFloat())
    }
}
