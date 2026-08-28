package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class OfflineReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("OfflineReminderReceiver", "Offline reminder triggered after 1 hour of inactivity.")
        NotificationHelper.sendOfflineReminderNotification(context)
        // Schedule next reminder in case user stays away
        NotificationHelper.scheduleOfflineReminder(context)
    }
}
