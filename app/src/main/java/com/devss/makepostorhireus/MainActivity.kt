package com.devss.makepostorhireus

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<ConstraintLayout>(R.id.my_layout)
        val saveBtn = findViewById<Button>(R.id.save_btn)
        val addBtn = findViewById<Button>(R.id.add_btn)
        val mET = findViewById<EditText>(R.id.my_et)
        val mTV = findViewById<TextView>(R.id.my_tv)
        imageUri = createImageUri()

        addBtn.setOnClickListener {
            mTV.text = mET.text.toString()
        }

        saveBtn.setOnClickListener {
            val bitmap = getBitmapFromView(container)
            storeBitmap(bitmap)
        }
    }

    private fun storeBitmap(bitmap: Bitmap) {
        val imageName = "99bit_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val mImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = mImageUri?.let { 
                    resolver.openOutputStream(it)
                }
            }
        } else {
            val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDirectory, imageName)
            fos = FileOutputStream(image)
        }
        
        fos?.use { 
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageUri(): Uri {
        val image = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.devss.makepostorhireus.fileProvider",
            image
        )
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bg = view.background
        bg.draw(canvas)

        view.draw(canvas)
        return bitmap
    }
}