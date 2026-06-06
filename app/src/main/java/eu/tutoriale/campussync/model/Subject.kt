package eu.tutoriale.campussync.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val totalLectures: Int = 0,
    val attendedLectures: Int = 0,
    val semester: Int
)