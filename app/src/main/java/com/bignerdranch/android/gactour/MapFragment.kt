package com.bignerdranch.android.gactour

import android.Manifest
import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val PERMISSION_ID = 1001

class MapFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    // Firebase References
    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference

    private var currentLoc: Button? = null
    private var dropPin: Button? = null
    private var promptView: View? = null
    private var uploadProgressView: View? = null

    private lateinit var mapPhotoView: MapPhotoView
    private lateinit var mapFrameLayout: FrameLayout

    private val animationDuration = 300L
    private var picCount = 0L
    private var promptX = 0f
    private var promptY = 0f

    // private val coordinatesThreeflags = Triple(-93.969900, 44.324488, 50f)


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_custom_map, container, false)
        mapPhotoView = view.findViewById(R.id.custom_map)
        mapFrameLayout = view.findViewById(R.id.mapFrameLayout)

        mapPhotoView.run {
            maximumScale = 5.00001f
            mediumScale = 5f
            minimumScale = 4.99999f
        }

        val scale = 5f
        mapPhotoView.post {
            mapPhotoView.setScale(scale, false)
            mapPhotoView.setOnPhotoTapListener(onPhotoTapListener)

            // TODO: Set Scroll to current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val latitude = location?.latitude
                val longitude = location?.longitude

                Log.d(TAG, "$latitude $longitude")
            }
        }

        val uploadMethod = "Gallery"
        /* Inflate Landmark Buttons onto the MapView *//*
        LocationProvider.locations.forEach { locationData ->
            val button = Button(requireContext()).apply {
                text = locationData.name
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    val intent = when(uploadMethod) {
                        "Gallery" -> Intent(requireContext(), UploadActivity::class.java)
                        else -> Intent(requireContext(), CameraActivity::class.java)
                    }
                    intent.putExtra("Building", text)
                    requireActivity().startActivity(intent) // Note this change
                }
            }

            mapPhotoView.addView(button, locationData.longitude, locationData.latitude)
            mapFrameLayout.addView(button)
        }
        */

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize fusedLocationClient for location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        setupLocationUpdates()

        val uploadString = "DropPin"
        storageRef = uploadString.let { FirebaseStorage.getInstance().getReference(it) }
        dbRef = uploadString.let { FirebaseDatabase.getInstance().getReference(it) }


        dbRef.get().addOnSuccessListener {
            picCount = it.childrenCount
            Log.d(TAGX, "Got value ${it.value} $picCount")
        }.addOnFailureListener{
            Log.d(TAGX, "Error getting data", it)
        }

    }


    /* Helper function to convert dp to pixels */
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }


    /* Handles taps on MapPhotoView to drop pins */
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ObjectAnimatorBinding")
    private val onPhotoTapListener = OnPhotoTapListener { _, x, y -> // Image's original dimensions
        val imageWidth = mapPhotoView.drawable.intrinsicWidth
        val imageHeight = mapPhotoView.drawable.intrinsicHeight

        // Calculate tapped position in image's pixel coordinates
        val imageX = (x * imageWidth).toInt()
        val imageY = (y * imageHeight).toInt()

        val offsetX = 0.0f
        val offsetY = (-7).dpToPx(requireContext()).toFloat() // Offset bottom of pin to pointer

        val adjustedX = imageX + offsetX
        val adjustedY = imageY + offsetY

        promptX = adjustedX - 0
        promptY = adjustedY - 24.dpToPx(requireContext())   // Offset to place prompt on top of pin

        val buttonSize = 64.dpToPx(requireContext())

        // Drop pin on tapped location
        if (dropPin == null) {
            dropPin = Button(requireContext())

            dropPin?.apply {
                visibility = View.INVISIBLE
                isClickable = false
                setBackgroundResource(R.drawable.gac_pin)

                layoutParams = FrameLayout.LayoutParams(buttonSize, buttonSize)

                scaleX = 1f
                scaleY = 1f
            }

            dropPin?.post {
                mapPhotoView.addView(dropPin!!, adjustedX.toDouble(), adjustedY.toDouble(), true)
                dropPin?.visibility = View.VISIBLE
            }

            mapFrameLayout.addView(dropPin)
        }

        // Prompt the user to upload media at pin's location
        if (promptView == null) {
            promptView = LayoutInflater.from(requireContext())
                .inflate(R.layout.upload_prompt, mapFrameLayout, false)

            promptView?.visibility = View.INVISIBLE

            val acceptButton: ImageButton = promptView!!.findViewById(R.id.btn_accept)
            val declineButton: ImageButton = promptView!!.findViewById(R.id.btn_decline)

            acceptButton.setOnClickListener {
                // todo: upload media
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                val mimeTypes = arrayOf("image/*", "video/*", "audio/*", "text/plain")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                filePickerActivityResult.launch(intent)
                mapFrameLayout.removeView(promptView)
                promptView = null
            }

            declineButton.setOnClickListener {
                // Dismiss the prompt
                mapFrameLayout.removeView(promptView)
                promptView = null

                // Reverse scaling animation to shrink the pin
                val colorAnimator = ValueAnimator.ofArgb(0x90000000.toInt(), Color.TRANSPARENT)
                colorAnimator.addUpdateListener { animator ->
                    val animatedColor = animator.animatedValue as Int
                    mapPhotoView.foreground = ColorDrawable(animatedColor)
                }
                val reverseScaleXAnimator = ObjectAnimator.ofFloat(dropPin, "scaleX", 1f, 0f)
                val reverseScaleYAnimator = ObjectAnimator.ofFloat(dropPin, "scaleY", 1f, 0f)
                val reverseAnimatorSet = AnimatorSet()
                reverseAnimatorSet.playTogether(reverseScaleXAnimator, reverseScaleYAnimator, colorAnimator)
                reverseAnimatorSet.duration = animationDuration

                reverseAnimatorSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        // todo: Remove the pin after animation ends
                    }
                })

                reverseAnimatorSet.start()
            }

            // Accounts for offset of initial pin/layout positioning
            promptView?.post {
                mapPhotoView.addView(promptView!!, promptX.toDouble(), promptY.toDouble(), true)
                promptView?.visibility = View.VISIBLE
            }

            mapFrameLayout.addView(promptView)
        }

        // Add scaling animation for the pop up effect
        val colorAnimator = ValueAnimator.ofArgb(Color.TRANSPARENT, 0x90000000.toInt())
        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            mapPhotoView.foreground = ColorDrawable(animatedColor)
        }

        val scaleXAnimator = ObjectAnimator.ofFloat(dropPin, "scaleX", 0.2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(dropPin, "scaleY", 0.2f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, colorAnimator)
        animatorSet.duration = animationDuration
        animatorSet.start()

        // centerOnPosition(mapPhotoView, adjustedX.toFloat(), adjustedY.toFloat())
        /*val combinedSet = AnimatorSet()
        combinedSet.playSequentially(centeringSet, animatorSet)
        combinedSet.start()*/

        mapPhotoView.addView(dropPin!!, adjustedX.toDouble(), adjustedY.toDouble(), true)
        mapPhotoView.addView(promptView!!, promptX.toDouble(), promptY.toDouble(), true)

        Log.d(TAGX, "Tapped at image pixel coordinates: ($imageX, $imageY)")
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private var filePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                val mimeType = requireActivity().contentResolver.getType(uri!!)
                when {
                    mimeType?.startsWith("image/") == true -> uploadFile(uri, "IMG", ".jpg")
                    mimeType?.startsWith("video/") == true -> uploadFile(uri, "VID", ".mp4")
                    mimeType?.startsWith("audio/") == true -> uploadFile(uri, "AUD", ".mp3")
                }
            }
    }

    /* Fetch media and display in appropriate view */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun uploadFile(uri: Uri, prefix: String, extension: String) {
        val fileID = "${prefix}_PIN_${picCount + 1}${extension}"
        storageRef.child(fileID).putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                // Calculate the progress percentage
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount

                if (uploadProgressView == null) {
                    uploadProgressView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.upload_progress, mapFrameLayout, false)

                    uploadProgressView?.visibility = View.INVISIBLE

                    // Accounts for offset of initial pin/layout positioning
                    uploadProgressView?.post {
                        mapPhotoView.addView(
                            uploadProgressView!!,
                            promptX.toDouble(),
                            promptY.toDouble(),
                            true
                        )
                        uploadProgressView?.visibility = View.VISIBLE
                    }

                    mapFrameLayout.addView(uploadProgressView)
                }
                val progressBar: ProgressBar = uploadProgressView!!.findViewById(R.id.uploadProgressBar)
                progressBar.progress = progress.toInt()

                mapPhotoView.addView(uploadProgressView!!, promptX.toDouble(), promptY.toDouble(), true)

            }
            .addOnSuccessListener {
//                dbRef.child("${picCount + 1}").setValue(fileID)

                val progressText: TextView = uploadProgressView!!.findViewById(R.id.uploadProgressText)
                progressText.text = "Upload Successful!"


                // Display the toast message
                Toast.makeText(requireContext(), "Upload Successful!", Toast.LENGTH_SHORT).show()

                // Use a handler to delay the removal
                Handler(Looper.getMainLooper()).postDelayed({
                    // Remove the view
                    mapFrameLayout.removeView(uploadProgressView)
                    uploadProgressView = null

                    // Reverse scaling animation to shrink the pin
                    val colorAnimator = ValueAnimator.ofArgb(0x90000000.toInt(), Color.TRANSPARENT)
                    colorAnimator.addUpdateListener { animator ->
                        val animatedColor = animator.animatedValue as Int
                        mapPhotoView.foreground = ColorDrawable(animatedColor)
                    }
                    val reverseScaleXAnimator = ObjectAnimator.ofFloat(dropPin, "scaleX", 1f, 0f)
                    val reverseScaleYAnimator = ObjectAnimator.ofFloat(dropPin, "scaleY", 1f, 0f)
                    val reverseAnimatorSet = AnimatorSet()
                    reverseAnimatorSet.playTogether(reverseScaleXAnimator, reverseScaleYAnimator, colorAnimator)
                    reverseAnimatorSet.duration = animationDuration

                    reverseAnimatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            // todo: Remove the pin after animation ends
                        }
                    })

                    reverseAnimatorSet.start()

                }, 3000) // Delay of 3 seconds


                when (prefix) {
                    "IMG" -> {

                    }
                    "VID" -> {


                    }
                    "AUD" -> {

                    }
                }
            }
            .addOnFailureListener {
                // Handle the failure
                Log.d("UploadActivity", "Error Uploading $extension: $it")
            }
    }

    /* Center screen to drop pin position */
    fun centerOnPosition(photoView: MapPhotoView, px: Float, py: Float) {
        val viewWidth = photoView.width
        val viewHeight = photoView.height

        // Calculate the center of the PhotoView in terms of the displayed image
        val matrixValues = FloatArray(9)
        photoView.imageMatrix.getValues(matrixValues)
        val currentScale = matrixValues[Matrix.MSCALE_X]
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        val centerX = (viewWidth / 2 - transX) / currentScale
        val centerY = (viewHeight / 2 - transY) / currentScale

        // Calculate the translation values to bring px, py to the center
        val dx = centerX - px
        val dy = centerY - py

        // Apply the translation to the matrix and set it to the PhotoView
        val newMatrix = Matrix(photoView.imageMatrix)
        newMatrix.postTranslate(dx * currentScale, dy * currentScale)
        photoView.imageMatrix = newMatrix
    }


    /* Setup the location request and track current location */
    private fun setupLocationUpdates() {
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
                    if (currentLoc == null && isAdded) {

                        // if (!isAdded) return

                        currentLoc = Button(requireContext()).apply {
                            tag = "CURRENT_LOCATION"
                            visibility = View.INVISIBLE
                            setBackgroundResource(R.drawable.map_current_location)
                            isClickable = false
                            isFocusable = false
                            translationZ = 1f
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,  // Width
                                FrameLayout.LayoutParams.WRAP_CONTENT   // Height
                            )
                            setPadding(0, 0, 0, 0)
                            setOnClickListener {
                                // todo: 1. stream media. 2. display banner with #media
                            }
                        }

                        // Inflate button to layout
                        mapFrameLayout.addView(currentLoc)

                        // Set animation to the beacon
                        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulsate)
                        currentLoc?.startAnimation(anim)

                        mapPhotoView.post {
                            mapPhotoView.addView(currentLoc!!, longitude, latitude)
//                            val (px, py) = mapPhotoView.getPinPosition(longitude, latitude)
//                            centerOnPosition(mapPhotoView, px.toFloat(), py.toFloat())
                            currentLoc?.visibility = View.VISIBLE
                        }
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
        if (checkPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestPermissions()
        }
    }


    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
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


}