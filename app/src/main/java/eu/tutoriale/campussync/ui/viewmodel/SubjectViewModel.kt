package eu.tutoriale.campussync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eu.tutoriale.campussync.data.local.CampusSyncDatabase
import eu.tutoriale.campussync.data.repository.SubjectRepository
import eu.tutoriale.campussync.model.Subject
import eu.tutoriale.campussync.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubjectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubjectRepository

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    // NEW — holds validation error message for UI to show
    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val _insertSuccess = MutableStateFlow(false)
    val insertSuccess: StateFlow<Boolean> = _insertSuccess.asStateFlow()

    init {
        val dao = CampusSyncDatabase.getDatabase(application).subjectDao()
        repository = SubjectRepository(dao)
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            repository.getAllSubjects().collect { subjectList ->
                _subjects.value = subjectList
            }
        }
    }

    // NEW — validates before inserting
    fun insertSubject(name: String, semester: Int) {
        when (val result = validateSubject(name, semester)) {
            is ValidationResult.Error -> {
                // Send error message to UI
                _validationError.value = result.message
            }
            is ValidationResult.Success -> {
                _validationError.value = null
                viewModelScope.launch {
                    val subject = Subject(
                        name = name.trim(),
                        semester = semester
                    )
                    repository.insertSubject(subject)
                    _insertSuccess.value = true
                }
            }
        }
    }

    // NEW — all validation rules in one place
    private fun validateSubject(name: String, semester: Int): ValidationResult {

        // Rule 1 — empty name check
        if (name.isBlank()) {
            return ValidationResult.Error("Subject name cannot be empty")
        }

        // Rule 2 — minimum length
        if (name.trim().length < 2) {
            return ValidationResult.Error("Subject name is too short")
        }

        // Rule 3 — semester range
        if (semester !in 1..8) {
            return ValidationResult.Error("Semester must be between 1 and 8")
        }

        // Rule 4 — duplicate check
        val isDuplicate = _subjects.value.any { existing ->
            existing.name.equals(name.trim(), ignoreCase = true) &&
                    existing.semester == semester
        }
        if (isDuplicate) {
            return ValidationResult.Error("$name already exists in Semester $semester")
        }

        return ValidationResult.Success
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // NEW — clears error after UI has shown it
    fun clearValidationError() {
        _validationError.value = null
    }
    fun resetInsertSuccess() {
        _insertSuccess.value = false
    }
}