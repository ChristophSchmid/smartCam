package de.c_schmid.smartcam

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.InetAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_take_picture.setOnClickListener {
            GetImage(false, WeakReference(camera_image)).execute()
        }
    }

    class GetImage(private val useFlash: Boolean = false,
                   private val imgView: WeakReference<ImageView>
                   ): AsyncTask<Unit, Int, Unit>() {

        private val MESSAGE_SEND = "SEND_IMG"
        private val TMPFILE = File.createTempFile("IMG", ".png", imgView.get()?.context?.cacheDir)

        var exception: Exception? = null

        override fun onPreExecute() {
            super.onPreExecute()
            Timber.v("Trying to receive image from remote camera")
        }

        override fun doInBackground(vararg params: Unit) {
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
            }
            finally {
                socket.close()
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            Timber.d("Received image from camera to file: $TMPFILE")

            if(exception == null) {
                Timber.v("Loading image into camera_image view")
                Picasso.get().load(TMPFILE).into(imgView.get())
            }
            else {
                Toast.makeText(imgView.get()?.context,"Camera not available",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

