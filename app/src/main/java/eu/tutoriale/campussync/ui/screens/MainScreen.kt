package eu.tutoriale.campussync.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.tutoriale.campussync.ui.viewmodel.AttendanceViewModel
import eu.tutoriale.campussync.ui.viewmodel.MarksViewModel
import eu.tutoriale.campussync.ui.viewmodel.SettingsViewModel
import eu.tutoriale.campussync.ui.viewmodel.SubjectViewModel
import eu.tutoriale.campussync.ui.viewmodel.TimetableViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val subjectViewModel: SubjectViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()
    val marksViewModel: MarksViewModel = viewModel()
    val timetableViewModel: TimetableViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val subjects by subjectViewModel.subjects.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    Scaffold(
        bottomBar = {
            CampusSyncBottomNav(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = NavyDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route
            ) {
                composable(Screen.Dashboard.route) {
                    val name by settingsViewModel.studentName.collectAsState()
                    val todayEntries by timetableViewModel.todayEntries.collectAsState()
                    DashboardScreen(
                        subjects = subjects,
                        studentName = name,
                        todayEntries = todayEntries,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.Subjects.route) {
                    SubjectsScreen(viewModel = subjectViewModel)
                }
                composable(Screen.Attendance.route) {
                    AttendanceScreen(
                        subjects = subjects,
                        viewModel = attendanceViewModel
                    )
                }
                composable(Screen.Marks.route) {
                    MarksScreen(
                        subjects = subjects,
                        viewModel = marksViewModel
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(viewModel = settingsViewModel)
                }
                composable(Screen.Timetable.route) {
                    TimetableScreen(
                        subjects = subjects,
                        viewModel = timetableViewModel
                    )
                }
            }
        }
    }
}