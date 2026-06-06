package eu.tutoriale.campussync.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timetable",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val subjectName: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val roomNumber: String = ""
)