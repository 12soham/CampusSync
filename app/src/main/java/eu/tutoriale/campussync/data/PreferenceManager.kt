package eu.tutoriale.campussync.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "campussync_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        const val KEY_STUDENT_NAME = "student_name"
        const val KEY_SEMESTER = "current_semester"
        const val KEY_ATTENDANCE_THRESHOLD = "attendance_threshold"
    }

    // Student Name
    var studentName: String
        get() = prefs.getString(KEY_STUDENT_NAME, "Student") ?: "Student"
        set(value) = prefs.edit().putString(KEY_STUDENT_NAME, value).apply()

    // Current Semester
    var currentSemester: Int
        get() = prefs.getInt(KEY_SEMESTER, 1)
        set(value) = prefs.edit().putInt(KEY_SEMESTER, value).apply()

    // Attendance Threshold
    var attendanceThreshold: Int
        get() = prefs.getInt(KEY_ATTENDANCE_THRESHOLD, 75)
        set(value) = prefs.edit().putInt(KEY_ATTENDANCE_THRESHOLD, value).apply()
}