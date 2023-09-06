package com.bignerdranch.android.gactour

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bignerdranch.android.gactour.databinding.ActivityCameraBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(
                    binding.root,
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference
    private var picCount: Long = 0
    private var buildingName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildingName = intent.getStringExtra("Building").toString()

        binding.txtUploadMsg.text = "Taking a Picture for $buildingName..."

        storageRef = buildingName.let { FirebaseStorage.getInstance().getReference(it) }
        dbRef = buildingName.let { FirebaseDatabase.getInstance().getReference(it) }

        dbRef.get().addOnSuccessListener {
            picCount = it.childrenCount
            Log.d("UploadActivity", "Got value ${it.value} $picCount")
        }.addOnFailureListener{
            Log.d("UploadActivity", "Error getting data", it)
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

        binding.preview.visibility = View.VISIBLE
        binding.switchBtn.visibility = View.VISIBLE
        binding.imgCaptureBtn.visibility = View.VISIBLE
        binding.btnNewImg.visibility = View.GONE

        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }

        binding.switchBtn.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }

        binding.btnNewImg.setOnClickListener {
            val intent = intent
            finish()
            startActivity(intent)
        }
    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        imageCapture?.let { it ->

            // getting realtime database values
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // gets the first snapshot and values after change in db
                    dbRef.get().addOnSuccessListener {
                        picCount = it.childrenCount
                        Log.d("UploadActivity", "${it.value} $picCount")
                    }.addOnFailureListener{
                        Log.d("UploadActivity", "Error getting data", it)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })

            val imageID = "IMG_${buildingName}_${picCount+1}.jpg"
            val file = File(externalMediaDirs[0], imageID)
            val imageUri = Uri.fromFile(file)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,

                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // upload image to firebase storage and display it using Glide
                        val uploadTask = imageUri?.let { storageRef.child(imageID).putFile(it) }
                        uploadTask?.addOnSuccessListener {
                            // upload to firebase db
                            dbRef.child("${picCount+1}").setValue(imageID)
                            // using glide library to display the image
                            storageRef.child("$imageID").downloadUrl.addOnSuccessListener {
                                binding.txtUploadMsg.text = "Picture Saved to $buildingName!"
                                Glide.with(this@CameraActivity)
                                    .load(it)
                                    .into(binding.imgSnap)
                            }.addOnFailureListener {
                            }
                        }?.addOnFailureListener {
                            // failed to upload image
                        }

                        Log.i(TAG, "The image has been saved in ${file.toUri()}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, "Error taking photo:$exception")
                    }

                }
            )
            binding.preview.visibility = View.GONE
            binding.switchBtn.visibility = View.GONE
            binding.imgCaptureBtn.visibility = View.GONE
            binding.btnNewImg.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

    companion object {
        val TAG = "CameraActivity"
    }
}