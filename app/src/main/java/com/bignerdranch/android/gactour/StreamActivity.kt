package com.bignerdranch.android.gactour

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

const val TAG = "StreamActivity"

class StreamActivity : AppCompatActivity() {
    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference

    private var buildingName: String = ""
    private var picCount: Long = 1

    private lateinit var imageview: ImageView
    private lateinit var textViewBuilding: TextView
    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView
    private lateinit var buttonPrev: Button
    private lateinit var buttonNext: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)

        buildingName = intent.getStringExtra("Building").toString()
        storageRef = buildingName?.let { FirebaseStorage.getInstance().getReference(it) }
        dbRef = buildingName?.let { FirebaseDatabase.getInstance().getReference(it) }!!

        imageview = findViewById(R.id.imageCurrentMedia)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                picCount = dataSnapshot.childrenCount
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })

//        dbRef.get().addOnSuccessListener {
//            picCount = (1..it.childrenCount).random()
//            Log.d(TAG, "Got value ${it.value} $picCount")
//        }.addOnFailureListener{
//            Log.d(TAG, "Error getting data", it)
//        }

        var num = 1L
        displayImage(num)

        buttonPrev = findViewById(R.id.buttonPrev)
        buttonNext = findViewById(R.id.buttonNext)

        buttonPrev.setOnClickListener {
            num--;
            if (num == 0L) num = picCount
            Log.d(TAG, "Prev $num")
            displayImage(num)
        }

        buttonNext.setOnClickListener {
            num++;
            if (num > picCount) num = 1
            Log.d(TAG, "Next $num")
            displayImage(num)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()
    }

    private fun displayImage(num: Long) {
        val imageID = "IMG_${buildingName}_$num.jpg"
        Log.d(TAG, imageID)

        // using glide library to display the image
        storageRef?.child("$imageID")?.downloadUrl?.addOnSuccessListener {
            Glide.with(this@StreamActivity)
                .load(it)
                .into(imageview)
            Log.e("Firebase", "download passed")
        }?.addOnFailureListener {
            Log.e("Firebase", "Failed in downloading")
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            val address = list[0].getAddressLine(0)
                            val latitude = list[0].latitude
                            val longitude = list[0].longitude

                            textViewBuilding= findViewById(R.id.textCurrentBuilding)
                            textViewLatitude= findViewById(R.id.textLatitude)
                            textViewLongitude= findViewById(R.id.textLongitude)

                            textViewBuilding.text = "You're near: \n$buildingName"
                            textViewLatitude.text = "Latitude: $latitude"
                            textViewLongitude.text = "Longitude: $longitude"

                            Log.d(TAG, "Lat: $latitude , Long: $longitude")
//                            tvCountryName.text = "Country Name\n${list[0].countryName}"
//                            tvLocality.text = "Locality\n${list[0].locality}"
//                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                    }
                }
            } else {
//                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun getLocationUpdates() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationRequest = LocationRequest.create()?.apply {
                    interval = 1000
                    fastestInterval = 500
                    smallestDisplacement = 3f
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }!!
//                locationRequest =  LocationRequest()
//                locationRequest.interval = 2000
//                locationRequest.fastestInterval = 2000
//                locationRequest.smallestDisplacement = 3f // 170 m = 0.1 mile
//                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
//                        locationResult ?: return
                        if (locationResult != null) {
                            currentLocation = locationResult.lastLocation
                        }
                        if (locationResult != null) {
                            for (location in locationResult.locations){
                                // Update UI with location data
                                val latitude = location.latitude
                                val longitude = location.longitude

                                textViewBuilding= findViewById(R.id.textCurrentBuilding)
                                textViewLatitude= findViewById(R.id.textLatitude)
                                textViewLongitude= findViewById(R.id.textLongitude)

                                textViewBuilding.text = "You're near: \n$buildingName"
                                textViewLatitude.text = "Latitude: $latitude"
                                textViewLongitude.text = "Longitude: $longitude"

                                Log.d(TAG, "Lat: $latitude , Long: $longitude")
                            }
                        }
                    }
                }
            }
        } else {
            requestPermissions()
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }


    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

}