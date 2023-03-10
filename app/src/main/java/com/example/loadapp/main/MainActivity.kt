package com.example.loadapp.main

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.loadapp.R
import com.example.loadapp.Utils.ButtonState
import com.example.loadapp.Utils.cancelNotifications
import com.example.loadapp.Utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var selectedDownloadUrl: URL
    var downloadStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main)

        setSupportActionBar(toolbar)

        createChannel(
            "channelId",
            "Download"
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

            btn_download.setOnClickListener {
            notificationManager = ContextCompat.getSystemService(applicationContext,
                NotificationManager::class.java) as NotificationManager
            notificationManager.cancelNotifications()
            if (download_radio_group.checkedRadioButtonId == -1)
            {
                Toast.makeText(this, "Select a button", Toast.LENGTH_SHORT).show()
            }
            else {
                val index = download_radio_group.indexOfChild(findViewById(download_radio_group.checkedRadioButtonId))
                selectedDownloadUrl = when(index){
                    0 -> URL.GLIDE
                    1 -> URL.UDACITY
                    else -> URL.RETROFIT
                }
                btn_download.buttonState = ButtonState.Loading
                download(selectedDownloadUrl.url)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent?.action

            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            downloadStatus = if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                "Success"
                            } else {
                                "Fail"
                            }
                        }
                    }

                }
                downloadStatus = "Success"
                btn_download.buttonState = ButtonState.Completed
                notificationManager = ContextCompat.getSystemService(applicationContext,
                NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification(selectedDownloadUrl.appName,downloadStatus,applicationContext)
        }
    }
    }

    private fun download(url: String) {
               val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                   // .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)

        }







    companion object {
        private enum class URL(val url: String, val appName: String, val msg:String){
            GLIDE(
                "https://github.com/bumptech/glide/archive/master.zip",
                "Glide: Image Loading Library By BumpTech",
                "Glide repository has been downloaded"
            ),
            UDACITY(
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
                "Udacity: Android Kotlin Nanodegree",
                "Udacity's third project repository has been downloaded"
            ),
            RETROFIT(
                "https://github.com/square/retrofit/archive/master.zip",
                "Retrofit: Type-safe HTTP client by Square, Inc",
                "Retrofit repository has been downloaded"
            )
        }
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = resources.getString(R.string.notification_description)
            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
