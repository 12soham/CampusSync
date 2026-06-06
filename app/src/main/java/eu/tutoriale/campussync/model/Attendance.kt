package eu.tutoriale.campussync.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
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
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val date: String,
    val isPresent: Boolean
)