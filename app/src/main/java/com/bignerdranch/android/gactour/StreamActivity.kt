package com.bignerdranch.android.gactour

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

const val TAG = "StreamActivity"

class StreamActivity : AppCompatActivity() {
    private  val beck = Triple(44.32401340322511,-93.97310743842756, 50f)
    private  val nobel = Triple(44.3220737848025, -93.97273944205695, 50f)
    private  val olin = Triple(44.32279045172101, -93.97334009636947, 50f)

    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference

    private var currentBuilding: String = "Walking"
    private var oldBuilding: String = ""
    private var picCount: Long = 1
    private var clicked: Boolean = false

    private lateinit var imageview: ImageView
    private lateinit var textViewBuilding: TextView
    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView
    private lateinit var textViewBeckDistance: TextView
    private lateinit var textViewNobelDistance: TextView
    private lateinit var textViewOlinDistance: TextView
    private lateinit var textViewImageId: TextView
    private lateinit var buttonPrev: ImageButton
    private lateinit var buttonNext: ImageButton
    private lateinit var buttonUpload: ImageButton
    private lateinit var buttonCamera: ImageButton
    private lateinit var buttonGallery: ImageButton
    private lateinit var buttonMap: Button


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
//    private lateinit var locationCallback: LocationCallback

    private var address: String = "Current Location"

//    private var currentLocation: Location? = null
    private val permissionId = 2
    private var user: String = ""

    private var mediaFileIds: List<String> = listOf()
    private var currentIndex: Int = 0

    private var isMediaPlayerValid = true
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        getLocation()
        getLocationUpdates()

        user = intent.getStringExtra("user").toString()

        buttonMap = findViewById(R.id.buttonMap)
        buttonMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
                .putExtra("UploadType","Camera")
            startActivity(intent)
        }

        imageview = findViewById(R.id.imageCurrentMedia)
        textViewBuilding= findViewById(R.id.textCurrentBuilding)
        textViewLatitude= findViewById(R.id.textLatitude)
        textViewLongitude= findViewById(R.id.textLongitude)
        textViewImageId = findViewById(R.id.textImageId)
        textViewBeckDistance = findViewById(R.id.textDistanceFromBeck)
        textViewNobelDistance = findViewById(R.id.textDistanceFromNobel)
        textViewOlinDistance = findViewById(R.id.textDistanceFromOlin)
        buttonPrev = findViewById(R.id.buttonPrev)
        buttonNext = findViewById(R.id.buttonNext)
        buttonUpload = findViewById(R.id.btnUpload)
        buttonCamera = findViewById(R.id.btnCamera)
        buttonGallery = findViewById(R.id.btnGallery)

        var num = 1L
//        displayImage(num, currentBuilding)

        buttonPrev.setOnClickListener {
            currentIndex = (currentIndex - 1 + mediaFileIds.size) % mediaFileIds.size
            displayMedia(currentIndex)
        }

        buttonNext.setOnClickListener {
            currentIndex = (currentIndex + 1) % mediaFileIds.size
            displayMedia(currentIndex)
        }

