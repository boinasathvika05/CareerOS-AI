package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val id: String = "student_1",
    val name: String = "John Doe",
    val careerGoals: String = "Full Stack Engineer (Spring Boot / Android Kmp)",
    val currentSkills: String = "Kotlin, Java, Jetpack Compose, SQLite",
    val resumeText: String = "",
    val resumeAtsScore: Int = 0,
    val resumeAnalysis: String = "",
    val githubUsername: String = "johndoe-dev",
    val githubAnalysis: String = "",
    val githubScore: Int = 0,
    val leetcodeUsername: String = "johndoe_lc",
    val leetcodeAnalysis: String = "",
    val leetcodeScore: Int = 0,
    val jobReadinessScore: Int = 0,
    val placementProbability: Int = 0,
    val careerReport: String = ""
)

@Entity(tableName = "roadmap_step")
data class RoadmapStep(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: Int,
    val week: Int,
    val title: String,
    val desc: String,
    val type: String, // "Daily Task", "Weekly Goal", "Course", "Project", "Interview Prep"
    val isCompleted: Boolean = false
)

@Entity(tableName = "job_application")
data class JobApplication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val roleName: String,
    val status: String, // "Applied", "OA Round", "Technical Interview", "HR Interview", "Offer", "Rejection"
    val dateApplied: String,
    val notes: String = ""
)

@Entity(tableName = "portfolio_item")
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "Certifications", "Internships", "Projects", "Research papers", "Hackathons", "Awards"
    val title: String,
    val organization: String,
    val description: String,
    val date: String
)

@Entity(tableName = "recruiter_prefs")
data class RecruiterPrefs(
    @PrimaryKey val id: String = "recruiter_1",
    val companyName: String = "Innovate Corp",
    val requirements: String = "Looking for Spring Boot engineer with strong problem solving and 2 years experience.",
    val experienceLevel: String = "Entry to Mid",
    val companyFilters: String = "Required: Java"
)

@Entity(tableName = "interview_invitation")
data class InterviewInvitation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val roleName: String,
    val requiredSkills: String,
    val matchPercentage: Int,
    val status: String, // "Pending", "Accepted", "Rejected", "Scheduled"
    val scheduledTime: String = ""
)

@Entity(tableName = "chat_message")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "User" or "Mentor" or "Interviewer"
    val content: String,
    val type: String, // "Mentor" or "MockInterview"
    val timestamp: Long = System.currentTimeMillis()
)
