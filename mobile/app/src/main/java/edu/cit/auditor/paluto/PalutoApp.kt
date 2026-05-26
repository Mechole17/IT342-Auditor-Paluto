package edu.cit.auditor.paluto

import android.app.Application
import android.content.Context

class PalutoApp : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        private var instance: PalutoApp? = null
        fun getContext(): Context = instance!!.applicationContext
    }
}
