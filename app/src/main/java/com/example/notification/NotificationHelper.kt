package com.example.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    private const val VICTORY_CHANNEL_ID = "game_victory_channel"
    private const val VICTORY_CHANNEL_NAME = "Game Victory & Rewards"
    private const val VICTORY_CHANNEL_DESC = "Notifications for game victories and coin rewards"

    private const val REMINDER_CHANNEL_ID = "game_offline_reminder_channel"
    private const val REMINDER_CHANNEL_NAME = "Game Inactivity Reminders"
    private const val REMINDER_CHANNEL_DESC = "Reminders to come back and play Tic Tac Toe after inactivity"

    private const val VICTORY_NOTIFICATION_BASE_ID = 1000
    private const val OFFLINE_NOTIFICATION_ID = 2000
    private const val ALARM_REQUEST_CODE = 3000

    private const val ONE_HOUR_MILLIS = 60 * 60 * 1000L // 1 hour in milliseconds

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Victory Channel
            val victoryChannel = NotificationChannel(
                VICTORY_CHANNEL_ID,
                VICTORY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = VICTORY_CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 200)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(victoryChannel)

            // 2. Offline Reminder Channel
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                REMINDER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = REMINDER_CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    /**
     * Sends immediate Victory Notification with celebratory text, coins earned, and app intent.
     */
    fun sendVictoryNotification(
        context: Context,
        gameTitle: String,
        coinsEarned: Int,
        message: String? = null
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val displayMessage = message ?: "🏆 Awesome victory in $gameTitle! You earned +$coinsEarned Coins! Tap to claim and start your next match!"

        val notification = NotificationCompat.Builder(context, VICTORY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🎉 VICTORY! Match Won in $gameTitle")
            .setContentText(displayMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(displayMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt() % 1000 + VICTORY_NOTIFICATION_BASE_ID,
                notification
            )
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Permission error sending victory notification", e)
        }
    }

    /**
     * Schedules a background alarm to trigger an offline reminder notification after 1 hour of inactivity.
     */
    fun scheduleOfflineReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, OfflineReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val triggerAtMillis = SystemClock.elapsedRealtime() + ONE_HOUR_MILLIS

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d("NotificationHelper", "Offline reminder scheduled in 1 hour ($triggerAtMillis)")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to schedule offline reminder alarm", e)
        }
    }

    /**
     * Cancels any pending offline reminder alarm when the user is actively playing in the app.
     */
    fun cancelOfflineReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, OfflineReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("NotificationHelper", "Offline reminder alarm cancelled.")
        }
    }

    /**
     * Sends the notification reminding the user to come back and play after 1+ hours of inactivity.
     */
    fun sendOfflineReminderNotification(context: Context) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val titles = listOf(
            "🎮 Game Time! Ready for Tic Tac Toe?",
            "⚡ The Cyber Grid is Waiting For You!",
            "🔥 Break Time! Play a quick Tic Tac Toe match!",
            "👑 Challenge the Master AI & Win Coins!"
        )
        val bodies = listOf(
            "It's been an hour! Come back and claim your victory on the neon board! 🕹️",
            "Your winning streak is waiting! Jump into 3×3, 6×6, 12×12 or 24×24 mode now! ✨",
            "Take a 2-minute break and play Tic Tac Toe with friends or Master AI! ⚔️",
            "Free daily rewards and cyber neon themes are ready for you. Tap to play! 🎁"
        )

        val randomIndex = (System.currentTimeMillis() % titles.size).toInt()
        val title = titles[randomIndex]
        val body = bodies[randomIndex]

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(OFFLINE_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Permission error sending offline reminder notification", e)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
