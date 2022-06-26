package com.example.newsonmap.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.newsonmap.*
import com.example.newsonmap.databinding.ActivityMainBinding
import com.example.newsonmap.notification.Notification
import com.example.newsonmap.notification.channelID
import com.example.newsonmap.notification.notificationID
import com.example.newsonmap.ui.about.AboutFragment
import com.example.newsonmap.ui.account.AccountFragment
import com.example.newsonmap.ui.authentication.LoginActivity
import com.example.newsonmap.ui.list.ListNewsFragment
import com.example.newsonmap.ui.map.MapsFragment
import com.example.newsonmap.utils.hasPermissionCompat
import com.example.newsonmap.utils.requestPermissionCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle


    private val fineLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val permissionRequestCode = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val preferenceManager = PreferenceManager()
//        if (!(preferenceManager.isNotificationSet())) {
//
//        }
        createNotificationChannel()
        startNotificationProcess()

        setupNavigationDrawer()
        askPermissions()


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val description = "Notification channel for NewsOnMap application"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startNotificationProcess() {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(applicationContext, Notification::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

//        val preferenceManager = PreferenceManager()
//        preferenceManager.updateNotificationSet()
    }


    private fun askPermissions() {
        if (hasPermissionCompat(fineLocationPermission) && hasPermissionCompat(storagePermission)) {
            replaceFragment(MapsFragment(), "Map")
        } else {
            requestPermissionCompat(
                arrayOf(fineLocationPermission, storagePermission),
                permissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCode ->
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    replaceFragment(MapsFragment(), "Map")
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Can not use map function, some permission not granted!",
                        Toast.LENGTH_SHORT
                    ).show()
                    replaceFragment(ListNewsFragment(), "List")
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }
    }

    private fun setupNavigationDrawer() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when (it.itemId) {
                R.id.nav_map -> askPermissions()
                R.id.nav_list -> replaceFragment(ListNewsFragment(), it.title.toString())
                R.id.nav_about -> replaceFragment(AboutFragment(), it.title.toString())
                R.id.nav_account -> replaceFragment(AccountFragment(), it.title.toString())
                R.id.nav_logout -> logout()

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
        binding.drawerLayout.closeDrawers()
        setTitle(title)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}