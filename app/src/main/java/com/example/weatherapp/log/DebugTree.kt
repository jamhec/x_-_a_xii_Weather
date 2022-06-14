package com.example.weatherapp.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class DebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element : StackTraceElement) : String? =
        "(${element.fileName} : ${element.lineNumber}) ${element.methodName}"


    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        FirebaseCrashlytics.getInstance().also {
            it.setCustomKey(ReleaseTree.PRIORITY, priority)
            tag?.let { _ -> it.setCustomKey(ReleaseTree.TAG, tag)}
            it.log(message)
            t?.let { e -> it.recordException(e)}
        }.sendUnsentReports()
    }

}