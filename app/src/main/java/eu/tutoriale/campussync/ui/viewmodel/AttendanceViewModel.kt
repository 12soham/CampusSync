package eu.tutoriale.campussync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eu.tutoriale.campussync.data.NotificationHelper
import eu.tutoriale.campussync.data.PreferenceManager
import eu.tutoriale.campussync.data.local.CampusSyncDatabase
import eu.tutoriale.campussync.data.repository.AttendanceRepository
import eu.tutoriale.campussync.model.Attendance
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository
    private val notificationHelper = NotificationHelper(application)
    internal val prefs = PreferenceManager(application)

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList.asStateFlow()

    private val _presentCount = MutableStateFlow(0)
    val presentCount: StateFlow<Int> = _presentCount.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private var currentSubjectName: String = ""

    // Jobs to cancel previous collectors before starting new ones
    private var attendanceJob: Job? = null
    private var presentJob: Job? = null
    private var totalJob: Job? = null

    init {
        val dao = CampusSyncDatabase.getDatabase(application).attendanceDao()
        repository = AttendanceRepository(dao)
    }

    fun loadAttendanceForSubject(subjectId: Int, subjectName: String = "") {
        currentSubjectName = subjectName

        // Cancel previous jobs before starting new ones
        // This prevents multiple collectors running simultaneously
        attendanceJob?.cancel()
        presentJob?.cancel()
        totalJob?.cancel()

        // Reset counts immediately when subject changes
        _presentCount.value = 0
        _totalCount.value = 0
        _attendanceList.value = emptyList()

        attendanceJob = viewModelScope.launch {
            repository.getAttendanceBySubject(subjectId).collect {
                _attendanceList.value = it
            }
        }

        presentJob = viewModelScope.launch {
            repository.getPresentCount(subjectId).collect {
                _presentCount.value = it
            }
        }

        totalJob = viewModelScope.launch {
            repository.getTotalCount(subjectId).collect { total ->
                _totalCount.value = total
                // Check notification only after both counts are settled
                val present = _presentCount.value
                if (total > 0 && present >= 0) {
                    checkAndNotify(present, total)
                }
            }
        }
    }

    fun markAttendance(subjectId: Int, isPresent: Boolean) {
        viewModelScope.launch {
            val today = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(Date())
            val attendance = Attendance(
                subjectId = subjectId,
                date = today,
                isPresent = isPresent
            )
            repository.insertAttendance(attendance)
        }
    }

    fun getAttendancePercentage(): Float {
        val total = _totalCount.value
        val present = _presentCount.value
        if (total == 0) return 0f
        return (present.toFloat() / total.toFloat()) * 100f
    }

    fun deleteAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.deleteAttendance(attendance)
        }
    }

    private fun checkAndNotify(present: Int, total: Int) {
        val percentage = (present.toFloat() / total.toFloat()) * 100f
        val threshold = prefs.attendanceThreshold

        // Only notify when strictly below threshold
        if (percentage < threshold && currentSubjectName.isNotBlank()) {
            notificationHelper.showAttendanceWarning(
                subjectName = currentSubjectName,
                percentage = percentage,
                threshold = threshold
            )
        }
    }
}