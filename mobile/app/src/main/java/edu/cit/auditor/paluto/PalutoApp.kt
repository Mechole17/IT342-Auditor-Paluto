package edu.cit.auditor.paluto

import android.app.Application
import android.content.Context

class PalutoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: PalutoApp? = null
        fun getContext(): Context = instance!!.applicationContext
    }
}
