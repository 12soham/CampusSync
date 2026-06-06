package eu.tutoriale.campussync.data.repository


import eu.tutoriale.campussync.data.local.AttendanceDao
import eu.tutoriale.campussync.model.Attendance
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    fun getAttendanceBySubject(subjectId: Int): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceBySubject(subjectId)
    }

    fun getPresentCount(subjectId: Int): Flow<Int> {
        return attendanceDao.getPresentCount(subjectId)
    }

    fun getTotalCount(subjectId: Int): Flow<Int> {
        return attendanceDao.getTotalCount(subjectId)
    }

    suspend fun insertAttendance(attendance: Attendance) {
        attendanceDao.insertAttendance(attendance)
    }

    suspend fun deleteAttendance(attendance: Attendance) {
        attendanceDao.deleteAttendance(attendance)
    }
}