package eu.tutoriale.campussync.ui.screens



sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Subjects : Screen("subjects")
    object Attendance : Screen("attendance")
    object Marks : Screen("marks")
    object Timetable : Screen("timetable")
    object Settings : Screen("settings")
    object AddSubject : Screen("add_subject")
}