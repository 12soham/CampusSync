package eu.tutoriale.campussync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutoriale.campussync.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val studentName by viewModel.studentName.collectAsState()
    val currentSemester by viewModel.currentSemester.collectAsState()
    val attendanceThreshold by viewModel.attendanceThreshold.collectAsState()

    var showNameDialog by remember { mutableStateOf(false) }
    var showSemesterDialog by remember { mutableStateOf(false) }
    var showThresholdDialog by remember { mutableStateOf(false) }

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
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Customize your experience",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Section
            Text(
                text = "PROFILE",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Person,
                    iconColor = AccentBlue,
                    title = "Student Name",
                    subtitle = studentName,
                    onClick = { showNameDialog = true }
                )
                HorizontalDivider(
                    color = NavyDark,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SettingsItem(
                    icon = Icons.Default.School,
                    iconColor = AccentPurple,
                    title = "Current Semester",
                    subtitle = "Semester $currentSemester",
                    onClick = { showSemesterDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Academic Section
            Text(
                text = "ACADEMIC",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Warning,
                    iconColor = AccentOrange,
                    title = "Attendance Warning",
                    subtitle = "Warn below $attendanceThreshold%",
                    onClick = { showThresholdDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Info Section
            Text(
                text = "APP INFO",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Info,
                    iconColor = AccentGreen,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = {}
                )
                HorizontalDivider(
                    color = NavyDark,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SettingsItem(
                    icon = Icons.Default.Code,
                    iconColor = AccentBlue,
                    title = "Built with",
                    subtitle = "Kotlin + Jetpack Compose + Room",
                    onClick = {}
                )
            }
        }
    }

    // Dialogs
    if (showNameDialog) {
        EditTextDialog(
            title = "Student Name",
            currentValue = studentName,
            onDismiss = { showNameDialog = false },
            onSave = {
                viewModel.updateStudentName(it)
                showNameDialog = false
            }
        )
    }

    if (showSemesterDialog) {
        EditNumberDialog(
            title = "Current Semester",
            currentValue = currentSemester,
            range = 1..8,
            onDismiss = { showSemesterDialog = false },
            onSave = {
                viewModel.updateSemester(it)
                showSemesterDialog = false
            }
        )
    }

    if (showThresholdDialog) {
        EditNumberDialog(
            title = "Attendance Warning %",
            currentValue = attendanceThreshold,
            range = 50..90,
            onDismiss = { showThresholdDialog = false },
            onSave = {
                viewModel.updateAttendanceThreshold(it)
                showThresholdDialog = false
            }
        )
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun EditTextDialog(
    title: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
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
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onSave(text.trim()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", color = Color.White)
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
fun EditNumberDialog(
    title: String,
    currentValue: Int,
    range: IntRange,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$value",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = value.toFloat(),
                    onValueChange = { value = it.toInt() },
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    steps = range.last - range.first - 1,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentBlue,
                        activeTrackColor = AccentBlue,
                        inactiveTrackColor = TextSecondary.copy(alpha = 0.3f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${range.first}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${range.last}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(value) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}