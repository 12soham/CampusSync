package eu.tutoriale.campussync.data.local

import androidx.room.*
import eu.tutoriale.campussync.model.TimetableEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: TimetableEntry)

    @Delete
    suspend fun deleteEntry(entry: TimetableEntry)

    @Query("SELECT * FROM timetable WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getEntriesForDay(day: String): Flow<List<TimetableEntry>>

    @Query("SELECT * FROM timetable ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllEntries(): Flow<List<TimetableEntry>>
}