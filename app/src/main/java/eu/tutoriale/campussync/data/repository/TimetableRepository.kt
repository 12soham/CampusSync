package eu.tutoriale.campussync.data.repository

import eu.tutoriale.campussync.data.local.TimetableDao
import eu.tutoriale.campussync.model.TimetableEntry
import kotlinx.coroutines.flow.Flow

class TimetableRepository(private val timetableDao: TimetableDao) {

    fun getAllEntries(): Flow<List<TimetableEntry>> {
        return timetableDao.getAllEntries()
    }

    fun getEntriesForDay(day: String): Flow<List<TimetableEntry>> {
        return timetableDao.getEntriesForDay(day)
    }

    suspend fun insertEntry(entry: TimetableEntry) {
        timetableDao.insertEntry(entry)
    }

    suspend fun deleteEntry(entry: TimetableEntry) {
        timetableDao.deleteEntry(entry)
    }
}