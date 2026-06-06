package eu.tutoriale.campussync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import eu.tutoriale.campussync.data.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceManager(application)

    private val _studentName = MutableStateFlow(prefs.studentName)
    val studentName: StateFlow<String> = _studentName.asStateFlow()

    private val _currentSemester = MutableStateFlow(prefs.currentSemester)
    val currentSemester: StateFlow<Int> = _currentSemester.asStateFlow()

    private val _attendanceThreshold = MutableStateFlow(prefs.attendanceThreshold)
    val attendanceThreshold: StateFlow<Int> = _attendanceThreshold.asStateFlow()

    fun updateStudentName(name: String) {
        prefs.studentName = name
        _studentName.value = name
    }

    fun updateSemester(semester: Int) {
        prefs.currentSemester = semester
        _currentSemester.value = semester
    }

    fun updateAttendanceThreshold(threshold: Int) {
        prefs.attendanceThreshold = threshold
        _attendanceThreshold.value = threshold
    }
}