package com.example.newsonmap.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.newsonmap.R
import com.example.newsonmap.ui.MainActivity

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "NewsOnMap"
const val messageExtra = "Check what is happening around you"

class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val repeatingIntent = Intent(context, MainActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            repeatingIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_logo_round)
            .setContentTitle(titleExtra)
            .setContentText(messageExtra)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationID, builder)
    }
}