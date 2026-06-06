package eu.tutoriale.campussync.data.repository


import eu.tutoriale.campussync.data.local.MarksDao
import eu.tutoriale.campussync.model.Marks
import kotlinx.coroutines.flow.Flow

class MarksRepository(private val marksDao: MarksDao) {

    fun getAllMarks(): Flow<List<Marks>> {
        return marksDao.getAllMarks()
    }

    fun getMarksBySemester(semester: Int): Flow<List<Marks>> {
        return marksDao.getMarksBySemester(semester)
    }

    fun getMarksBySubject(subjectId: Int): Flow<List<Marks>> {
        return marksDao.getMarksBySubject(subjectId)
    }

    suspend fun insertMarks(marks: Marks) {
        marksDao.insertMarks(marks)
    }

    suspend fun deleteMarks(marks: Marks) {
        marksDao.deleteMarks(marks)
    }

    suspend fun updateMarks(marks: Marks) {
        marksDao.updateMarks(marks)
    }
}