package com.example.steganographer


import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EncryptActivity : AppCompatActivity() {
    private var keytxt:EditText?=null
    private var data_to_hide:EditText?=null
    private var imgbtn:TextView?=null
    private var encbtn:Button?=null
    private var b_string:String?=""
    private  var btm:Bitmap?=null
    private var image_show:ImageView?=null
    private var toolbar: Toolbar?=null
    private  var builder: AlertDialog.Builder?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypt)

        keytxt=findViewById<View>(R.id.keytxt) as EditText
        data_to_hide=findViewById<View>(R.id.data_to_hide) as EditText
        imgbtn=findViewById<View>(R.id.imgbtn) as TextView
        encbtn=findViewById<View>(R.id.encbtn) as Button
        image_show=findViewById<View>(R.id.image_view) as ImageView
        toolbar=findViewById<View>(R.id.toolbar) as Toolbar
        builder = AlertDialog.Builder(this)

        toolbar!!.setNavigationIcon(R.drawable.arrow_icon)
        toolbar!!.setTitle(R.string.app_name)
        toolbar !!.setNavigationOnClickListener(){
            finish()

        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.process_dialog, null)
        builder!!.setView(dialogView)
        builder!!.setTitle("Encrypting...")
        builder!!.setCancelable(false)
        val dialog: Dialog = builder!!.create()

        data_to_hide!!.setOnClickListener(){
            show_input_dialog()

        }
        //Adding image in which data will be encoded
        imgbtn!!.setOnClickListener(){
            //opening gallary
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, 1);
        }


        //Encryption of data and hiding it in image
        encbtn!!.setOnClickListener() {

            if(keytxt!!.text.toString().length==16) {
                if(data_to_hide!!.text.toString().length>0) {
                    if (btm != null) {
                        Thread{
                            runOnUiThread{dialog.show()}
                        //encrypting data using AES encryption
                        val s: String = encrypting_data()
                        //hiding data in image and getting modified img
                        val bitmap:Bitmap=data_hiding_in_img(s, btm!!)
                        //reseting the encrypted binary string for next time use
                        b_string=""
                        //saving stego image to gallary
                        saveMediaToStorage(bitmap)
                        runOnUiThread{dialog.dismiss()}
                        }.start()
                    }
                    else{Toast.makeText(this, "ADD A IMAGE FIRST", Toast.LENGTH_SHORT).show()
                }

            }
                else{ Toast.makeText(this, "Text to hide is empty", Toast.LENGTH_SHORT).show()
                }
        }
            else{
                val k=keytxt!!.text.toString().length
                Toast.makeText(this, "Key must be of 16 digits not $k ", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    //creating bitmap
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        uri,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()
                    val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    cursor.close()

                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                     BitmapFactory.decodeFile(picturePath, options)
                    Log.e("uri path", picturePath.toString())
                    //resizeing image
                    options.inSampleSize = calculateInSampleSize(options, 400, 400);
                    options.inJustDecodeBounds = false
                    btm =
                        BitmapFactory.decodeFile(picturePath, options)

                    image_show!!.setImageBitmap(btm)
                    image_show!!.visibility=View.VISIBLE
                    imgbtn!!.setText("Change Image")

                }
                catch (e: Exception) {
                    Toast.makeText(this, "Can't catch image.Try again!!", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun encrypting_data():String{
        val key = keytxt!!.text.toString()
        val s = data_to_hide!!.text.toString()
        //generating key from give  key text
        val skey: Key = SecretKeySpec(key.toByteArray(), "AES")
        print(skey.toString())
        val c: Cipher = Cipher.getInstance("AES")
        c.init(Cipher.ENCRYPT_MODE, skey)
        //encrypting text string
        val re = c.doFinal(s.toByteArray())

        //converting encrypted string to base64
        val re_base64 = Base64.encodeToString(re, Base64.NO_WRAP or Base64.NO_PADDING)
        Log.e("aaAA", re_base64.toString())

        //converting each chr of base64 string to binary and combining it
        for(i in re_base64){
            var single_b_string=Integer.toBinaryString((i.toInt()))
            //if binary str is less than 8 bit then making it 8 bit by adding 0's
            if(single_b_string.length<8){
                for(j in 1..(8-single_b_string.length)){
                    single_b_string="0"+single_b_string
                }
            }
            //final binary string to hide in image
            b_string= b_string+ single_b_string
        }
        Log.e("barraylength", b_string.toString())
        Log.e("barray", b_string!!.length.toString())
        return b_string.toString()

    }


    private fun data_hiding_in_img(s: String, btm: Bitmap):Bitmap{
        val termi_string="0001011100011110"
        val starting_string="011010010110111001100110011010010110111001101001"
        val str_to_encode =starting_string+s+ termi_string

        val w: Int = btm.getWidth()
        val h: Int = btm.getHeight()
        val data = IntArray(w * h)
        //getting btm piixel array
        btm.getPixels(data, 0, w, 0, 0, w, h)
        Log.e("w", w.toString())
        Log.e("h", h.toString())

        var count = 0
        var termi_count=0
        //modifying pixel data by encoding string(to encode)
        Log.e("r be", (data.get(1) shr 8 and 0xff).toString())
        Log.e("r2 before", (data.get(2) shr 8 and 0xff).toString())

        for (y in 0 until h) {
            if (count > str_to_encode!!.length - 1) {

                break
            } else {
                for (x in 0 until w) {
                    if (count > str_to_encode!!.length - 1) {
                        break

                    } else {
                        val index: Int = y * w + x
                        var R: Int = data.get(index) shr 16 and 0xff //bitwise shifting
                        var G: Int = data.get(index) shr 8 and 0xff
                        var B: Int = data.get(index) and 0xff
                        // val p= intArrayOf(R,G,B)
                        //barray.add(p)

                        R = encod(R, count, str_to_encode)
                        count++
                        if(count<str_to_encode!!.length){
                            G = encod(G, count, str_to_encode)
                            count++}
                        if(count<str_to_encode!!.length){
                            B = encod(B, count, str_to_encode)
                            count++}
                        // Log.e("count",count.toString())

                        //to restore the values after RGB modification, use
                        data[index] = -0x1000000 or (R shl 16) or (G shl 8) or B
                    }
                }
            }
        }
        Log.e("r after", (data.get(1) shr 8 and 0xff).toString())
        Log.e("r2 afte", (data.get(2) shr 8 and 0xff).toString())

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        //creating bitmap of modified pixel data
        bitmap.setPixels(data, 0, w, 0, 0, w, h)

        return bitmap
       }

    //encoding each bit of string(to encode) in rgb values
    private fun encod(co: Int, count: Int, str_to_encode: String):Int{
        var b=Integer.toBinaryString(co)
        if(b.length<8){
            for(j in 1..(8-b.length)){
                b="0"+b
            }
        }
        b=b.slice(0..(b.length - 2)) +str_to_encode!![count]
        val d=Integer.parseInt(b, 2)
        return d
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        try {
            val filename = "${System.currentTimeMillis()}.png"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("gggggggggggggggg", "kkkkkkkkkkkkkkkkkkkkkkkk")
                contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                    runOnUiThread { Toast.makeText(this, "Process Done!! Image saved to Internal/Pictures", Toast.LENGTH_SHORT).show() }

                }
            } else {
                Log.e("gggggggggggggggg", "jjjjjjjjjjjjjjjjjjjjjj")
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                runOnUiThread { Toast.makeText(this, "Process Done!! Image saved to $imagesDir", Toast.LENGTH_SHORT).show() }

            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)

            }}
         catch (e: Exception) {
            e.printStackTrace();
             runOnUiThread { Toast.makeText(this, "Error!! Image not Saved", Toast.LENGTH_SHORT).show() }
        }
    }
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


    private fun show_input_dialog() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle("Text Box")
            dialog.setMessage("Enter your Secret Text here")
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewInflated: View=inflater.inflate(R.layout.input_string_dialog, null)
        val input:EditText? = viewInflated.findViewById<View>(R.id.input) as? EditText
        input!!.setText(data_to_hide!!.text.toString())
            dialog.setView(viewInflated)
            dialog.setPositiveButton("Done", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                    data_to_hide!!.setText(input!!.text.toString())
                }
            })
            dialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }
            })
            .create()
        dialog.show()
    }
    override fun onBackPressed() {
        finish()
        return
    }
}