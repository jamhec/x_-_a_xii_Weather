package com.example.weatherapp.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    companion object {
        const val PRIORITY = "priority"
        const val TAG = "tag"

    }

    override fun isLoggable(tag : String?, priority : Int) : Boolean {
        return !(priority == Log.INFO || priority == Log.VERBOSE || priority == Log.DEBUG)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
         FirebaseCrashlytics.getInstance().also {
             it.setCustomKey(PRIORITY, priority)
             tag?.let { _ -> it.setCustomKey(TAG, tag)}
             it.log(message)
             t?.let { e -> it.recordException(e)}
         }.sendUnsentReports()
    }

}