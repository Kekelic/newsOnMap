package com.example.newsonmap

import android.app.Application

class NewsOnMap: Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object{
        lateinit var application: Application
    }
}