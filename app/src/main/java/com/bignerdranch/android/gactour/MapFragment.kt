package com.bignerdranch.android.gactour

import android.Manifest
import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.google.android.gms.location.*
import kotlinx.coroutines.processNextEventInCurrentThread

private const val PERMISSION_ID = 1001

class MapFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    private var currentLoc: Button? = null
    private var dropPin: Button? = null
    private var promptView: View? = null

    private lateinit var mapPhotoView: MapPhotoView
    private lateinit var mapFrameLayout: FrameLayout

    private val animationDuration = 300L

    // private val coordinatesThreeflags = Triple(-93.969900, 44.324488, 50f)


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

        /* Inflate Landmark Buttons onto the MapView *//*
        LocationProvider.locations.forEach { locationData ->
            val button = Button(context).apply {
                text = locationData.name
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    val intent = when(uploadMethod) {
                        "Gallery" -> Intent(context, UploadActivity::class.java)
                        else -> Intent(context, CameraActivity::class.java)
                    }
                    intent.putExtra("Building", text)
                    startActivity(intent)
                }
            }

            mapPhotoView.addPin(button, locationData.longitude, locationData.latitude)
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
        val offsetY = (0).dpToPx(requireContext())

        val adjustedX = imageX + offsetX
        val adjustedY = imageY + offsetY


        val buttonSize = 64.dpToPx(requireContext())
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
                mapPhotoView.addPin(dropPin!!, adjustedX.toDouble(), adjustedY.toDouble(), true)

                dropPin?.visibility = View.VISIBLE
            }

            mapFrameLayout.addView(dropPin)
        }

        // Add scaling animation for the pop up effect
//        mapPhotoView.foreground = ColorDrawable(Color.parseColor("#99000000"))
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

        mapPhotoView.addPin(dropPin!!, adjustedX.toDouble(), adjustedY.toDouble(), true)

        // Prompt the user to upload media at pin's location
        if (promptView == null) {
            promptView = LayoutInflater.from(requireContext())
                .inflate(R.layout.map_upload_prompt, mapFrameLayout, false)
            promptView?.visibility = View.INVISIBLE

            val acceptButton: Button = promptView!!.findViewById(R.id.btn_accept)
            val declineButton: Button = promptView!!.findViewById(R.id.btn_decline)

            acceptButton.setOnClickListener {
                // todo: upload media
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

            mapFrameLayout.addView(promptView)

            // Accounts for offset of initial pin/layout positioning
            promptView?.post {
                val matrix = Matrix(mapPhotoView.imageMatrix)
                val points = floatArrayOf(adjustedX, adjustedY.toFloat())
                matrix.mapPoints(points)

                promptView?.translationX = points[0] - (promptView?.width ?: 0) / 2.0f
                promptView?.translationY = points[1] - promptView?.height!! - buttonSize
                promptView?.tag = floatArrayOf(adjustedX, adjustedY.toFloat())

                promptView?.visibility = View.VISIBLE
            }
        }

        val matrix = Matrix(mapPhotoView.imageMatrix)
        val points = floatArrayOf(adjustedX, adjustedY.toFloat())
        matrix.mapPoints(points)

        promptView?.translationX = points[0] - (promptView?.width ?: 0) / 2.0f
        promptView?.translationY = points[1] - promptView?.height!! - buttonSize
        promptView?.tag = floatArrayOf(adjustedX, adjustedY.toFloat())


//        mapPhotoView.setOnMatrixChangeListener {
//            val originalX = promptView?.tag as FloatArray
//            val newMatrix = Matrix(mapPhotoView.imageMatrix)
//            val newPoints = floatArrayOf(originalX[0], originalX[1])
//            newMatrix.mapPoints(newPoints)
//            promptView?.translationX = newPoints[0] - (promptView?.width ?: 0) / 2.0f
//            promptView?.translationY = newPoints[1] - promptView?.height!! - buttonSize
//            promptView?.tag = floatArrayOf(originalX[0], originalX[1])
//        }

        Log.d(TAGX, "Tapped at image pixel coordinates: ($imageX, $imageY)")
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
                            mapPhotoView.addPin(currentLoc!!, longitude, latitude)
                            currentLoc?.visibility = View.VISIBLE
                        }
                    }


                    // Set the button's position on the custom map
                    mapPhotoView.addPin(currentLoc!!, longitude, latitude)
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