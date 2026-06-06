package eu.tutoriale.campussync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutoriale.campussync.model.Subject
import eu.tutoriale.campussync.model.TimetableEntry
import eu.tutoriale.campussync.ui.viewmodel.ClassStatus
import eu.tutoriale.campussync.ui.viewmodel.TimetableViewModel
import java.util.Calendar

@Composable
fun DashboardScreen(
    subjects: List<Subject>,
    studentName: String = "Student",
    todayEntries: List<TimetableEntry> = emptyList(),
    onNavigate: (String) -> Unit
) {
    val hour = java.util.Calendar.getInstance(
        java.util.TimeZone.getDefault()
    ).get(java.util.Calendar.HOUR_OF_DAY)

    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val emoji = when {
        hour < 12 -> "☀️"
        hour < 17 -> "🌤️"
        else -> "🌙"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(52.dp))

                // Header Row with Settings button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$greeting, $studentName! $emoji",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "CampusSync",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Settings Icon Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .clickable { onNavigate(Screen.Settings.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GreetingCard(
                    greeting = greeting,
                    emoji = emoji,
                    studentName = studentName
                )
            }

            item {
                QuickStatsRow(subjects = subjects)
            }

            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                QuickActionsRow(onNavigate = onNavigate)
            }

            // Today's Classes Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Classes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = getTodayName(),
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    // View All button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .clickable { onNavigate(Screen.Timetable.route) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (todayEntries.isEmpty()) {
                item {
                    EmptyTodayView(onNavigate = onNavigate)
                }
            } else {
                items(todayEntries.sortedBy { it.startTime }) { entry ->
                    DashboardTimetableCard(entry = entry)
                }
            }

            item {
                Text(
                    text = "Subjects Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (subjects.isEmpty()) {
                item {
                    EmptyDashboardView(onNavigate = onNavigate)
                }
            } else {
                items(subjects) { subject ->
                    DashboardSubjectCard(subject = subject)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Helper function to get today's name
fun getTodayName(): String {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Sunday"
    }
}

@Composable
fun DashboardTimetableCard(entry: TimetableEntry) {
    // Get status using same logic as TimetableViewModel
    val status = getEntryStatus(entry.startTime, entry.endTime)

    val statusColor = when (status) {
        ClassStatus.ONGOING -> AccentGreen
        ClassStatus.COMPLETED -> TextSecondary
        ClassStatus.UPCOMING -> AccentBlue
    }

    val statusLabel = when (status) {
        ClassStatus.ONGOING -> "🟢 Ongoing"
        ClassStatus.COMPLETED -> "✅ Completed"
        ClassStatus.UPCOMING -> "⏳ Upcoming"
    }

    val accentColors = listOf(
        AccentBlue, AccentPurple, AccentGreen, AccentOrange
    )
    val accentColor = accentColors[entry.subjectId % accentColors.size]
    val cardAlpha = if (status == ClassStatus.COMPLETED) 0.6f else 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = if (status == ClassStatus.ONGOING)
            androidx.compose.foundation.BorderStroke(
                1.dp, AccentGreen.copy(alpha = 0.5f)
            )
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor.copy(alpha = cardAlpha))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.subjectName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary.copy(alpha = cardAlpha)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${entry.startTime} - ${entry.endTime}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (entry.roomNumber.isNotBlank()) {
                    Text(
                        text = "Room ${entry.roomNumber}",
                        fontSize = 11.sp,
                        color = accentColor.copy(alpha = cardAlpha)
                    )
                }
            }

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusLabel,
                    fontSize = 11.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Duplicate status logic for Dashboard
// (Dashboard doesn't have access to TimetableViewModel)
fun getEntryStatus(startTime: String, endTime: String): ClassStatus {
    return try {
        val format = java.text.SimpleDateFormat(
            "hh:mm a",
            java.util.Locale.getDefault()
        )
        val now = java.util.Calendar.getInstance()
        val currentTime = format.parse(
            String.format(
                "%02d:%02d %s",
                now.get(java.util.Calendar.HOUR),
                now.get(java.util.Calendar.MINUTE),
                if (now.get(java.util.Calendar.AM_PM) ==
                    java.util.Calendar.AM) "AM" else "PM"
            )
        )
        val start = format.parse(startTime)
        val end = format.parse(endTime)

        when {
            currentTime == null || start == null || end == null ->
                ClassStatus.UPCOMING
            currentTime.before(start) -> ClassStatus.UPCOMING
            currentTime.after(end) -> ClassStatus.COMPLETED
            else -> ClassStatus.ONGOING
        }
    } catch (e: Exception) {
        ClassStatus.UPCOMING
    }
}

@Composable
fun EmptyTodayView(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "📅", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No classes today",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap to set up your timetable",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = { onNavigate(Screen.Timetable.route) }
            ) {
                Text(
                    text = "Go to Timetable →",
                    color = AccentBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun GreetingCard(
    greeting: String,
    emoji: String,
    studentName: String = "Student"
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(AccentBlue, AccentPurple)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Welcome Back! 👋",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your academic\njourney",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Stay on top of attendance & marks",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun QuickStatsRow(subjects: List<Subject>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Subjects",
            value = "${subjects.size}",
            icon = Icons.Default.Book,
            color = AccentBlue
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Semester",
            value = if (subjects.isEmpty()) "-"
            else "${subjects.maxOf { it.semester }}",
            icon = Icons.Default.Star,
            color = AccentPurple
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Status",
            value = if (subjects.isEmpty()) "New" else "Active",
            icon = Icons.Default.CheckCircle,
            color = AccentGreen
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun QuickActionsRow(onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            modifier = Modifier.weight(1f),
            label = "Mark Attendance",
            icon = Icons.Default.CheckCircle,
            gradient = listOf(AccentGreen, Color(0xFF059669)),
            onClick = { onNavigate(Screen.Attendance.route) }
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            label = "Add Marks",
            icon = Icons.Default.Star,
            gradient = listOf(AccentBlue, AccentPurple),
            onClick = { onNavigate(Screen.Marks.route) }
        )
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(colors = gradient))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DashboardSubjectCard(subject: Subject) {
    val accentColors = listOf(
        AccentBlue, AccentPurple, AccentGreen, AccentOrange
    )
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
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Semester ${subject.semester}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Sem ${subject.semester}",
                    fontSize = 12.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyDashboardView(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Let's Get Started!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your subjects to start\ntracking attendance and marks",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onNavigate(Screen.Subjects.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Subjects",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}