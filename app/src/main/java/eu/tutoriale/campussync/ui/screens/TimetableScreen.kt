package eu.tutoriale.campussync.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
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
import eu.tutoriale.campussync.model.TimetableEntry
import eu.tutoriale.campussync.ui.viewmodel.TimetableViewModel
import androidx.compose.ui.draw.alpha
import eu.tutoriale.campussync.ui.viewmodel.ClassStatus

@Composable
fun TimetableScreen(
    subjects: List<Subject>,
    viewModel: TimetableViewModel
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val groupedEntries by viewModel.groupedEntries.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val insertSuccess by viewModel.insertSuccess.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val entriesForDay = groupedEntries[selectedDay] ?: emptyList()

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
                        text = "Timetable",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Your weekly schedule",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // Add Button
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
                        contentDescription = "Add Entry",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Day Selector
            DaySelector(
                days = viewModel.daysOfWeek,
                selectedDay = selectedDay,
                onDaySelected = { viewModel.selectDay(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Entries for selected day
            if (entriesForDay.isEmpty()) {
                EmptyTimetableView(day = selectedDay)
            } else {
                Text(
                    text = "$selectedDay Classes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entriesForDay) { entry ->
                        TimetableEntryCard(
                            entry = entry,
                            status = viewModel.getClassStatus(
                                entry.startTime,
                                entry.endTime
                            ),
                            onDelete = { viewModel.deleteEntry(entry) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AddTimetableDialog(
                subjects = subjects,
                days = viewModel.daysOfWeek,
                selectedDay = selectedDay,
                validationError = validationError,
                onDismiss = {
                    showAddDialog = false
                    viewModel.clearValidationError()
                },
                onAdd = { entry ->
                    viewModel.insertEntry(entry)
                }
            )
        }
    }
}

@Composable
fun DaySelector(
    days: List<String>,
    selectedDay: String,
    onDaySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(days) { day ->
            val isSelected = day == selectedDay
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) AccentBlue else CardBg,
                label = "dayColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else TextSecondary,
                label = "textColor"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onDaySelected(day) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    // Show short name Mon, Tue etc.
                    text = day.take(3),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold
                    else FontWeight.Normal,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun TimetableEntryCard(
    entry: TimetableEntry,
    status: ClassStatus,
    onDelete: () -> Unit
) {
    val accentColors = listOf(
        AccentBlue, AccentPurple, AccentGreen, AccentOrange
    )
    val accentColor = accentColors[entry.subjectId % accentColors.size]

    // Status color and label
    val statusColor = when (status) {
        ClassStatus.ONGOING -> AccentGreen
        ClassStatus.COMPLETED -> TextSecondary
        ClassStatus.UPCOMING -> AccentBlue
    }

    val statusLabel = when (status) {
        ClassStatus.ONGOING -> "Ongoing"
        ClassStatus.COMPLETED -> "Completed"
        ClassStatus.UPCOMING -> "Upcoming"
    }

    val statusEmoji = when (status) {
        ClassStatus.ONGOING -> "🟢"
        ClassStatus.COMPLETED -> "✅"
        ClassStatus.UPCOMING -> "⏳"
    }

    // Card is slightly faded if completed
    val cardAlpha = if (status == ClassStatus.COMPLETED) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (status == ClassStatus.ONGOING)
                CardBg.copy(alpha = 1f)
            else
                CardBg
        ),
        border = if (status == ClassStatus.ONGOING)
            androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.5f))
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored left border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Subject Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.subjectName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    // Status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$statusEmoji $statusLabel",
                            fontSize = 10.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${entry.startTime} - ${entry.endTime}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                if (entry.roomNumber.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Room ${entry.roomNumber}",
                        fontSize = 12.sp,
                        color = accentColor
                    )
                }
            }

            // Delete Button
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimetableDialog(
    subjects: List<Subject>,
    days: List<String>,
    selectedDay: String,
    validationError: String?,
    onDismiss: () -> Unit,
    onAdd: (TimetableEntry) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedDayLocal by remember { mutableStateOf(selectedDay) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var roomNumber by remember { mutableStateOf("") }
    var subjectExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Add Class",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Validation Error
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
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "Select Subject",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = subjectExpanded
                            )
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
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false },
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
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                // Day Dropdown
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDayLocal,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = dayExpanded
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false },
                        modifier = Modifier.background(CardBg)
                    ) {
                        days.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day, color = TextPrimary) },
                                onClick = {
                                    selectedDayLocal = day
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                // Time Row
                // Time Row — tap to open picker
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Start Time
                    OutlinedTextField(
                        value = startTime.ifBlank { "Start Time" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Start Time", color = TextSecondary) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { showStartTimePicker = true }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = if (startTime.isBlank())
                                Color(0xFFEF4444).copy(alpha = 0.5f)
                            else
                                TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = if (startTime.isBlank())
                                TextSecondary
                            else
                                TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStartTimePicker = true }
                    )

                    // End Time
                    OutlinedTextField(
                        value = endTime.ifBlank { "End Time" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("End Time", color = TextSecondary) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { showEndTimePicker = true }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = if (endTime.isBlank())
                                Color(0xFFEF4444).copy(alpha = 0.5f)
                            else
                                TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = if (endTime.isBlank())
                                TextSecondary
                            else
                                TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndTimePicker = true }
                    )
                }

// Time Pickers
                if (showStartTimePicker) {
                    CampusSyncTimePicker(
                        title = "Select Start Time",
                        onTimeSelected = {
                            startTime = it
                            showStartTimePicker = false
                        },
                        onDismiss = { showStartTimePicker = false }
                    )
                }

                if (showEndTimePicker) {
                    CampusSyncTimePicker(
                        title = "Select End Time",
                        onTimeSelected = {
                            endTime = it
                            showEndTimePicker = false
                        },
                        onDismiss = { showEndTimePicker = false }
                    )
                }

                // Room Number
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("Room Number (optional)", color = TextSecondary) },
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
                    val subject = selectedSubject ?: return@Button
                    onAdd(
                        TimetableEntry(
                            subjectId = subject.id,
                            subjectName = subject.name,
                            dayOfWeek = selectedDayLocal,
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            roomNumber = roomNumber.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Add",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
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
fun EmptyTimetableView(day: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Classes on $day",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add a class",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusSyncTimePicker(
    title: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = NavyMid,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = TextSecondary,
                        selectorColor = AccentBlue,
                        containerColor = CardBg,
                        timeSelectorSelectedContainerColor = AccentBlue,
                        timeSelectorUnselectedContainerColor = NavyMid,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = TextSecondary,
                        periodSelectorSelectedContainerColor = AccentBlue,
                        periodSelectorUnselectedContainerColor = NavyMid,
                        periodSelectorSelectedContentColor = Color.White,
                        periodSelectorUnselectedContentColor = TextSecondary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Format time as "09:00 AM"
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour < 12) "AM" else "PM"
                    val displayHour = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }
                    val formattedTime = String.format(
                        "%02d:%02d %s",
                        displayHour,
                        minute,
                        amPm
                    )
                    onTimeSelected(formattedTime)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}