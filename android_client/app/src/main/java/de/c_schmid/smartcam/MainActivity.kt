package de.c_schmid.smartcam

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var TMPFILE: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TMPFILE = File.createTempFile("IMG", ".png", applicationContext.cacheDir)

        button_take_picture.setOnClickListener {

            Timber.v("Trying to receive image from remote camera")
            val tmpimg = GetImage(false).execute(false).get()
            Timber.d("Received image from camera to file: $tmpimg")
            if(tmpimg != "") {
                Timber.d("Loading image into camera_image view")
                Picasso.get().load(TMPFILE).into(camera_image)
            }
            else {
                Toast.makeText(applicationContext,"Camera not available",Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class GetImage(private val useFlash: Boolean = false): AsyncTask<Boolean, Int, String>() {

        private val MESSAGE_SEND = "SEND_IMG"

        var exception: Exception? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Boolean?): String {
            val socket = Socket(InetAddress.getByName("192.168.178.27"), 12345)
            val outputToCamera = socket.getOutputStream().bufferedWriter()
            val inputFromCamera = socket.getInputStream()
            try {
                outputToCamera.apply {
                    write(MESSAGE_SEND)
                    flush()
                }

                val rcvdBytes = inputFromCamera.readBytes()
                TMPFILE.writeBytes(rcvdBytes)

                socket.close()
            }
            catch(e: IOException) {
                this.exception = e
                return ""
            }
            finally {
                socket.close()
            }

            Timber.d("Successfully received image")
            return TMPFILE.absolutePath
        }
    }
}
