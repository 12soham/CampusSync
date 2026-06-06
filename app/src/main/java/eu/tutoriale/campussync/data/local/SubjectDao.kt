package eu.tutoriale.campussync.data.local


import androidx.room.*
import eu.tutoriale.campussync.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM subjects WHERE semester = :semester")
    fun getSubjectsBySemester(semester: Int): Flow<List<Subject>>

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Update
    suspend fun updateSubject(subject: Subject)
}