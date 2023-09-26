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
import android.os.Handler
import android.os.Looper
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
    private lateinit var uploadProgressBar: ProgressBar


    private var picCount: Long = 0
    private var buildingName: String = ""

    private var isMediaPlayerValid = true
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        updateRunnable = Runnable {
            if (mediaPlayer.isPlaying) {
                // Your code to update UI or seekbar
                handler.postDelayed(updateRunnable, 1000)
            }
        }

        uploadProgressBar = findViewById(R.id.uploadProgressBar)

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

            val intent = Intent(Intent.ACTION_GET_CONTENT)
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

    /* Fetch media and display in appropriate view */
    // todo: Introduce Modularity and Handle Uploading Custom Text Messages
    private fun uploadFile(uri: Uri, prefix: String, extension: String) {
        val fileID = "${prefix}_${buildingName}_${picCount + 1}${extension}"
        storageRef.child(fileID).putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                // Calculate the progress percentage
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                uploadProgressBar.visibility = View.VISIBLE
                txtUploadMsg.text = "Uploading Media"
                buttonUpload.visibility = View.GONE
                uploadProgressBar.progress = progress.toInt()
            }
            .addOnSuccessListener {
                imageview.visibility = View.GONE
                dbRef.child("${picCount + 1}").setValue(fileID)

                val videoView: VideoView = findViewById(R.id.videoView)
                val playButton: ImageButton = findViewById(R.id.playButton)

                val playPauseButton = findViewById<Button>(R.id.playPauseButton)
                val seekBar = findViewById<SeekBar>(R.id.seekBar)
                var isPlaying = false

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

                txtUploadMsg.visibility = View.VISIBLE
                buttonUpload.visibility = View.VISIBLE
                txtUploadMsg.text = "File Uploaded Successfully!"
                buttonUpload.text = "Upload New File"
                buttonUpload.setOnClickListener {
                    uploadProgressBar.visibility = View.GONE
                    mediaPlayer.release()
                    val intent = intent
                    finish()
                    imageview.visibility = View.VISIBLE
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
            // Handle the failure
            uploadProgressBar.visibility = View.GONE
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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        isMediaPlayerValid = false
        handler.removeCallbacks(updateRunnable)
        super.onDestroy()
    }

}




    /* Previous Media (image) fetcher using hardcoded file values
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