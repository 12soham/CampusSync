package eu.tutoriale.campussync.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import eu.tutoriale.campussync.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "attendance_warning_channel"
        const val CHANNEL_NAME = "Attendance Warnings"
        const val CHANNEL_DESC = "Alerts when attendance drops below threshold"
    }

    init {
        createNotificationChannel()
    }

    // Creates channel once — required for Android 8+
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESC
        }
        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showAttendanceWarning(
        subjectName: String,
        percentage: Float,
        threshold: Int
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentTitle("⚠️ Attendance Alert — $subjectName")
            .setContentText(
                "$subjectName attendance is ${percentage.toInt()}%" +
                        " — below your $threshold% threshold!"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Use subjectName hashCode as unique notification ID
        manager.notify(subjectName.hashCode(), notification)
    }
}