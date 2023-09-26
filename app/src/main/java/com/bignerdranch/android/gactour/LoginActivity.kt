package com.bignerdranch.android.gactour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate

class LoginActivity : AppCompatActivity() {
    private lateinit var btnStudent: Button
    private lateinit var btnGuest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnStudent = findViewById(R.id.btnStudent)
        btnGuest = findViewById(R.id.btnGuest)

        btnStudent.setOnClickListener{
            val intent = Intent(this, StreamActivity::class.java)
                .putExtra("user","student")
            startActivity(intent)
        }

        btnGuest.setOnClickListener{
            val intent = Intent(this, StreamActivity::class.java)
                .putExtra("user","guest")
            startActivity(intent)
        }
    }
}