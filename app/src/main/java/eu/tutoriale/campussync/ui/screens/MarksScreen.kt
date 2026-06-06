package eu.tutoriale.campussync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutoriale.campussync.model.Marks
import eu.tutoriale.campussync.model.Subject
import eu.tutoriale.campussync.ui.viewmodel.MarksViewModel

@Composable
fun MarksScreen(
    subjects: List<Subject>,
    viewModel: MarksViewModel
) {
    val marksList by viewModel.marksList.collectAsState()
    val cgpa by viewModel.cgpa.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val insertSuccess by viewModel.insertSuccess.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Auto close dialog when insert succeeds
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

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Marks",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${marksList.size} subjects recorded",
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
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Marks",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CGPA Card
            CGPACard(cgpa = cgpa)

            Spacer(modifier = Modifier.height(20.dp))

            if (marksList.isEmpty()) {
                EmptyMarksView()
            } else {
                Text(
                    text = "Subject Marks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(marksList) { marks ->
                        MarksCard(
                            marks = marks,
                            percentage = viewModel.getSubjectPercentage(marks),
                            onDelete = { viewModel.deleteMarks(marks) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        if (showAddDialog) {
            AddMarksDialog(
                subjects = subjects,
                validationError = validationError,
                onDismiss = {
                    showAddDialog = false
                    viewModel.clearValidationError()
                },
                onAdd = { marks ->
                    viewModel.insertMarks(marks)
                }
            )
        }
    }
}

@Composable
fun CGPACard(cgpa: Float) {
    val cgpaColor = when {
        cgpa >= 8f -> AccentGreen
        cgpa >= 6f -> AccentOrange
        else -> Color(0xFFEF4444)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(cgpaColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.2f", cgpa),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = cgpaColor
                    )
                    Text(
                        text = "CGPA",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "Current CGPA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        cgpa >= 8f -> "Excellent performance! 🎉"
                        cgpa >= 6f -> "Good — keep pushing! 💪"
                        cgpa > 0f -> "Needs improvement 📚"
                        else -> "Add marks to see CGPA"
                    },
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (cgpa / 10f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    color = cgpaColor,
                    trackColor = NavyDark
                )
            }
        }
    }
}

@Composable
fun MarksCard(
    marks: Marks,
    percentage: Float,
    onDelete: () -> Unit
) {
    val gradeColor = when {
        percentage >= 70f -> AccentGreen
        percentage >= 50f -> AccentOrange
        else -> Color(0xFFEF4444)
    }

    val grade = when {
        percentage >= 90f -> "O"
        percentage >= 80f -> "A+"
        percentage >= 70f -> "A"
        percentage >= 60f -> "B+"
        percentage >= 50f -> "B"
        percentage >= 40f -> "C"
        else -> "F"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(gradeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = grade,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = marks.subjectName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Semester ${marks.semester}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = NavyDark)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MarksChip(
                    label = "Internal",
                    value = "${marks.internalMarks.toInt()}/${marks.maxInternalMarks.toInt()}"
                )
                MarksChip(
                    label = "External",
                    value = "${marks.externalMarks.toInt()}/${marks.maxExternalMarks.toInt()}"
                )
                MarksChip(
                    label = "Percentage",
                    value = "${percentage.toInt()}%",
                    valueColor = gradeColor
                )
            }
        }
    }
}

@Composable
fun MarksChip(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMarksDialog(
    subjects: List<Subject>,
    validationError: String?,
    onDismiss: () -> Unit,
    onAdd: (Marks) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var internalMarks by remember { mutableStateOf("") }
    var maxInternal by remember { mutableStateOf("") }
    var externalMarks by remember { mutableStateOf("") }
    var maxExternal by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Add Marks",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Validation Error Card
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

                // Subject Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "Select Subject",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextSecondary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBg)
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${subject.name} — Sem ${subject.semester}",
                                        color = TextPrimary
                                    )
                                },
                                onClick = {
                                    selectedSubject = subject
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Internal Marks Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = internalMarks,
                        onValueChange = { internalMarks = it },
                        label = { Text("Internal", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxInternal,
                        onValueChange = { maxInternal = it },
                        label = { Text("Max", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // External Marks Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = externalMarks,
                        onValueChange = { externalMarks = it },
                        label = { Text("External", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxExternal,
                        onValueChange = { maxExternal = it },
                        label = { Text("Max", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val subject = selectedSubject ?: return@Button
                    val intMarks = internalMarks.toFloatOrNull() ?: return@Button
                    val maxInt = maxInternal.toFloatOrNull() ?: return@Button
                    val extMarks = externalMarks.toFloatOrNull() ?: return@Button
                    val maxExt = maxExternal.toFloatOrNull() ?: return@Button
                    onAdd(
                        Marks(
                            subjectId = subject.id,
                            subjectName = subject.name,
                            internalMarks = intMarks,
                            maxInternalMarks = maxInt,
                            externalMarks = extMarks,
                            maxExternalMarks = maxExt,
                            semester = subject.semester
                        )
                    )
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

@Composable
fun EmptyMarksView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Marks Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your subject marks",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}