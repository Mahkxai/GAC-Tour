package com.bignerdranch.android.gactour

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.util.*

class StreamActivity : AppCompatActivity() {
    private lateinit var beck: Button
    private lateinit var nobel: Button
    private lateinit var olin: Button

    private var uploadMethod: String = ""
    private lateinit var uploadMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_stream)

        uploadMethod = intent.getStringExtra("UploadType").toString()

//        uploadMsg = findViewById(R.id.txtUploadMsg)
        beck = findViewById(R.id.buttonBeck)
        nobel = findViewById(R.id.buttonNobel)
        olin = findViewById(R.id.buttonOlin)

        if (uploadMethod == "Gallery") {
//            uploadMsg.text = "Select a building where \nyou want to upload your picture."

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