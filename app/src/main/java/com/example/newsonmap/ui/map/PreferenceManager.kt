package com.example.newsonmap.ui.map

import android.content.Context
import com.example.newsonmap.NewsOnMap

class PreferenceManager {

    companion object {
        const val PREFS_FILE = "NewsOnMapPreferences"
        const val PREFS_KEY_MAP_MODE = "Mode"
        const val PREFS_KEY_TIME_LAST_NEWS= "Time"
    }

    fun setMapMode(mapMode: String) {
        val sharedPreferences = NewsOnMap.ApplicationContext.getSharedPreferences(
            PREFS_FILE, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(PREFS_KEY_MAP_MODE, mapMode)
        editor.apply()
    }

    fun getMapMode(): String? {
        val sharedPreferences = NewsOnMap.ApplicationContext.getSharedPreferences(
            PREFS_FILE, Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(PREFS_KEY_MAP_MODE, "NORMAL")
    }

    fun setTimeLastNews(hours: Int) {
        val sharedPreferences = NewsOnMap.ApplicationContext.getSharedPreferences(
            PREFS_FILE, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(PREFS_KEY_TIME_LAST_NEWS, hours)
        editor.apply()
    }

    fun getTimeLastNews(): Int {
        val sharedPreferences = NewsOnMap.ApplicationContext.getSharedPreferences(
            PREFS_FILE, Context.MODE_PRIVATE
        )
        val weekHours = 168
        return sharedPreferences.getInt(PREFS_KEY_TIME_LAST_NEWS, weekHours)
    }

}