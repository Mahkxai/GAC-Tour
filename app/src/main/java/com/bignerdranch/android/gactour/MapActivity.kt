package com.bignerdranch.android.gactour

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Matrix
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.github.chrisbanes.photoview.OnMatrixChangedListener
import com.github.chrisbanes.photoview.PhotoView

class MapActivity : AppCompatActivity() {
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
        val pin1: Button = findViewById(R.id.buttonBeck)
        mapPhotoView.addPin(pin1, 100f, 200f) // Replace x and y with desired positions

        val customMap: PhotoView = findViewById(R.id.custom_map)
        customMap.minimumScale = 1f
        customMap.maximumScale = 10.0f

//      adjust scaling and position after view load
        customMap.post {
            val scale = 4.0f
            customMap.setScale(scale, false)

            // Calculate the offset (considering the scaled image)
//            val offsetX = (10 * scale).toInt()
//            val offsetY = (100 * scale).toInt()

            // Scroll to the desired position
//            customMap.scrollTo(-offsetX, -offsetY)
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