//        buttonPrev.setOnClickListener {
//            num--;
//            if (num == 0L) num = picCount
//            Log.d(TAG, "Prev $num")
//            displayImage(num)
//        }
//
//        buttonNext.setOnClickListener {
//            num++;
//            if (num > picCount) num = 1
//            Log.d(TAG, "Next $num")
//            displayImage(num)
//        }

        if (user == "guest") {
            buttonUpload.visibility = View.GONE
            buttonCamera.visibility = View.GONE
            buttonGallery.visibility = View.GONE
        } else {
            buttonUpload.visibility = View.VISIBLE
            buttonCamera.visibility = View.VISIBLE
            buttonGallery.visibility = View.VISIBLE
        }

        buttonUpload.setOnClickListener {
            if (clicked) {
                buttonCamera.animate().alpha(0f).translationXBy(150f).translationYBy(150f).setDuration(250);
                buttonGallery.animate().alpha(0f).translationXBy(-150f).translationYBy(150f).setDuration(250);
//                buttonCamera.visibility = View.INVISIBLE
//                buttonGallery.visibility = View.INVISIBLE
            } else {
//                buttonCamera.visibility = View.VISIBLE
//                buttonGallery.visibility = View.VISIBLE
                buttonCamera.animate().alpha(1f).translationXBy(-150f).translationYBy(-150f).setDuration(250);
                buttonGallery.animate().alpha(1f).translationXBy(150f).translationYBy(-150f).setDuration(250);
            }
            clicked = !clicked
        }

        buttonCamera.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
                .putExtra("UploadType","Camera")
            startActivity(intent)
        }

        buttonGallery.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
                .putExtra("UploadType","Gallery")
            startActivity(intent)
        }
    }

    private fun getCurrentBuilding(currentLat: Double, currentLong: Double): String {
        val distanceFromBeckDeg = sqrt((currentLat - beck.first).pow(2) + (currentLong - beck.second).pow(2))
        val distanceFromNobelDeg = sqrt((currentLat - nobel.first).pow(2) + (currentLong - nobel.second).pow(2))
        val distanceFromOlinDeg = sqrt((currentLat - olin.first).pow(2) + (currentLong - olin.second).pow(2))

        val distanceFromBeckM = distanceFromBeckDeg/0.001f * 111f
        val distanceFromNobelM = distanceFromNobelDeg/0.001f * 111f
        val distanceFromOlinM = distanceFromOlinDeg/0.001f * 111f

        textViewBeckDistance.text = "Beck: $distanceFromBeckM"
        textViewNobelDistance.text = "Nobel: $distanceFromNobelM"
        textViewOlinDistance.text = "Olin: $distanceFromOlinM"
//        Log.d(TAG, "Beck: $distanceFromBeckM, Nobel: $distanceFromNobelM, Olin: $distanceFromOlinM")

        if (distanceFromBeckM < beck.third) {
            // check if Beck
            return "Beck"
        }
        else if (distanceFromNobelM < nobel.third) {
            // check if Nobel
            return "Nobel"
        }
        else if (distanceFromOlinM < olin.third) {
            // check if Olin
            return "Olin"
        }
        return "Walking"
    }

    /* Helper function to stream media fetched from Firebase Storage */
    // todo: Introduce Modularity and Optimize for Streaming (Reused code from UploadActivity)
    private fun displayMedia(index: Int) {
        val videoView: VideoView = findViewById(R.id.videoView)
        val playButton: ImageButton = findViewById(R.id.playButton)

        val playPauseButton = findViewById<Button>(R.id.playPauseButton)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        var isPlaying = false

        videoView.visibility = View.GONE
        videoView.stopPlayback()
        seekBar.visibility = View.GONE
        playPauseButton.visibility = View.GONE
        playButton.visibility = View.GONE
        imageview.visibility = View.GONE
        // Stop the video playback if any
//        imageview.visibility = View.GONE
        // For audio, you can reset or stop the media player if it's playing
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        if (mediaFileIds.isNotEmpty()) {
            val fileId = mediaFileIds[index]
            buttonPrev.visibility = View.VISIBLE
            buttonNext.visibility = View.VISIBLE

            val extension = fileId.substringAfterLast(".", "")

            Log.d(TAG, "$fileId")

            // Use fileId to download from Firebase Storage
            storageRef.child(fileId).downloadUrl.addOnSuccessListener { uri ->
                playPauseButton.setOnClickListener {
                    if (isPlaying) {
                        mediaPlayer.pause()
                        playPauseButton.text = "Play"
                    } else {
                        mediaPlayer.start()
                        playPauseButton.text = "Pause"
                    }
                    isPlaying = !isPlaying
                }

                // Update SeekBar while song is playing
                val updateSeekBar = object : Runnable {
                    override fun run() {
                        if (isMediaPlayerValid) {
                            seekBar.progress = mediaPlayer.currentPosition
                            handler.postDelayed(this, 1000)
                        }
                    }
                }

                mediaPlayer.setOnPreparedListener {
                    seekBar.max = mediaPlayer.duration
                    seekBar.visibility = View.VISIBLE
                    playPauseButton.visibility = View.VISIBLE
                    handler.post(updateSeekBar)
                }

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress)
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })


                when (extension) {

                    "jpg", "jpeg", "png" -> {
                        Glide.with(this).load(uri).into(imageview)
                        videoView.visibility = View.GONE
                        imageview.visibility = View.VISIBLE
                    }
                    "mp4", "3gp" -> {
                        val progressBar: ProgressBar = findViewById(R.id.progressBar)

                        // Show the progress bar before setting the video URI
                        progressBar.visibility = View.VISIBLE

                        videoView.setOnInfoListener { _, what, _ ->
                            when (what) {
                                MediaPlayer.MEDIA_INFO_BUFFERING_START -> progressBar.visibility = View.VISIBLE
                                MediaPlayer.MEDIA_INFO_BUFFERING_END -> progressBar.visibility = View.GONE
                            }
                            true
                        }

                        videoView.setOnPreparedListener { mediaPlayer ->
                            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)

                            // Hide the progress bar when video is prepared
                            progressBar.visibility = View.GONE
                            videoView.start()
                        }

                        videoView.setOnCompletionListener {
                            playButton.visibility = View.VISIBLE
                        }

                        videoView.setVideoURI(uri)
                        videoView.visibility = View.VISIBLE
                        imageview.visibility = View.GONE

                        playButton.setOnClickListener {
                            videoView.start()
                            playButton.visibility = View.GONE
                        }

                        /*********/
//                        videoView.setVideoURI(uri)
//                        videoView.visibility = View.VISIBLE
//                        imageview.visibility = View.GONE
//                        videoView.start()
//
//                        playButton.setOnClickListener {
//                            videoView.start()
//                            playButton.visibility = View.GONE
//                        }

                    }
                    "mp3", "wav" -> {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(this, uri)
                        mediaPlayer.prepare()  // Asynchronous preparation might be better, using prepareAsync()
                        mediaPlayer.start()
                        isPlaying = true
                        playPauseButton.text = "Pause"
                    }
                }

                videoView.setOnCompletionListener {
                    playButton.visibility = View.VISIBLE
                }

                // Release the MediaPlayer resources when you no longer need them
                mediaPlayer.setOnCompletionListener {
                    isMediaPlayerValid = false
                }
