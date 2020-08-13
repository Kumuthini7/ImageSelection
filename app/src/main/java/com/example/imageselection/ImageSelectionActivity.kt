package com.example.imageselection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Kumuthini.N on 12-08-2020
 */

class ImageSelectionActivity : AppCompatActivity() {
    private val adapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI() {

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mLayoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        rView.layoutManager = mLayoutManager
        rView.adapter = adapter

        select_images.setOnClickListener {
            if (askForPermissions()) {
                openGalleryForImages()
            }
        }
    }

    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , SELECT_PICTURE_REQUEST_CODE
            )
        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_PICTURE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SELECT_PICTURE_REQUEST_CODE -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val list = ArrayList<String>()

                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until 10) {
                            val selectedUri = data.clipData!!.getItemAt(i).uri
                            FileUtils.getPath(this, selectedUri)?.let { list.add(it) }
                        }
                        bindImages(list = list)
                        if (count > 10) {
                            Toast.makeText(
                                this,
                                "Only 10 images can be selected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (data?.data != null) {
                        val selectedUri = data.data
                        if (selectedUri != null) {
                            FileUtils.getPath(this, selectedUri)?.let { list.add(it) }
                            bindImages(list = list)
                        } else {
                            Toast.makeText(
                                this,
                                "Only 10 images can be selected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }
        }
    }

    private fun bindImages(list: java.util.ArrayList<String>) {
        adapter.update(list)
    }

    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    openGalleryForImages()
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, i ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val REQUEST_CODE = 1000
        private const val SELECT_PICTURE_REQUEST_CODE = 1001
    }
}