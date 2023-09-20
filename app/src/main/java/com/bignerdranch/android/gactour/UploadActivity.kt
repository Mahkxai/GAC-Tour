package com.bignerdranch.android.gactour

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class UploadActivity : AppCompatActivity() {
    // references to firebase storage and database
    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference

    private lateinit var buttonUpload: Button
    private lateinit var txtUploadMsg: TextView
    private lateinit var imageview: ImageView

    private var picCount: Long = 0
    private var buildingName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        buildingName = intent.getStringExtra("Building").toString()

        storageRef = buildingName.let { FirebaseStorage.getInstance().getReference(it) }
        dbRef = buildingName.let { FirebaseDatabase.getInstance().getReference(it) }!!


        dbRef.get().addOnSuccessListener {
            picCount = it.childrenCount
            Log.d("UploadActivity", "Got value ${it.value} $picCount")
        }.addOnFailureListener{
            Log.d("UploadActivity", "Error getting data", it)
        }

        imageview = findViewById(R.id.imageView)
        buttonUpload = findViewById(R.id.buttonUpload)
        txtUploadMsg = findViewById(R.id.textView)

        txtUploadMsg.text = "Upload a File"
        buttonUpload.text = "Select File"

        buttonUpload.setOnClickListener {
//            val galleryIntent = Intent(Intent.ACTION_PICK)
//            galleryIntent.type = "image/*"
//            imagePickerActivityResult.launch(galleryIntent)

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            val mimeTypes = arrayOf("image/*", "video/*", "audio/*", "text/plain")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            imagePickerActivityResult.launch(intent)
        }
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                val mimeType = contentResolver.getType(uri!!)
                when {
                    mimeType?.startsWith("image/") == true -> uploadFile(uri, "IMG", ".jpg")
                    mimeType?.startsWith("video/") == true -> uploadFile(uri, "VID", ".mp4")
                    mimeType?.startsWith("audio/") == true -> uploadFile(uri, "AUD", ".mp3")
                    mimeType == "text/plain" -> uploadText(uri)
                }
            }
        }

    private fun uploadFile(uri: Uri, prefix: String, extension: String) {
        val fileID = "${prefix}_${buildingName}_${picCount + 1}${extension}"
        storageRef.child(fileID).putFile(uri).addOnSuccessListener {
            dbRef.child("${picCount + 1}").setValue(fileID)

            val videoView: VideoView = findViewById(R.id.videoView)
            val playButton: ImageButton = findViewById(R.id.playButton)
            val mediaPlayer = MediaPlayer()

            when (prefix) {
                "IMG" -> {
                    Glide.with(this).load(uri).into(imageview)
                    videoView.visibility = View.GONE
                    imageview.visibility = View.VISIBLE
                }
                "VID" -> {
                    videoView.setVideoURI(uri)
                    videoView.visibility = View.VISIBLE
                    imageview.visibility = View.GONE
                    videoView.start()

                    playButton.setOnClickListener {
                        videoView.start()
                        playButton.visibility = View.GONE
                    }

                }
                "AUD" -> {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(this, uri)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
            }

            videoView.setOnCompletionListener {
                playButton.visibility = View.VISIBLE
            }

            // Release the MediaPlayer resources when you no longer need them
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }


            txtUploadMsg.text = "File Uploaded Successfully!"
            buttonUpload.text = "Upload New File"
            buttonUpload.setOnClickListener {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }.addOnFailureListener {
            // Handle the failure
            Log.d("UploadActivity", "Error Uploading $extension: $it")
        }
    }

    private fun uploadText(uri: Uri) {
        val text = readTextFromUri(uri)
        val textID = "TXT_${buildingName}_${picCount + 1}.txt"
        dbRef.child("${picCount + 1}").setValue(text)
        // Display the text using a TextView if you wish
    }

    @SuppressLint("Range")
    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() } ?: ""
    }

    /*
    // lambda expression to receive a result back, here we
    // receive single item(photo) on selection
    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data
                val imageID = "IMG_${buildingName}_${picCount+1}.jpg"

                // extract the file name with extension
                // val sd = getFileName(applicationContext, imageUri!!)

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

                // upload image to firebase storage and display it using Glide
                val uploadTask = imageUri?.let { storageRef.child(imageID).putFile(it) }
                uploadTask?.addOnSuccessListener {
                    // upload to firebase db
                    dbRef.child("${picCount+1}").setValue(imageID)
                    // using glide library to display the image
                    storageRef.child("$imageID").downloadUrl.addOnSuccessListener {
                        Glide.with(this@UploadActivity)
                            .load(it)
                            .into(imageview)
                    }.addOnFailureListener {
                    }

                    txtUploadMsg.text = "Picture Uploaded Successfully!"
                    buttonUpload.text = "Upload New Picture"

                    buttonUpload.setOnClickListener {
                        val intent = intent
                        finish()
                        startActivity(intent)
                    }

                }?.addOnFailureListener {
                    // failed to upload image
                }
            }
        }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
    */
}