//                when (extension) {
//                    "jpg", "jpeg", "png" -> playImage(uri)
//                    "mp4", "3gp" -> playVideo(uri)
//                    "mp3", "wav" -> playAudio(uri)
//                    else -> {
//                        // Handle unrecognized extensions
//                    }
//                }
                textViewImageId.text = fileId
            }.addOnFailureListener {
                Log.e("Firebase", "Download Failed")
            }
        } else {
            textViewImageId.text = "No media files found in $address"
            imageview.setImageResource(R.drawable.gac_logo)
        }
    }

    private fun playImage(uri: Uri) {
        Glide.with(this@StreamActivity)
            .load(uri)
            .into(imageview)
        imageview.visibility = View.VISIBLE
        // Hide VideoView and stop any other media players
    }

    private fun playVideo(uri: Uri) {
        val videoView: VideoView = findViewById(R.id.videoView)
        val playButton: ImageButton = findViewById(R.id.playButton)

        videoView.setVideoURI(uri)
        videoView.start()

        playButton.setOnClickListener {
            videoView.start()
            playButton.visibility = View.GONE
        }

        videoView.setOnCompletionListener {
            playButton.visibility = View.VISIBLE
        }
    }

    private fun playAudio(uri: Uri) {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, uri)
            prepare()  // Asynchronous preparation might be better using prepareAsync()
            start()
        }
    }

    // todo: Simplify Location Handler Functions
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

                        address = list[0].getAddressLine(0)
                        val latitude = list[0].latitude
                        val longitude = list[0].longitude

                        textViewBuilding= findViewById(R.id.textCurrentBuilding)
                        textViewLatitude= findViewById(R.id.textLatitude)
                        textViewLongitude= findViewById(R.id.textLongitude)

                        textViewBuilding.text =
                            if (currentBuilding == "Walking") "No Images Found in $address"
                            else "You're in $currentBuilding"
                        textViewLatitude.text = "Latitude: $latitude"
                        textViewLongitude.text = "Longitude: $longitude"

//                        Log.d(TAG, "Lat: $latitude , Long: $longitude")
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

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
//                        locationResult ?: return
//                        Log.d(TAG, "location object created")

                        if (locationResult != null) {
//                            Log.d(TAG, "location received")

                            for (location in locationResult.locations){
                                // Update UI with location data
                                val latitude = location.latitude
                                val longitude = location.longitude

                                oldBuilding = currentBuilding
                                currentBuilding = getCurrentBuilding(latitude, longitude)

                                Log.d(TAG, "$currentBuilding")

                                if (oldBuilding != currentBuilding) {
                                    // Initialize references
                                    storageRef = FirebaseStorage.getInstance().getReference(currentBuilding)
                                    dbRef = FirebaseDatabase.getInstance().getReference(currentBuilding)

                                    // Fetch media IDs once
                                    dbRef.get().addOnSuccessListener { dataSnapshot ->
                                        mediaFileIds = dataSnapshot.children.mapNotNull { it.value?.toString() }
                                        Log.d(TAG, "First Call: $mediaFileIds IX: $currentIndex")
                                        displayMedia(currentIndex)
                                    }.addOnFailureListener {
                                        Log.e("Firebase", "Failed to fetch file IDs")
                                    }
                                }

                                textViewBuilding.text =
                                    if (currentBuilding == "Walking") "No Images Found in $address"
                                    else "You're in $currentBuilding"
                                textViewLatitude.text = "Latitude: $latitude"
                                textViewLongitude.text = "Longitude: $longitude"

//                                Log.d(TAG, "Lat: $latitude , Long: $longitude")
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

}




/* Previous function used to display on images
private fun displayImage(num: Long, building: String = currentBuilding) {
    if (building != "Walking") {
        val imageID = "IMG_${building}_$num.jpg"
        textViewImageId.text = imageID
        buttonPrev.visibility = View.VISIBLE
        buttonNext.visibility = View.VISIBLE

        // using glide library to display the image
        storageRef = currentBuilding.let { FirebaseStorage.getInstance().getReference(it) }
        dbRef = currentBuilding.let { FirebaseDatabase.getInstance().getReference(it) }!!

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                picCount = dataSnapshot.childrenCount
//                    Log.d(TAG, "${dataSnapshot.value} $picCount")
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })

        storageRef.child(imageID).downloadUrl.addOnSuccessListener {
            Glide.with(this@MainActivity)
                .load(it)
                .into(imageview)
            Log.e("Firebase", "Download passed")
        }.addOnFailureListener {
            Log.e("Firebase", "Download Failed")
        }

    } else {
        textViewImageId.text = "No images found in $address"
        imageview.setImageResource(R.drawable.gac_logo)
        buttonPrev.visibility = View.GONE
        buttonNext.visibility = View.GONE
    }
}
*/