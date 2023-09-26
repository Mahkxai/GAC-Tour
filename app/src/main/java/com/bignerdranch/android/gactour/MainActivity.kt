package com.bignerdranch.android.gactour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bignerdranch.android.gactour.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MapFragment())

        // Disable Night Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        // Fragment Handler to access different app components
        binding.bottomNavBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.map -> replaceFragment(MapFragment())
                R.id.stream -> replaceFragment(StreamFragment())
                R.id.upload -> replaceFragment(UploadFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}