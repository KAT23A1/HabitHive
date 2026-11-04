package com.example.habithive

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/** Creates the notification channel Android 8+ needs before it will show any notification. */
fun ensureNotificationChannel(ctx: Context) {
    ctx.getSystemService(NotificationManager::class.java).createNotificationChannel(
        NotificationChannel("habit_reminders", "Habit reminders", NotificationManager.IMPORTANCE_HIGH)
    )
}

/** Receives push messages from Firebase and shows them as notifications. */
class HabitMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) { Log.d("FCM", "New token: $token") }

    // runs when a push arrives while the app is open
    override fun onMessageReceived(msg: RemoteMessage) {
        val title = msg.notification?.title ?: msg.data["title"] ?: "HabitHive"
        val body = msg.notification?.body ?: msg.data["body"] ?: ""
        Log.d("FCM", "Message received: $title")
        ensureNotificationChannel(this)
        val open = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val n = NotificationCompat.Builder(this, "habit_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title).setContentText(body)
            .setContentIntent(open).setAutoCancel(true).build()
        getSystemService(NotificationManager::class.java).notify(System.currentTimeMillis().toInt(), n)
    }
}
