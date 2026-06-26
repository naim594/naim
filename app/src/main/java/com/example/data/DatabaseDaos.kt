package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<StudentProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfile)
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY dateCreated DESC")
    fun getAllNotes(): Flow<List<StudyNote>>

    @Query("SELECT * FROM study_notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Flow<StudyNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote): Long

    @Update
    suspend fun updateNote(note: StudyNote)

    @Delete
    suspend fun deleteNote(note: StudyNote)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatHistory(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY dateCreated DESC")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE subject = :subject ORDER BY dateCreated DESC")
    fun getFlashcardsBySubject(subject: String): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_questions ORDER BY id DESC")
    fun getAllQuizzes(): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE subject = :subject ORDER BY id DESC")
    fun getQuizzesBySubject(subject: String): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizQuestion)
}
