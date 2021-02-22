package com.ericg.androidLearning.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ericg.androidLearning.R
import com.ericg.androidLearning.databinding.ActivityMainBinding
import androidx.databinding.DataBindingUtil as DB

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)

class MainActivity : AppCompatActivity() {

    private var uri: Uri? = null
    private var mainActivityBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = DB.setContentView(this, R.layout.activity_main)
        mainActivityBinding?.btnSelectImage?.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            startActivityForResult(this, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            uri = data.data
            val bitMap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            mainActivityBinding!!.imageView.apply {
                setImageBitmap(bitMap)
                clipToOutline = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fmWhatsApp -> {
                toast("Clicked Fm WhatsApp")
                // Navigate
            }
            R.id.restartApp -> {
                toast("Clicked Restart")
                // Navigate
            }
            R.id.messageANumber -> {
                toast("Clicked Restart")
                // Navigate
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toast(message: String? = "") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}