package com.menna.nabata_7asena.core.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.menna.nabata_7asena.MainActivity
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.core.audio.AudioPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UnifiedAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var audioPlayerManager: AudioPlayerManager

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "نباتاً حسناً"
        val message = intent.getStringExtra("MESSAGE") ?: ""
        val soundId = intent.getIntExtra("SOUND_ID", R.raw.sound_normal)
        val destination = intent.getStringExtra("DESTINATION")

        
        audioPlayerManager.playSequence(context, soundId)

        
        showNotification(context, title, message, destination)
    }

    private fun showNotification(context: Context, title: String, message: String, destination: String?) {
        val channelId = "nabata_unified_v2" 
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تنبيهات التطبيق", NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                setSound(null, null) 
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (destination != null) putExtra("DESTINATION_ROUTE", destination)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}