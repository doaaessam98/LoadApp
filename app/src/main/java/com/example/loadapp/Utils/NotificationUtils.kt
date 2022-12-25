package com.example.loadapp.Utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.loadapp.R
import com.example.loadapp.details.DetailsActivity

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(message: String,status: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, DetailsActivity::class.java)
    contentIntent.apply {
        putExtra("fileName", message)
        putExtra("status", status)
    }
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val action = NotificationCompat.Action.Builder(0,"Check the status",contentPendingIntent).build()

    val builder = NotificationCompat.Builder(
        applicationContext,
        "channelId"
    )
        .setSmallIcon(R.drawable.ic_notification_icon_24dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(contentPendingIntent)
        .addAction(action)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}