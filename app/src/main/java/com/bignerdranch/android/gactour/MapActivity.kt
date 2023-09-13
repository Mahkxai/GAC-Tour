package com.bignerdranch.android.gactour

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.github.chrisbanes.photoview.PhotoView

class MapActivity : AppCompatActivity() {
    // first: latitude (X); second: longitude (Y); third: distance to display media
    private val coordinatesBeck = Triple(-93.97309072033401, 44.32398138197456, 50f)
    private val coordinatesNobel = Triple(-93.97277916957361, 44.32207370488806, 50f)
    private val coordinatesOlin = Triple(-93.97341709197065, 44.322791458399685, 50f)

    // Calculate scale factor to translate coordinate position to map position
    private val coordinatesThreeflags = Triple(-93.969900, 44.324488, 50f)
    private val coordinatesArb = Triple(-93.975110, 44.320171, 50f)

    private val posThreeflags = Pair(3273, 1518)  // Switched the first two parameters
    private val posArb = Pair(1230, 1693)        // Switched the first two parameters

    private val imgSize = Pair(4346, 2735)       // Switched the first two parameters

    private lateinit var beck: Button
    private lateinit var nobel: Button
    private lateinit var olin: Button

    private var uploadMethod: String = ""
    private lateinit var uploadMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_custom_map)

        uploadMethod = intent.getStringExtra("UploadType").toString()

        val mapPhotoView: MapPhotoView = findViewById(R.id.custom_map)
        val drawable = mapPhotoView.drawable
        val imageWidth = drawable.intrinsicWidth.toFloat()
        val imageHeight = drawable.intrinsicHeight.toFloat()
        Log.d("MapActivity", "$imageHeight $imageWidth")

        val beckPin: Button = findViewById(R.id.buttonBeck)
        val nobelPin: Button = findViewById(R.id.buttonNobel)
        val olinPin: Button = findViewById(R.id.buttonOlin)


//        val locX = coordinatesThreeflags.first
//        val locY = coordinatesThreeflags.second
//        val locX = coordinatesArb.first
//        val locY = coordinatesArb.second
        val beckX = coordinatesBeck.first
        val beckY = coordinatesBeck.second
        val nobelX = coordinatesNobel.first
        val nobelY = coordinatesNobel.second
        val olinX = coordinatesOlin.first
        val olinY = coordinatesOlin.second

        mapPhotoView.addPin(beckPin, beckX, beckY)
        mapPhotoView.addPin(nobelPin, nobelX, nobelY)
        mapPhotoView.addPin(olinPin, olinX, olinY)

//        mapPhotoView.addPin(pin1, posArb.first.toFloat() * imgScale, posArb.second.toFloat() * imgScale)

        val customMap: PhotoView = findViewById(R.id.custom_map)
        customMap.minimumScale = 1f
        customMap.maximumScale = 10.0f
        customMap.post {
            val scale = 1.0f
            customMap.setScale(scale, false)

            // Calculate the offset (considering the scaled image)
            // val offsetX = (10 * scale).toInt()
            //  val offsetY = (100 * scale).toInt()

            // Scroll to the desired position
            // customMap.scrollTo(-offsetX, -offsetY)
        }

        beck = findViewById(R.id.buttonBeck)
        nobel = findViewById(R.id.buttonNobel)
        olin = findViewById(R.id.buttonOlin)

        if (uploadMethod == "Gallery") {
            beck.setOnClickListener {
                val intent = Intent(this, UploadActivity::class.java)
                    .putExtra("Building","Beck")
                startActivity(intent)
            }

            nobel.setOnClickListener {
                val intent = Intent(this, UploadActivity::class.java)
                    .putExtra("Building","Nobel")
                startActivity(intent)
            }

            olin.setOnClickListener {
                val intent = Intent(this, UploadActivity::class.java)
                    .putExtra("Building","Olin")
                startActivity(intent)
            }
        } else {
//            uploadMsg.text = "Select a building where \nyou want to take your picture."

            beck.setOnClickListener {
                val intent = Intent(this, CameraActivity::class.java)
                    .putExtra("Building","Beck")
                startActivity(intent)
            }

            nobel.setOnClickListener {
                val intent = Intent(this, CameraActivity::class.java)
                    .putExtra("Building","Nobel")
                startActivity(intent)
            }

            olin.setOnClickListener {
                val intent = Intent(this, CameraActivity::class.java)
                    .putExtra("Building","Olin")
                startActivity(intent)
            }
        }
    }


//    val square = { x: Double -> x * x }
//    val posDistX = posThreeflags.first - posArb.first
//    val posDistY = posThreeflags.second - posArb.second
//    val coordinatesDistanceX = coordinatesThreeflags.first - coordinatesArb.first
//    val coordinatesDistanceY = coordinatesThreeflags.second - coordinatesArb.second
//    val mapScale = posDistX/coordinatesDistanceX
//    val imgScale = imageWidth / imgSize.first
//    val screenScale = mapScale * imgScale
//
//    val posDistance = sqrt(square((posThreeflags.first - posArb.first).toDouble())
//        + square((posThreeflags.second - posArb.second).toDouble()))
//    val coordinatesDistance = sqrt(square((coordinatesThreeflags.first - coordinatesArb.first).toDouble())
//            + square((coordinatesThreeflags.second - coordinatesArb.second).toDouble()))
//    val beckX = ((coordinatesBeck.first - coordinatesThreeflags.first) * screenScale).toFloat() +
//        posThreeflags.first
//    val beckY = ((coordinatesBeck.second - coordinatesThreeflags.second) * screenScale).toFloat() +
//            posThreeflags.second


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}