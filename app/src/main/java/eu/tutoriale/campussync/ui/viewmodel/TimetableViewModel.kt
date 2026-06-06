package eu.tutoriale.campussync.ui.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eu.tutoriale.campussync.data.local.CampusSyncDatabase
import eu.tutoriale.campussync.data.repository.TimetableRepository
import eu.tutoriale.campussync.model.TimetableEntry
import eu.tutoriale.campussync.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ClassStatus {
    UPCOMING,
    ONGOING,
    COMPLETED
}

class TimetableViewModel(application: Application) : AndroidViewModel(application) {



    private val repository: TimetableRepository

    // All entries grouped by day
    private val _groupedEntries = MutableStateFlow<Map<String, List<TimetableEntry>>>(emptyMap())
    val groupedEntries: StateFlow<Map<String, List<TimetableEntry>>> = _groupedEntries.asStateFlow()

    // Today's entries
    private val _todayEntries = MutableStateFlow<List<TimetableEntry>>(emptyList())
    val todayEntries: StateFlow<List<TimetableEntry>> = _todayEntries.asStateFlow()

    // Currently selected day in UI
    private val _selectedDay = MutableStateFlow(getTodayName())
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    // Validation error
    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    // Insert success flag
    private val _insertSuccess = MutableStateFlow(false)
    val insertSuccess: StateFlow<Boolean> = _insertSuccess.asStateFlow()

    // Days of week in order
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday"
    )

    init {
        val dao = CampusSyncDatabase.getDatabase(application).timetableDao()
        repository = TimetableRepository(dao)
        loadAllEntries()
    }

    private fun loadAllEntries() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                // Group entries by day
                _groupedEntries.value = entries.groupBy { it.dayOfWeek }
                // Filter today's entries
                _todayEntries.value = entries.filter {
                    it.dayOfWeek == getTodayName()
                }
            }
        }
    }

    fun selectDay(day: String) {
        _selectedDay.value = day
    }

    fun getEntriesForSelectedDay(): List<TimetableEntry> {
        return _groupedEntries.value[_selectedDay.value] ?: emptyList()
    }

    fun insertEntry(entry: TimetableEntry) {
        when (val result = validateEntry(entry)) {
            is ValidationResult.Error -> {
                _validationError.value = result.message
                _insertSuccess.value = false
            }
            is ValidationResult.Success -> {
                _validationError.value = null
                viewModelScope.launch {
                    repository.insertEntry(entry)
                    _insertSuccess.value = true
                }
            }
        }
    }

    private fun validateEntry(entry: TimetableEntry): ValidationResult {

        // Rule 1 — subject must be selected
        if (entry.subjectName.isBlank()) {
            return ValidationResult.Error("Please select a subject")
        }

        // Rule 2 — start time must be set
        if (entry.startTime.isBlank()) {
            return ValidationResult.Error("Please set start time")
        }

        // Rule 3 — end time must be set
        if (entry.endTime.isBlank()) {
            return ValidationResult.Error("Please set end time")
        }

        // Rule 4 — check for duplicate slot
        val existing = _groupedEntries.value[entry.dayOfWeek] ?: emptyList()
        val isDuplicate = existing.any {
            it.subjectId == entry.subjectId &&
                    it.startTime == entry.startTime
        }
        if (isDuplicate) {
            return ValidationResult.Error(
                "${entry.subjectName} already scheduled at ${entry.startTime} on ${entry.dayOfWeek}"
            )
        }

        return ValidationResult.Success
    }

    fun deleteEntry(entry: TimetableEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun resetInsertSuccess() {
        _insertSuccess.value = false
    }

    // Gets today's day name from Calendar
    private fun getTodayName(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Monday"
        }
    }

    fun getClassStatus(startTime: String, endTime: String): ClassStatus {
        return try {
            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            val now = java.util.Calendar.getInstance()
            val currentTime = format.parse(
                String.format(
                    "%02d:%02d %s",
                    now.get(java.util.Calendar.HOUR),
                    now.get(java.util.Calendar.MINUTE),
                    if (now.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"
                )
            )
            val start = format.parse(startTime)
            val end = format.parse(endTime)

            when {
                currentTime == null || start == null || end == null -> ClassStatus.UPCOMING
                currentTime.before(start) -> ClassStatus.UPCOMING
                currentTime.after(end) -> ClassStatus.COMPLETED
                else -> ClassStatus.ONGOING
            }
        } catch (e: Exception) {
            ClassStatus.UPCOMING
        }
    }
}