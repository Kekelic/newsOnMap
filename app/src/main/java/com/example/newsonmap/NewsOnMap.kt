package com.example.newsonmap

import android.app.Application
import android.content.Context

class NewsOnMap : Application() {

    companion object {
        lateinit var ApplicationContext: Context private set

    }

    override fun onCreate() {
        super.onCreate()
        ApplicationContext = this
    }


}