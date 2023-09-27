// https://yangcha.github.io/iview/iview.html to find pixel coordinates

package com.bignerdranch.android.gactour

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import com.github.chrisbanes.photoview.PhotoView
import org.opencv.android.OpenCVLoader
import org.opencv.calib3d.Calib3d.findHomography
import org.opencv.core.Core.perspectiveTransform
import kotlin.math.*
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point

class MapPhotoView(context: Context, attrs: AttributeSet?) : PhotoView(context, attrs) {

    private val pins = mutableListOf<Button>()
    private val trackedViews = mutableListOf<View>()

    // Reference Real Coordinates
    private val realTennis = Pair(-93.972493, 44.329580)
    private val realPlex = Pair(-93.967557, 44.324720)
    private val realPitt = Pair(-93.973291, 44.319415)
    private val realRound = Pair(-93.984849, 44.326233)

    //  3D Map Reference Pixel Coordinates
    private val imgSize = Pair(4346.0, 2735.0)
    private val pixelTennis = Pair(3612.0, -634.0)  // Top Left corner of NE Tennis Court
    private val pixelPlex = Pair(3839.0, -1773.0)   // Bottom right of Plex
    private val pixelPitt = Pair(1287.0, -2112.0)   // Bottom left of Pittman
    private val pixelRound = Pair(1426.0, -242.0)   // Roundabout near the High School (Top Left)


    init {
        // Initialize OpenCV for Matrix transformations
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV");
        } else {
            Log.d("OpenCV", "OpenCV loaded");
        }

        setOnMatrixChangeListener {
            for (view in trackedViews) {
                val originalX = view.tag as FloatArray
                updateViewPosition(view, originalX[0].toDouble(), originalX[1].toDouble())
            }
        }
    }

    /* Communicates with Activities/Fragments and adds views */
    fun addView(view: View, x: Double, y: Double, isPixel: Boolean = false) {
        trackedViews.add(view)
        val (newX, newY) = getPinPosition(x, y, isPixel)
        updateViewPosition(view, newX, newY)
    }


    /* Transforms real coordinates to pixel coordinates */
    fun getPinPosition(x: Double, y: Double, isPixel: Boolean = false): Pair<Double, Double> {

        val distance = { dx: Double, dy: Double -> sqrt(dx * dx + dy * dy) }
        val drawable = this.drawable
        val imageWidth = drawable.intrinsicWidth.toDouble()
        val imageHeight = drawable.intrinsicHeight.toDouble()
        val imgScaleX = imageWidth / imgSize.first
        val imgScaleY = imageHeight / imgSize.second
        val imgDistance = distance(imgSize.first, imgSize.second)
        val scrnDistance = distance(imageWidth, imageHeight)
        val imgScale =  scrnDistance / imgDistance

        // Defining your four real-world points
        val realPoints = listOf(
            Point(realTennis.first, realTennis.second),
            Point(realPlex.first, realPlex.second),
            Point(realPitt.first, realPitt.second),
            Point(realRound.first, realRound.second)
        )

        // Defining your corresponding four pixel points
        val pixelPoints = listOf(
            Point(pixelTennis.first, pixelTennis.second),
            Point(pixelPlex.first, pixelPlex.second),
            Point(pixelPitt.first, pixelPitt.second),
            Point(pixelRound.first, pixelRound.second)
        )

        // Converting points to MatOfPoint2f format which is required by the findHomography method
        val realMat = MatOfPoint2f()
        val pixelMat = MatOfPoint2f()
        realMat.fromList(realPoints)
        pixelMat.fromList(pixelPoints)

        // Calculating the transformation matrix for both pixel -> real and v.v.
        // todo: Apply pixel scaling for pixel -> real transformation
        val homographyMatrix = if (isPixel) {
            findHomography(pixelMat, realMat)
        } else {
            findHomography(realMat, pixelMat)
        }

        // Transforming the points using the perspectiveTransform method
        val srcPoint = MatOfPoint2f(Point(x, y))
        val dstPoint = MatOfPoint2f()
        perspectiveTransform(srcPoint, dstPoint, homographyMatrix)

        // Adjusting the transformed points with device scaling
        val transformed = dstPoint.toArray()[0]
        val finalX = transformed.x * imgScale
        val finalY = -1 * transformed.y * imgScale


        if (isPixel) {
            return Pair(x, y)
        }

        return Pair(finalX, finalY)
    }

    /* Maps transformed to PhotoView */
    private fun updateViewPosition(view: View, x: Double, y: Double) {
        val matrix = Matrix(imageMatrix)
        val points = floatArrayOf(x.toFloat(), y.toFloat())
        matrix.mapPoints(points)

        view.translationX = points[0] - view.width / 2
        view.translationY = points[1] - view.height / 2

        // If you have any view-specific operations like the below, you can check for instance type
        if (view is Button && view.tag == "CURRENT_LOCATION") {
            // Your Button-specific operations
        }

        view.tag = floatArrayOf(x.toFloat(), y.toFloat())
    }

}




