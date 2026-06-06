package eu.tutoriale.campussync.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eu.tutoriale.campussync.model.Attendance
import eu.tutoriale.campussync.model.Marks
import eu.tutoriale.campussync.model.Subject
import eu.tutoriale.campussync.model.TimetableEntry

@Database(
    entities = [Subject::class, Attendance::class, Marks::class, TimetableEntry::class],
    version = 3,
    exportSchema = false
)
abstract class CampusSyncDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun marksDao(): MarksDao
    abstract fun timetableDao(): TimetableDao

    companion object {
        @Volatile
        private var INSTANCE: CampusSyncDatabase? = null

        fun getDatabase(context: Context): CampusSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusSyncDatabase::class.java,
                    "campussync_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}