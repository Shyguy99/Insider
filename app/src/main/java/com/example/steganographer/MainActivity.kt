package com.example.steganographer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private var enc_img:ImageView?=null
    private var dec_img:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // request permission to use the camera on the user's phone
        if (ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
        }

        // request permission to write data (aka images) to the user's external storage of their phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION)
        }

        // request permission to read data (aka images) from the user's external storage of their phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION)
        }



        enc_img=findViewById<View>(R.id.enc_img) as ImageView
        dec_img=findViewById<View>(R.id.dec_img) as ImageView

        enc_img!!.setOnClickListener(){
            intent= Intent(this, EncryptActivity::class.java)
            startActivity(intent)
        }
        dec_img!!.setOnClickListener(){
            intent= Intent(this, DecryptActivity::class.java)
            startActivity(intent)
        }



    }
    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(applicationContext, "This application needs read, write, and camera permissions to run. Application now closing.", Toast.LENGTH_LONG).show()
                System.exit(0)
            }
        }
    }
    companion object {
        const val REQUEST_PERMISSION = 300

    }
}