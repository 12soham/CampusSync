package eu.tutoriale.campussync.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutoriale.campussync.model.Attendance
import eu.tutoriale.campussync.model.Subject
import eu.tutoriale.campussync.ui.viewmodel.AttendanceViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun AttendanceScreen(
    subjects: List<Subject>,
    viewModel: AttendanceViewModel
) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    val attendanceList by viewModel.attendanceList.collectAsState()
    val presentCount by viewModel.presentCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()

    LaunchedEffect(selectedSubject) {
        selectedSubject?.let {
            viewModel.loadAttendanceForSubject(it.id, it.name)
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
            Text(
                text = "Attendance",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Track your lectures",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (subjects.isEmpty()) {
                EmptyAttendanceView()
            } else {
                // Subject Selector
                SubjectSelector(
                    subjects = subjects,
                    selectedSubject = selectedSubject,
                    onSubjectSelected = { selectedSubject = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                selectedSubject?.let { subject ->
                    // Attendance Stats Card
                    AttendanceStatsCard(
                        presentCount = presentCount,
                        totalCount = totalCount,
                        percentage = viewModel.getAttendancePercentage()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mark Attendance Buttons
                    MarkAttendanceButtons(
                        onPresent = {
                            viewModel.markAttendance(subject.id, true)
                        },
                        onAbsent = {
                            viewModel.markAttendance(subject.id, false)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Attendance History
                    Text(
                        text = "History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(attendanceList.reversed()) { attendance ->
                            AttendanceHistoryItem(
                                attendance = attendance,
                                onDelete = {
                                    viewModel.deleteAttendance(attendance)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSelector(
    subjects: List<Subject>,
    selectedSubject: Subject?,
    onSubjectSelected: (Subject) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedSubject?.name ?: "Select a subject",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = if (selectedSubject != null) TextPrimary else TextSecondary,
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
                            text = "${subject.name} — Sem ${subject.semester}",
                            color = TextPrimary
                        )
                    },
                    onClick = {
                        onSubjectSelected(subject)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceStatsCard(
    presentCount: Int,
    totalCount: Int,
    percentage: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    val progressColor = when {
        percentage >= 75f -> AccentGreen
        percentage >= 60f -> AccentOrange
        else -> Color(0xFFEF4444)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attendance",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${percentage.toInt()}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }

                // Stats
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatChip(
                        label = "Present",
                        value = "$presentCount",
                        color = AccentGreen
                    )
                    StatChip(
                        label = "Absent",
                        value = "${totalCount - presentCount}",
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = progressColor,
                trackColor = NavyDark,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Warning
            if (percentage < 75f && totalCount > 0) {
                Text(
                    text = "⚠️ Below 75% — attendance is critical!",
                    fontSize = 12.sp,
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Medium
                )
            } else if (percentage >= 75f && totalCount > 0) {
                Text(
                    text = "✅ Good attendance — keep it up!",
                    fontSize = 12.sp,
                    color = AccentGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun MarkAttendanceButtons(
    onPresent: () -> Unit,
    onAbsent: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Present Button
        Button(
            onClick = onPresent,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentGreen
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Present",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        // Absent Button
        Button(
            onClick = onAbsent,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF4444)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Absent",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun AttendanceHistoryItem(
    attendance: Attendance,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (attendance.isPresent) AccentGreen
                        else Color(0xFFEF4444)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = attendance.date,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (attendance.isPresent) "Present" else "Absent",
                fontSize = 13.sp,
                color = if (attendance.isPresent) AccentGreen else Color(0xFFEF4444),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyAttendanceView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Subjects Found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add subjects first to track attendance",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}


