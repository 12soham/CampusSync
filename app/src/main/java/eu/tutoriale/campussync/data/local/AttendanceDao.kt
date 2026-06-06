package eu.tutoriale.campussync.data.local


import androidx.room.*
import eu.tutoriale.campussync.model.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE subjectId = :subjectId")
    fun getAttendanceBySubject(subjectId: Int): Flow<List<Attendance>>

    @Query("SELECT COUNT(*) FROM attendance WHERE subjectId = :subjectId AND isPresent = 1")
    fun getPresentCount(subjectId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE subjectId = :subjectId")
    fun getTotalCount(subjectId: Int): Flow<Int>
}