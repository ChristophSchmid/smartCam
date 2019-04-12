package de.c_schmid.smartcam

import android.app.Application
import android.util.Log
import timber.log.Timber





class smartCam: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}