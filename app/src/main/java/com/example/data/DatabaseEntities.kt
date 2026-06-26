package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Scholar",
    val studentClass: String = "Class 12",
    val favoriteSubjects: String = "Physics, Chemistry, Mathematics", // Comma-separated
    val studyGoalMinutes: Int = 60,
    val completedMinutes: Int = 15
)

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val originalText: String,
    val formattedContent: String, // Rich HTML or markdown
    val formulas: String = "", // JSON or comma-separated list of key formulas
    val summary: String = "",
    val mindMapPoints: String = "", // Semantically organized bullet points
    val paperStyle: String = "ruled", // "ruled", "grid", "blank"
    val isFavorite: Boolean = false,
    val dateCreated: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "ai"
    val messageText: String,
    val language: String = "en", // "en", "hi", "as"
    val subject: String = "General", // "Physics", "Chemistry", etc.
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val answer: String,
    val subject: String,
    val chapter: String = "General",
    val dateCreated: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // "A", "B", "C", "D"
    val explanation: String = "",
    val subject: String,
    val chapter: String = "General"
)
