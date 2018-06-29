package com.jamessonfaria.projetocomments.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.activity.ListCommentsActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val NOTIFICATION_ID = 1
    private var mNotificationManager: NotificationManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage!!.notification!!.body != null) {
            sendNotification(remoteMessage.notification!!.body)
        }
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(msg: String?) {
        this.mNotificationManager = getSystemService("notification") as NotificationManager
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, ListCommentsActivity::class.java), 0)
        //        MediaPlayer.create(this, R.raw.air_horn).start();
        val mBuilder = NotificationCompat.Builder(this)
        mBuilder.setSmallIcon(getNotificationIcon(mBuilder))
                .setTicker("Hearty365")
                .setContentTitle("Mensagem")
                .setContentText(msg)
                //                  .setStyle(new NotificationCompat.BigTextStyle().bigText("Menssagem do: " +authMessage))
                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_STATUS)
                .setContentInfo("Chat")
                .setContentIntent(contentIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setVibrate(longArrayOf(400, 400))
        mBuilder.setContentIntent(contentIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "channel"
            val NAME = "sao joao"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel(CHANNEL_ID, NAME, importance)
            mNotificationManager!!.createNotificationChannel(channel)
            mBuilder.setChannelId(CHANNEL_ID)
        }
        this.mNotificationManager!!.notify(NOTIFICATION_ID, mBuilder.build())
    }


    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.drawable.logo2
        } else {
            R.drawable.logo2
        }
    }
}