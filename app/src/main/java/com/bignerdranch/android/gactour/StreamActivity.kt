package com.bignerdranch.android.gactour

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.util.*

class StreamActivity : AppCompatActivity() {
    private lateinit var beck: Button
    private lateinit var nobel: Button
    private lateinit var olin: Button
    private lateinit var stream: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_stream)
        beck = findViewById(R.id.buttonBeck)
        nobel = findViewById(R.id.buttonNobel)
        olin = findViewById(R.id.buttonOlin)
        stream = findViewById(R.id.buttonStream)

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

        stream.setOnClickListener {
            val intent = Intent(this, StreamActivity::class.java)
                .putExtra("Building", "Olin")
            startActivity(intent)
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