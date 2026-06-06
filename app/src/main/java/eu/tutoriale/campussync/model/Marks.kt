package eu.tutoriale.campussync.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "marks",
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
data class Marks(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val subjectName: String,
    val internalMarks: Float,
    val maxInternalMarks: Float,
    val externalMarks: Float = 0f,
    val maxExternalMarks: Float,
    val semester: Int
)