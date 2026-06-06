package eu.tutoriale.campussync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eu.tutoriale.campussync.data.local.CampusSyncDatabase
import eu.tutoriale.campussync.data.repository.MarksRepository
import eu.tutoriale.campussync.model.Marks
import eu.tutoriale.campussync.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MarksRepository

    private val _marksList = MutableStateFlow<List<Marks>>(emptyList())
    val marksList: StateFlow<List<Marks>> = _marksList.asStateFlow()

    private val _cgpa = MutableStateFlow(0.0f)
    val cgpa: StateFlow<Float> = _cgpa.asStateFlow()

    // NEW — validation error for UI
    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    // NEW — success event for UI to close dialog
    private val _insertSuccess = MutableStateFlow(false)
    val insertSuccess: StateFlow<Boolean> = _insertSuccess.asStateFlow()

    init {
        val dao = CampusSyncDatabase.getDatabase(application).marksDao()
        repository = MarksRepository(dao)
        loadAllMarks()
    }

    private fun loadAllMarks() {
        viewModelScope.launch {
            repository.getAllMarks().collect { list ->
                _marksList.value = list
                _cgpa.value = calculateCGPA(list)
            }
        }
    }

    fun insertMarks(marks: Marks) {
        when (val result = validateMarks(marks)) {
            is ValidationResult.Error -> {
                _validationError.value = result.message
                _insertSuccess.value = false
            }
            is ValidationResult.Success -> {
                _validationError.value = null
                viewModelScope.launch {
                    repository.insertMarks(marks)
                    // Tell UI insertion was successful
                    _insertSuccess.value = true
                }
            }
        }
    }

    private fun validateMarks(marks: Marks): ValidationResult {

        // Rule 1 — max marks cannot be zero
        if (marks.maxInternalMarks <= 0f) {
            return ValidationResult.Error(
                "Maximum internal marks must be greater than 0"
            )
        }
        if (marks.maxExternalMarks <= 0f) {
            return ValidationResult.Error(
                "Maximum external marks must be greater than 0"
            )
        }

        // Rule 2 — marks cannot be negative
        if (marks.internalMarks < 0f || marks.externalMarks < 0f) {
            return ValidationResult.Error(
                "Marks cannot be negative"
            )
        }

        // Rule 3 — scored cannot exceed maximum
        if (marks.internalMarks > marks.maxInternalMarks) {
            return ValidationResult.Error(
                "Internal marks (${marks.internalMarks}) cannot exceed maximum (${marks.maxInternalMarks})"
            )
        }
        if (marks.externalMarks > marks.maxExternalMarks) {
            return ValidationResult.Error(
                "External marks (${marks.externalMarks}) cannot exceed maximum (${marks.maxExternalMarks})"
            )
        }

        // Rule 4 — duplicate subject check
        val isDuplicate = _marksList.value.any { existing ->
            existing.subjectId == marks.subjectId &&
                    existing.semester == marks.semester
        }
        if (isDuplicate) {
            return ValidationResult.Error(
                "${marks.subjectName} marks already added for Semester ${marks.semester}"
            )
        }

        return ValidationResult.Success
    }

    fun deleteMarks(marks: Marks) {
        viewModelScope.launch {
            repository.deleteMarks(marks)
        }
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun resetInsertSuccess() {
        _insertSuccess.value = false
    }

    private fun percentageToGradePoint(percentage: Float): Float {
        return when {
            percentage >= 90 -> 10f
            percentage >= 80 -> 9f
            percentage >= 70 -> 8f
            percentage >= 60 -> 7f
            percentage >= 50 -> 6f
            percentage >= 40 -> 5f
            else -> 0f
        }
    }

    private fun calculateCGPA(list: List<Marks>): Float {
        if (list.isEmpty()) return 0f
        val gradePoints = list.map { marks ->
            val totalScored = marks.internalMarks + marks.externalMarks
            val totalMax = marks.maxInternalMarks + marks.maxExternalMarks
            val percentage = (totalScored / totalMax) * 100f
            percentageToGradePoint(percentage)
        }
        return gradePoints.average().toFloat()
    }

    fun getSubjectPercentage(marks: Marks): Float {
        val totalScored = marks.internalMarks + marks.externalMarks
        val totalMax = marks.maxInternalMarks + marks.maxExternalMarks
        return (totalScored / totalMax) * 100f
    }
}