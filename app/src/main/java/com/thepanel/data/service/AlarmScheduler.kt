package com.thepanel.data.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.thepanel.data.local.AlarmEntity
import java.util.Calendar

class AlarmScheduler(
    private val context: Context
) : AlarmManagerService {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun upsertAlarm(entity: AlarmEntity): Long {
        if (entity.enabled) {
            schedule(entity)
        } else {
            cancel(entity.id)
        }
        return entity.id
    }

    override suspend fun setAlarmEnabled(id: Long, enabled: Boolean) {
        if (!enabled) {
            cancel(id)
        }
    }

    override suspend fun deleteAlarm(id: Long) {
        cancel(id)
    }

    fun schedule(entity: AlarmEntity) {
        val triggerAt = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, entity.hour)
            set(Calendar.MINUTE, entity.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent(entity)
        )
    }

    private fun cancel(id: Long) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                id.toInt(),
                Intent(context, PanelAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun pendingIntent(entity: AlarmEntity): PendingIntent {
        val intent = Intent(context, PanelAlarmReceiver::class.java)
            .putExtra("alarm_id", entity.id)
            .putExtra("alarm_title", entity.title)
            .putExtra("repeat_daily", entity.repeatDaily)
            .putExtra("alarm_hour", entity.hour)
            .putExtra("alarm_minute", entity.minute)
        return PendingIntent.getBroadcast(
            context,
            entity.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class PanelAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("alarm_title").orEmpty().ifBlank { "Panel alarmi" }
        ensureChannel(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText("Alarm zamani geldi")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .build()
        manager.notify(intent.getLongExtra("alarm_id", 0L).toInt(), notification)

        if (intent.getBooleanExtra("repeat_daily", false)) {
            AlarmScheduler(context).schedule(
                AlarmEntity(
                    id = intent.getLongExtra("alarm_id", 0L),
                    title = title,
                    hour = intent.getIntExtra("alarm_hour", 0),
                    minute = intent.getIntExtra("alarm_minute", 0),
                    enabled = true,
                    repeatDaily = true
                )
            )
        }
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Panel Alarms",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "panel_alarms"
    }
}
