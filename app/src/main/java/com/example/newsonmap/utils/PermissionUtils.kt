package com.example.newsonmap.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

fun AppCompatActivity.hasPermissionCompat (permission: String): Boolean{
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun AppCompatActivity.requestPermissionCompat(permission: Array<String>, requestCode: Int){
    ActivityCompat.requestPermissions(this, permission, requestCode)
}