/*
    /*
    private val realTornado = Pair(-93.972247, 44.323026)
    private val realArb = Triple(-93.975110, 44.320171, 50f)
    private val realThreeFlags = Triple(-93.969900, 44.324488, 50f)
    private val pixelTornado = Pair(2495.0, -1510.0)
    private val pixelThreeFlags = Pair(3274.0, -1518.0)
    private val pixelArb = Pair(1230.0, -1693.0)
    /* //    2D Map Reference Pixel Coordinates
    private val imgSize = Pair(2866.0, 2098.0)
    private val pixelThreeflags = Pair(1947.0, -1493.0)
    private val pixelArb = Pair(742.0, -1305.0)
    */
    */

    /*
    val buildingLoc = Pair(x, y)
    val rotatedLoc = rotatePoint(buildingLoc, getRotationAngle(), refLoc)
    val newPos = scalePoint(rotatedLoc)
    updatePinPosition(button, newPos.first, newPos.second)
    */


    /*
    private fun scalePoint(pos: Pair<Double, Double>): Pair<Double, Double> {
        val distance = { dx: Double, dy: Double -> sqrt(dx * dx + dy * dy) }

        // Image information
        val drawable = this.drawable

        val imageWidth = drawable.intrinsicWidth.toDouble()
        val imageHeight = drawable.intrinsicHeight.toDouble()

        val imgScaleX = imageWidth / imgSize.first
        val imgScaleY = imageHeight / imgSize.second

        // Calculate pixel density
        val pixelDensity = context.resources.displayMetrics.density

        // Calculate vector difference of input coordinates and reference coordinates
        val deltaX = pos.first - realTornado.first
        val deltaY = pos.second - realTornado.second

        // Calculate the pixel and real-world distances for reference points
        val pixelDistX = pixelTornado.first - pixelTennis.first
        val pixelDistY = pixelTornado.second - pixelTennis.second

        val realDistX = realTornado.first - realTennis.first
        val realDistY = realTornado.second - realTennis.second

        val mapScaleX = abs(pixelDistX/realDistX)
        val mapScaleY = abs(pixelDistY/realDistY)

        val screenScaleX =  abs(mapScaleX * imgScaleX)
        val screenScaleY =  abs(mapScaleY * imgScaleY)

        val imgDistance = distance(imgSize.first, imgSize.second)
        val scrnDistance = distance(imageWidth, imageHeight)
        val imgScale =  scrnDistance / imgDistance
        val pixelDistance = distance(pixelDistX,  pixelDistY)
        val realDistance = distance(realDistX, realDistY)
        val mapScale = abs(pixelDistance / realDistance)
        val screenScale =  abs(mapScale * imgScale)

        val finalX = ( (deltaX * mapScale * imgScaleX) + (pixelTornado.first * imgScaleX) )
        val finalY = ( (deltaY * mapScale * imgScaleY) + (pixelTornado.second * imgScaleY) )

    //        val finalX = ( (deltaX * scaleX) + (pixelTornado.first) )
    //        val finalY = ( (deltaY * scaleY) + (pixelTornado.second) )

        Log.d("MapActivity", "Diff: $deltaX $deltaY  PxDensity: $pixelDensity")
        Log.d("MapActivity", "IMG: $imgScale MAP: $mapScale SCR: $screenScale")
        Log.d("MapActivity", "IMG: $imgScaleX $imgScaleY MAP: $mapScaleX $mapScaleY SCR: $screenScaleX $screenScaleY")

        Log.d("MapActivity", "Before: $pos After: ${Pair(finalX, -1* finalY)}")

        return Pair(finalX, -1 * finalY)
    }
    */

    /*
    private fun getRotationAngle(): Double {
        val calculateAngle: (Double, Double, Double, Double) -> Double = { x1, y1, x2, y2 ->
            atan2((y2 - y1), (x2 - x1))
        }
        val realAngle = calculateAngle(realTornado.first, realTornado.second,
            realTennis.first, realTennis.second)
        val imageAngle = calculateAngle(pixelTornado.first, pixelTornado.second,
            pixelTennis.first, pixelTennis.second)
        return realAngle - imageAngle
    }
    */

    /*
    private fun rotatePoint(
        buildingLoc: Pair<Double, Double>, angle: Double, refLoc: Pair<Double, Double>
    ): Pair<Double, Double> {

        val (x0, y0) = refLoc
        val (x1, y1) = buildingLoc
        val rotationAngle = angle * -1f

        // Translate point to origin
        val translatedX = x1 - x0
        val translatedY = y1 - y0

        // Rotate the point
        val xNew = translatedX * cos(rotationAngle) - translatedY * sin(rotationAngle)
        val yNew = translatedX * sin(rotationAngle) + translatedY * cos(rotationAngle)

        // Translate the point back
        val finalX = xNew + x0
        val finalY = yNew + y0

        return Pair(finalX, finalY)
    }
    */
*/
