package com.example.steganographer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.ByteArrayOutputStream
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class DecryptActivity : AppCompatActivity() {
    private var key_txt: EditText? = null
    private var decry_img_btn: TextView? = null
    private var decode_str:String=""
    private   var decoded_txt:TextView?=null
    private var decry_btn:Button?=null
    private var btm:Bitmap?=null
    private var final_str:String?=""
    private var toolbar: Toolbar?=null
    private  var builder:AlertDialog.Builder?=null
    private val valid_imgornot="011010010110111001100110011010010110111001101001"




    //valid variable to check whether data present in image or not
    private var valid:Int=1
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt)
        key_txt = findViewById<View>(R.id.key_txt) as EditText
        decry_img_btn = findViewById<View>(R.id.decry_img) as TextView
        decry_btn=findViewById<View>(R.id.decrybtn) as Button
        decoded_txt=findViewById<View>(R.id.decoded_txt) as TextView
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
        builder!!.setTitle("Decrypting...")
        builder!!.setCancelable(false)
        val dialog: Dialog = builder!!.create()

        decoded_txt!!.setOnClickListener(){
            val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", decoded_txt!!.text.toString())
            manager.setPrimaryClip(clipData)
            Toast.makeText(this,"Text Copied to Clipboard",Toast.LENGTH_SHORT).show()
        }
        decry_img_btn!!.setOnClickListener() {
            try {

                //opening gallary
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(intent, 1);
            }
            catch (e: java.lang.Exception){
                Toast.makeText(this, "Can't Open Gallary", Toast.LENGTH_SHORT).show()
            }

        }

        decry_btn!!.setOnClickListener(){
            
            if(key_txt!!.text.toString().length==16) {
                if (btm != null) {
                            Log.e("111", "its first")
                    Thread {
                            this.runOnUiThread { dialog.show() }
                            exctract_str_from_img(btm!!)
                        Log.e("padddinf", valid.toString())
                    if (valid == 1) {
                            //converting decode_string to base64 for decrypting
                            val base64_str = convo_to_base64(decode_str)

                            //decrypting the string
                             final_str = decod_str(base64_str)

                             this.runOnUiThread {
                                 dialog.dismiss()
                        final_str?.let { it1 -> Log.e("final", it1) }

                        decoded_txt!!.text = final_str

                        decoded_txt!!.visibility = View.VISIBLE


                        //reseting the variables for next use
                        decode_str = ""
                        final_str=""
                             valid=1}
                    
                        }
                    
                        
                        else {
                            runOnUiThread {

                                    dialog.dismiss()
                                Toast.makeText(this, "No data encoded in image", Toast.LENGTH_SHORT).show()}
                        }
                    }.start()
                    
            }
                else{Toast.makeText(this, "Image not added", Toast.LENGTH_SHORT).show()
                }
            }
            else{val k=key_txt!!.text.toString().length
                Toast.makeText(this, "Key length must be 16 digits not $k ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        finish()
        return
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    btm = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val stream = ByteArrayOutputStream()
                    btm!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    Toast.makeText(this,"Image added",Toast.LENGTH_SHORT).show()
                    decry_img_btn!!.text = "Change Image to Decrypt"

                } catch (e: Exception) {
                    Toast.makeText(this,"Error! Image not added ",Toast.LENGTH_SHORT).show()
                    e.printStackTrace()

                }

            }
        }
    }

    private fun exctract_str_from_img(btm: Bitmap){
            val w: Int = btm.getWidth()
            val h: Int = btm.getHeight()
            var data = IntArray(w * h)
            //getting btm piixel array
            btm.getPixels(data, 0, w, 0, 0, w, h)
            Log.e("w", w.toString())
            Log.e("h", h.toString())

            var count = 0
            var chk = 1
            //modifying pixel data by encoding string(to encode)
            Log.e("r after", (data.get(1) shr 8 and 0xff).toString())
            Log.e("r2 afte", (data.get(2) shr 8 and 0xff).toString())

            for (y in 0 until h) {
                if ((chk == 1) and (valid==1) ){

                    for (x in 0 until w) {
                        val index: Int = y * w + x
                        val R: Int = data.get(index) shr 16 and 0xff //bitwise shifting
                        val G: Int = data.get(index) shr 8 and 0xff
                        val B: Int = data.get(index) and 0xff

                        if (termi(count) or (valid==0)) {
                            chk = 0
                            break
                        } else {
                            termi_decod(R, count)
                            count++
                        }


                        if (termi(count) or (valid==0)) {

                            chk = 0
                            break
                        }
                        else {
                            termi_decod(G, count)
                            count++
                        }

                        if (termi(count) or (valid==0)) {
                            chk = 0
                            break
                        } else {
                            termi_decod(B, count)
                            count++
                        }
                        //Log.e("count3pair",count.toString())
                        //to restore the values after RGB modification, use
                        //next statement
                        data[index] = -0x1000000 or (R shl 16) or (G shl 8) or B
                    }
                } else {
                    break
                }
            }
        
    }

    //decoding rgb pixel value and adding lsb to str
    private fun termi_decod(co: Int, count: Int) {
        val b = Integer.toBinaryString(co)
        if (count<2000){//Log.e("binart", b)
        }
        decode_str = decode_str + b[b.length - 1]
        if (decode_str!!.length==48 ){if (decode_str!=valid_imgornot){valid=0}}
        if(decode_str.length%8==0) {
            val toint = Integer.parseInt(
                decode_str.slice(decode_str.length - 8..decode_str.length - 1),
                2
            )
            val tochr = toint.toChar()
            if(count<2000)
            {Log.e("chr", tochr.toString())
        }}
    }

    //to check terminating symbol
    private  fun termi(count: Int):Boolean{
        if (decode_str.length>=16){
            val termi1=decode_str.slice(decode_str.length - 16..decode_str.length - 9)
            val termi1_int=Integer.parseInt(termi1, 2)
            val termi2=decode_str.slice(decode_str.length - 8..decode_str.length - 1)
            val termi2_int=Integer.parseInt(termi2, 2)
            if((termi1_int==23) and (termi2_int==30)){
                decode_str=decode_str.slice(48..decode_str.length - 16)

                return(true)
            }
        }
        //Log.e("termi_bool",count.toString())
        return false
        }

    private fun convo_to_base64(s: String):String{
        var base64_str:String=""
        for(i in 0..s.length-8 step  8){
            val bin=s.slice(i..i + 7)
            val toint=Integer.parseInt(bin, 2)
            val tochr=toint.toChar()
            base64_str += tochr
        }
        return base64_str
    }

    private  fun decod_str(bs: String):String?{
        val dkey:Key =SecretKeySpec(key_txt!!.text.toString().toByteArray(), "AES")
        val cc:Cipher = Cipher.getInstance("AES")
        cc.init(Cipher.DECRYPT_MODE, dkey)
        var ree:ByteArray?=null
        try {
             ree = cc.doFinal(Base64.decode(bs, Base64.NO_WRAP or Base64.NO_PADDING))
        }
        catch (e: Exception){
            runOnUiThread { Toast.makeText(this, "Wrong security Key", Toast.LENGTH_SHORT).show() }
        }
        val st: String? = ree?.let { String(it) }
        return st
    }
   
    }
