package com.bignerdranch.android.gactour

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

        txtUploadMsg.text = "Upload a Picture!"
        buttonUpload.text = "Select Picture"

        buttonUpload.setOnClickListener {
                val galleryIntent = Intent(Intent.ACTION_PICK)
                galleryIntent.type = "image/*"
                imagePickerActivityResult.launch(galleryIntent)
        }
    }

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

}