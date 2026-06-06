package eu.tutoriale.campussync.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutoriale.campussync.model.Subject
import eu.tutoriale.campussync.ui.viewmodel.SubjectViewModel

val NavyDark = Color(0xFF0A0E21)
val NavyMid = Color(0xFF1D1F33)
val CardBg = Color(0xFF252742)
val AccentBlue = Color(0xFF4C6EF5)
val AccentPurple = Color(0xFF7C3AED)
val AccentGreen = Color(0xFF10B981)
val AccentOrange = Color(0xFFF59E0B)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B3C6)

@Composable
fun SubjectsScreen(viewModel: SubjectViewModel) {
    val subjects by viewModel.subjects.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val insertSuccess by viewModel.insertSuccess.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

// Auto close dialog when subject added successfully
    LaunchedEffect(insertSuccess) {
        if (insertSuccess) {
            showAddDialog = false
            viewModel.resetInsertSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Subjects",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${subjects.size} subjects added",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentBlue, AccentPurple)
                            )
                        )
                        .clickable { showAddDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Subject",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (subjects.isEmpty()) {
                EmptySubjectsView()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects) { subject ->
                        SubjectCard(
                            subject = subject,
                            onDelete = { viewModel.deleteSubject(subject) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
        if (showAddDialog) {
            AddSubjectDialog(
                validationError = validationError,
                onDismiss = {
                    showAddDialog = false
                    viewModel.clearValidationError()
                },
                onAdd = { name, semester ->
                    viewModel.insertSubject(name, semester)
                }
            )
        }
    }
}

@Composable
fun SubjectCard(subject: Subject, onDelete: () -> Unit) {
    val accentColors = listOf(AccentBlue, AccentPurple, AccentGreen, AccentOrange)
    val accentColor = accentColors[subject.id % accentColors.size]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Semester ${subject.semester}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun EmptySubjectsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Subjects Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your first subject",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun AddSubjectDialog(
    validationError: String?,
    onDismiss: () -> Unit,
    onAdd: (String, Int) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Add Subject",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Show ViewModel validation error at top
                validationError?.let { error ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⚠️")
                            Text(
                                text = error,
                                fontSize = 13.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it },
                    label = { Text("Semester (1-8)", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val semInt = semester.toIntOrNull() ?: 0
                    onAdd(subjectName, semInt)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}