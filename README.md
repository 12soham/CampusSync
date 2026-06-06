🎯 Problem Statement
Every engineering student manually tracks:
"How many classes have I attended?"
"Can I bunk tomorrow's class?"
"What CGPA will I get this semester?"
CampusSync answers all these questions automatically — no manual math, no surprises before exams.
---
✨ Features
📋 Subject Management
Add subjects with semester info
Duplicate subject prevention
Cascade delete — removing a subject auto-deletes its attendance and marks
✅ Smart Attendance Tracker
Mark present/absent with one tap
Live attendance percentage with animated progress bar
Color-coded warnings — 🟢 Safe / 🟡 Warning / 🔴 Critical
Full attendance history with delete support
Push notification when attendance drops below your threshold
📊 Marks & CGPA Calculator
Enter internal and external marks per subject
Automatic grade calculation — O / A+ / A / B+ / B / C / F
Real-time CGPA calculation using 10-point grading scale
Visual CGPA progress bar
🗓️ Weekly Timetable
Add classes with time picker — no manual typing
Day-wise schedule view
Ongoing class indicator — see which class is happening right now
Status badges — ✅ Completed / 🟢 Ongoing / ⏳ Upcoming
Today's schedule shown directly on Dashboard
🏠 Smart Dashboard
Time-based greeting — Good Morning / Afternoon / Evening
Personalized with student name
Today's class schedule at a glance
Quick action buttons — Mark Attendance / Add Marks
Subject overview
⚙️ Settings
Set your student name
Set current semester
Customize attendance warning threshold (default 75%)
All settings persist across app restarts
---
🏗️ Architecture
CampusSync follows MVVM (Model-View-ViewModel) architecture with Repository pattern.
```
UI Layer (Jetpack Compose)
        ↕
ViewModel Layer (StateFlow)
        ↕
Repository Layer
        ↕
Data Layer (Room Database + SharedPreferences)
```
Why MVVM?
Separation of concerns — UI never touches database directly
Survives screen rotation — ViewModel persists configuration changes
Testable — Each layer can be tested independently
Reactive UI — StateFlow + collectAsState() = automatic UI updates
---
🛠️ Tech Stack
Category	Technology
Language	Kotlin
UI Framework	Jetpack Compose
Architecture	MVVM + Repository Pattern
Local Database	Room DB with KSP
Async	Kotlin Coroutines + Flow
State Management	StateFlow + collectAsState()
Navigation	Jetpack Navigation Compose
Preferences	SharedPreferences
Notifications	NotificationCompat
Splash Screen	Core SplashScreen API
Build Tool	Gradle with KSP
---
📁 Project Structure
```
app/
├── data/
│   ├── local/
│   │   ├── CampusSyncDatabase.kt    # Room DB singleton
│   │   ├── SubjectDao.kt            # Subject queries
│   │   ├── AttendanceDao.kt         # Attendance queries
│   │   ├── MarksDao.kt              # Marks queries
│   │   └── TimetableDao.kt          # Timetable queries
│   ├── repository/
│   │   ├── SubjectRepository.kt
│   │   ├── AttendanceRepository.kt
│   │   ├── MarksRepository.kt
│   │   └── TimetableRepository.kt
│   ├── PreferenceManager.kt         # SharedPreferences wrapper
│   └── NotificationHelper.kt        # Notification logic
├── model/
│   ├── Subject.kt                   # Room entity
│   ├── Attendance.kt                # Room entity with ForeignKey
│   ├── Marks.kt                     # Room entity with ForeignKey
│   ├── TimetableEntry.kt            # Room entity with ForeignKey
│   └── ValidationResult.kt          # Sealed class for validation
└── ui/
    ├── screens/
    │   ├── DashboardScreen.kt
    │   ├── SubjectsScreen.kt
    │   ├── AttendanceScreen.kt
    │   ├── MarksScreen.kt
    │   ├── TimetableScreen.kt
    │   ├── SettingsScreen.kt
    │   ├── MainScreen.kt
    │   ├── BottomNavigation.kt
    │   └── Navigation.kt
    └── viewmodel/
        ├── SubjectViewModel.kt
        ├── AttendanceViewModel.kt
        ├── MarksViewModel.kt
        ├── TimetableViewModel.kt
        └── SettingsViewModel.kt
```
---
🗄️ Database Design
```
subjects
├── id (PrimaryKey, autoGenerate)
├── name
├── totalLectures
├── attendedLectures
└── semester

attendance (ForeignKey → subjects.id CASCADE DELETE)
├── id (PrimaryKey, autoGenerate)
├── subjectId
├── date
└── isPresent

marks (ForeignKey → subjects.id CASCADE DELETE)
├── id (PrimaryKey, autoGenerate)
├── subjectId
├── subjectName
├── internalMarks
├── maxInternalMarks
├── externalMarks
├── maxExternalMarks
└── semester

timetable (ForeignKey → subjects.id CASCADE DELETE)
├── id (PrimaryKey, autoGenerate)
├── subjectId
├── subjectName
├── dayOfWeek
├── startTime
├── endTime
└── roomNumber
```
Key Design Decision: All child tables use `CASCADE DELETE` — when a subject is deleted, all its attendance records, marks, and timetable entries are automatically removed. No orphan data.
---
🧮 CGPA Calculation Logic
```
Step 1 — Calculate percentage per subject
percentage = (internalMarks + externalMarks) / (maxInternal + maxExternal) × 100

Step 2 — Convert to grade point (10-point scale)
90–100% → 10    80–89% → 9    70–79% → 8
60–69%  → 7     50–59% → 6    40–49% → 5
below 40% → 0

Step 3 — Average all grade points
CGPA = sum of all grade points / number of subjects
```
---
⚡ Key Technical Decisions
KSP over KAPT
Used KSP (Kotlin Symbol Processing) instead of KAPT for Room annotation processing — faster build times and full Kotlin support.
Job Cancellation for Flow Collectors
When switching subjects in AttendanceScreen, previous Flow collectors are cancelled using `Job` references before starting new ones — prevents stale data and incorrect calculations.
Sealed Class for Validation
Used `ValidationResult` sealed class (Success/Error) instead of throwing exceptions — clean, predictable validation flow from ViewModel to UI.
SharedPreferences for Settings
Used SharedPreferences instead of Room for settings data — appropriate choice for simple key-value data that doesn't need relational queries.
---
🚀 Getting Started
Prerequisites
Android Studio Hedgehog or later
Android SDK 29+
Kotlin 2.0+
Installation
Clone the repository
```bash
git clone https://github.com/yourusername/CampusSync.git
```
Open in Android Studio
Sync Gradle dependencies
Run on device or emulator (API 29+)
---
📋 Validation Rules
Subject Validation
Name cannot be empty
Minimum 2 characters
Semester must be between 1 and 8
No duplicate subject in same semester
Marks Validation
Maximum marks cannot be zero
Marks cannot be negative
Scored marks cannot exceed maximum
No duplicate marks entry for same subject
Timetable Validation
Subject must be selected
Start and end time must be set
No duplicate slot for same subject at same time
---
🎓 About
Built by Soham Bandivdekar  
B.E. Computer Engineering (Honours in AI/ML)  
Vidyavardhini's College of Engineering and Technology
> *"Built CampusSync because I was personally struggling to track attendance across 6 subjects and calculating whether I'd be short before exams. I wanted an app that warns me before it's too late — so I built it."*
