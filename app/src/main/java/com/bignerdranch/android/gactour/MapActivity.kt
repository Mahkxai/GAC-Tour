package com.bignerdranch.android.gactour

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.math.roundToInt


const val TAGX = "MapActivity"
const val PERMISSIONID = 2


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var currentLocation: Location? = null

    private var currentLoc: Button? = null
    private var dropPin: Button? = null
    private lateinit var streamButton: Button

    private var uploadMethod: String = ""
    private lateinit var uploadMsg: TextView
    private lateinit var mapPhotoView: MapPhotoView
    private lateinit var mapFrameLayout: FrameLayout

    private val coordinatesThreeflags = Triple(-93.969900, 44.324488, 50f)


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_map)
        // uploadMethod = intent.getStringExtra("UploadType").toString()
        uploadMethod = "Gallery"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationUpdates()

        mapPhotoView = findViewById(R.id.custom_map)
        mapFrameLayout = findViewById(R.id.mapFrameLayout)

        try {
            mapPhotoView.maximumScale = 8f
            mapPhotoView.mediumScale = 3f
            mapPhotoView.minimumScale = 1f
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ZoomError", "Error setting zoom: ${e.message}")
        }

        val scale = 5f
        /* Set Starting Position of Map*/
        mapPhotoView.post {
            mapPhotoView.setScale(scale, false)

            /* Set Scroll to current location  */
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Convert current lat/lng to x/y pixels.
                    val (locX, locY) = mapPhotoView.getPinPosition(longitude, latitude)

                    // Compute how far the desired position is from the center.
                    // val centerXOffset = mapPhotoView.width / 2 - locX
                    // val centerYOffset = mapPhotoView.height / 2 - locY
                    // mapPhotoView.scrollTo((-centerXOffset).toInt(), (-centerYOffset).toInt())
                }
            }
        }

        /* Inflate Landmark Buttons onto the MapView */
        LocationProvider.locations.forEach { locationData ->
            val button = Button(this)
            button.text = locationData.name

            if (uploadMethod == "Gallery") {
                button.setOnClickListener {
                    val intent = Intent(this, UploadActivity::class.java)
                        .putExtra("Building", button.text)
                    startActivity(intent)
                }
            } else {
                button.setOnClickListener {
                    val intent = Intent(this, CameraActivity::class.java)
                        .putExtra("Building", button.text)
                    startActivity(intent)
                }
            }

            button.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,  // Width
                FrameLayout.LayoutParams.WRAP_CONTENT   // Height
            )

            // Set the button's position on the custom map
            mapPhotoView.addView(button, locationData.longitude, locationData.latitude)

            // Inflate button to layout
            mapFrameLayout.addView(button)
        }

        streamButton = findViewById(R.id.buttonStream)
        streamButton.setOnClickListener {
            val intent = Intent(this, StreamActivity::class.java)
                .putExtra("user","guest")
            startActivity(intent)
        }

        /* Load Google Maps
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        */

        // Extension function to convert DP to Pixels
        fun Int.dpToPx(context: Context): Int {
            val density = context.resources.displayMetrics.density
            return (this * density).roundToInt()
        }

        /* Drop Pin at Clicked Location */
        // todo: use pin to upload media
        mapPhotoView.setOnPhotoTapListener(object : OnPhotoTapListener {
            override fun onPhotoTap(view: ImageView?, x: Float, y: Float) {
                // Image's original dimensions
                val imageWidth = mapPhotoView.drawable.intrinsicWidth
                val imageHeight = mapPhotoView.drawable.intrinsicHeight

                // Calculate tapped position in image's pixel coordinates
                val imageX = (x * imageWidth).toInt()
                val imageY = (y * imageHeight).toInt()

                if (dropPin == null) {
                    dropPin = Button(this@MapActivity).apply {
                        isClickable = false
                        setBackgroundResource(R.drawable.map_drop_pin)

                        // Set size for the button
                        layoutParams = FrameLayout.LayoutParams(
                            6.dpToPx(this@MapActivity),  // Convert DP to Pixels for Width
                            6.dpToPx(this@MapActivity)   // Convert DP to Pixels for Height
                        )
                    }
                    mapFrameLayout.addView(dropPin)
                }
                val offsetX = 0.0f
                val offsetY = 0.0f

                val adjustedX = imageX + offsetX
                val adjustedY = imageY + offsetY
                mapPhotoView.addView(dropPin!!, adjustedX.toDouble(), adjustedY.toDouble(), true)
                Log.d(TAGX, "Tapped at image pixel coordinates: ($imageX, $imageY)")
            }
        })

    }

    private fun setupLocationUpdates() {
        // Setup the location request
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 1000 // 1 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.locations?.forEach { location ->
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d(TAGX, "Lat: $latitude , Long: $longitude")

                    // Inflate Current Location Dynamically
                    // If the button (marker) hasn't been initialized, create it
                    if (currentLoc == null) {
                        // Add a translation of 1 pixel in the Z-axis
                        currentLoc = Button(this@MapActivity)
                        currentLoc!!.translationZ = 1f
                        currentLoc!!.isClickable = false
                        currentLoc!!.setBackgroundResource(R.drawable.map_current_location)

                        currentLoc?.layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,  // Width
                            FrameLayout.LayoutParams.WRAP_CONTENT   // Height
                        )

                        currentLoc!!.setOnClickListener {

                        }

                        // Inflate button to layout
                        mapFrameLayout.addView(currentLoc)
                    }

                    // Set the button's position on the custom map
                    mapPhotoView.addView(currentLoc!!, longitude, latitude)

                }
            }
        }

        if (checkPermissions()) {
            startLocationUpdates()
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermissions()) {  // Check permissions
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
        } else {
            requestPermissions()
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSIONID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)  // Call the super method

        if (requestCode == PERMISSIONID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()  // Call startLocationUpdates again here after permissions are granted
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions() && isLocationEnabled()) {
            startLocationUpdates()
        } else {
            requestPermissions()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAGX, "onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAGX, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAGX, "onDestroy() called")
    }

    /* Get a handle to the GoogleMap object and display marker. */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(coordinatesThreeflags.second, coordinatesThreeflags.first))
                .title("Marker")
        )

        setupMap()
    }

    private fun setupMap() {
        // Set the initial camera position
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(coordinatesThreeflags.second, coordinatesThreeflags.first))
            .zoom(16f)
            .bearing(-57f)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        overlay2DMap()
    }

    private fun overlay2DMap() {
        // Geographical bounds of image
        val southWestLat = 44.320107
        val southWestLon = -93.985287

        val northEastLat = 44.326857
        val northEastLon = -93.963462

        val imageBounds = LatLngBounds(
            LatLng(southWestLat, southWestLon),
            LatLng(northEastLat, northEastLon)
        )

        /* Add image overlay to Google Maps
        googleMap.addGroundOverlay(
            GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.gustavus_adolphus_map_2d_mini))
                .positionFromBounds(imageBounds)
                .transparency(0.5f)  // Set some transparency

        )
        */

        googleMap.setLatLngBoundsForCameraTarget(imageBounds)
        googleMap.setMinZoomPreference(18.0f)
        // Optional: Hide the underlying Google Map
//        googleMap.mapType = GoogleMap.MAP_TYPE_NONE
    }

}




    /* Test Custom Buttons
    val drawable = mapPhotoView.drawable
    val imageWidth = drawable.intrinsicWidth.toFloat()
    val imageHeight = drawable.intrinsicHeight.toFloat()
    Log.d("MapActivity", "$imageHeight $imageWidth")

    val locX = coordinatesThreeflags.first
    val locY = coordinatesThreeflags.second
    val locX = coordinatesArb.first
    val locY = coordinatesArb.second
    mapPhotoView.addPin(beckPin, locX, locY)

    val beckPin: Button = findViewById(R.id.buttonBeck)
    val nobelPin: Button = findViewById(R.id.buttonNobel)
    val olinPin: Button = findViewById(R.id.buttonOlin)
    val beckX = coordinatesBeck.first
    val beckY = coordinatesBeck.second
    val nobelX = coordinatesNobel.first
    val nobelY = coordinatesNobel.second
    val olinX = coordinatesOlin.first
    val olinY = coordinatesOlin.second
    mapPhotoView.addPin(beckPin, beckX, beckY)
    mapPhotoView.addPin(nobelPin, nobelX, nobelY)
    mapPhotoView.addPin(olinPin, olinX, olinY)
    */

    /* Extra Test Code
    val square = { x: Double -> x * x }
    val posDistX = posThreeflags.first - posArb.first
    val posDistY = posThreeflags.second - posArb.second
    val coordinatesDistanceX = coordinatesThreeflags.first - coordinatesArb.first
    val coordinatesDistanceY = coordinatesThreeflags.second - coordinatesArb.second
    val mapScale = posDistX/coordinatesDistanceX
    val imgScale = imageWidth / imgSize.first
    val screenScale = mapScale * imgScale

    val posDistance = sqrt(square((posThreeflags.first - posArb.first).toDouble())
        + square((posThreeflags.second - posArb.second).toDouble()))
    val coordinatesDistance = sqrt(square((coordinatesThreeflags.first - coordinatesArb.first).toDouble())
            + square((coordinatesThreeflags.second - coordinatesArb.second).toDouble()))
    val beckX = ((coordinatesBeck.first - coordinatesThreeflags.first) * screenScale).toFloat() +
        posThreeflags.first
    val beckY = ((coordinatesBeck.second - coordinatesThreeflags.second) * screenScale).toFloat() +
            posThreeflags.second
    */

    /* Previous Code to Get Dynamic Locations Updates
    private fun setupLocationUpdates() {
        Log.d(TAGX, "Successful Call")

        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        // Setup the location request
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            smallestDisplacement = 3f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.locations?.forEach { location ->
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d(TAGX, "Lat: $latitude , Long: $longitude")
                }
            }
        }

        startLocationUpdates()
    }



    @SuppressLint("MissingPermission", "SetTextI18n")
    /* private fun getLocation() {
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

                        Log.d(TAGX, "Lat: $latitude , Long: $longitude")
//                            tvCountryName.text = "Country Name\n${list[0].countryName}"
//                            tvLocality.text = "Locality\n${list[0].locality}"
//                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    } */

    /* Current Location Updates */
    /* private fun getLocationUpdates() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationRequest = LocationRequest.create()?.apply {
                    interval = 1000
                    fastestInterval = 500
                    smallestDisplacement = 3f
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }!!

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
//                        locationResult ?: return
                        Log.d(TAGX, "location object created")

                        if (locationResult != null) {
                            Log.d(TAGX, "location received")

                            for (location in locationResult.locations){
                                // Update UI with location data
                                val latitude = location.latitude
                                val longitude = location.longitude

                                Log.d(TAGX, "Lat: $latitude , Long: $longitude")
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    } */

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
                getLocationUpdates()
            }
        }
    }


    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }
    */