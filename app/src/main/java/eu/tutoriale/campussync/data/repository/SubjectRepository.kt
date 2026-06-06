package eu.tutoriale.campussync.data.repository



import eu.tutoriale.campussync.data.local.SubjectDao
import eu.tutoriale.campussync.model.Subject
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val subjectDao: SubjectDao) {

    fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

    fun getSubjectsBySemester(semester: Int): Flow<List<Subject>> {
        return subjectDao.getSubjectsBySemester(semester)
    }

    suspend fun insertSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }

    suspend fun deleteSubject(subject: Subject) {
        subjectDao.deleteSubject(subject)
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
    }
}