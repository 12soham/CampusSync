package eu.tutoriale.campussync.data.local


import androidx.room.*
import eu.tutoriale.campussync.model.Marks
import kotlinx.coroutines.flow.Flow

@Dao
interface MarksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarks(marks: Marks)

    @Delete
    suspend fun deleteMarks(marks: Marks)

    @Update
    suspend fun updateMarks(marks: Marks)

    @Query("SELECT * FROM marks WHERE semester = :semester")
    fun getMarksBySemester(semester: Int): Flow<List<Marks>>

    @Query("SELECT * FROM marks WHERE subjectId = :subjectId")
    fun getMarksBySubject(subjectId: Int): Flow<List<Marks>>

    @Query("SELECT * FROM marks")
    fun getAllMarks(): Flow<List<Marks>>
}