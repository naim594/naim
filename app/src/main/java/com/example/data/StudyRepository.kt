package com.example.data

import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class StudyRepository(
    private val studentDao: StudentDao,
    private val studyNoteDao: StudyNoteDao,
    private val chatDao: ChatDao,
    private val flashcardDao: FlashcardDao,
    private val quizDao: QuizDao
) {
    val profile: Flow<StudentProfile?> = studentDao.getProfile()
    val allNotes: Flow<List<StudyNote>> = studyNoteDao.getAllNotes()
    val chatHistory: Flow<List<ChatMessage>> = chatDao.getChatHistory()
    val allFlashcards: Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()
    val allQuizzes: Flow<List<QuizQuestion>> = quizDao.getAllQuizzes()

    fun getNoteById(id: Long): Flow<StudyNote?> = studyNoteDao.getNoteById(id)
    fun getFlashcardsBySubject(subject: String): Flow<List<Flashcard>> = flashcardDao.getFlashcardsBySubject(subject)
    fun getQuizzesBySubject(subject: String): Flow<List<QuizQuestion>> = quizDao.getQuizzesBySubject(subject)

    suspend fun saveProfile(profile: StudentProfile) = studentDao.insertProfile(profile)
    suspend fun insertNote(note: StudyNote): Long = studyNoteDao.insertNote(note)
    suspend fun updateNote(note: StudyNote) = studyNoteDao.updateNote(note)
    suspend fun deleteNote(note: StudyNote) = studyNoteDao.deleteNote(note)
    suspend fun insertFlashcard(fc: Flashcard) = flashcardDao.insertFlashcard(fc)
    suspend fun deleteFlashcard(fc: Flashcard) = flashcardDao.deleteFlashcard(fc)
    suspend fun insertQuiz(q: QuizQuestion) = quizDao.insertQuiz(q)
    suspend fun clearChat() = chatDao.clearChatHistory()

    suspend fun askAssistant(message: String, model: String, language: String, subject: String): String {
        // 1. Insert user message in database
        val userMsg = ChatMessage(sender = "user", messageText = message, language = language, subject = subject)
        chatDao.insertMessage(userMsg)

        // 2. Build system instruction based on academic focus
        val systemInstruction = """
            You are "NAIM AI", a luxury, highly professional academic assistant designed specifically for students.
            You solve Physics, Chemistry, Mathematics, Biology, and English questions.
            Always explain concepts step-by-step with supreme pedagogical clarity.
            The user preferred language is $language (which can be English, Hindi, or Assamese).
            If the language is Hindi, respond in Hindi. If Assamese, respond in Assamese. Otherwise, respond in English.
            Use clear headings, bullet points, and highlighted boxes.
            If presenting formulas, present them with clear definitions.
        """.trimIndent()

        // 3. Request Gemini
        val response = GeminiService.generateContent(message, model, systemInstruction)

        // 4. Save AI response to DB
        val aiMsg = ChatMessage(sender = "ai", messageText = response, language = language, subject = subject)
        chatDao.insertMessage(aiMsg)

        return response
    }

    suspend fun parseHandwrittenNote(title: String, imageBytes: ByteArray, paperStyle: String): Long {
        val prompt = """
            Extract and format this handwritten note image.
            You must:
            1. Extract ALL content verbatim, omitting nothing.
            2. Correct grammar and formatting mistakes.
            3. Organize information with clear headings.
            4. Extract any scientific or mathematical formulas with descriptions.
            5. Create a comprehensive chapter/page summary.
            6. Suggest a checklist of mind-map points for visual memory.
            
            Return your response STRICTLY as a single JSON object with these exact keys:
            {
              "rawText": "full verbatim extracted text",
              "formattedContent": "premium formatted notebook text with colorful HTML headings and structures",
              "formulas": "formulas separated by commas or newlines",
              "summary": "comprehensive study summary",
              "mindMap": "bulleted mind map points"
            }
            Do not wrap the JSON object inside markdown code blocks (such as ```json). Return raw JSON text directly.
        """.trimIndent()

        val jsonResponseStr = GeminiService.generateContentWithImage(prompt, imageBytes, "image/jpeg", "gemini-3.5-flash")
        
        // Clean markdown blocks if Gemini accidentally returned them
        val cleanedJson = jsonResponseStr.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val json = JSONObject(cleanedJson)
            val note = StudyNote(
                title = title.ifEmpty { "Extracted Note" },
                originalText = json.optString("rawText", "Extracted note content"),
                formattedContent = json.optString("formattedContent", "No formatted text"),
                formulas = json.optString("formulas", ""),
                summary = json.optString("summary", ""),
                mindMapPoints = json.optString("mindMap", ""),
                paperStyle = paperStyle
            )
            studyNoteDao.insertNote(note)
        } catch (e: Exception) {
            // Fallback if JSON parsing fails
            val fallbackNote = StudyNote(
                title = title.ifEmpty { "Extracted Note" },
                originalText = jsonResponseStr,
                formattedContent = jsonResponseStr,
                summary = "Extracted from image notes",
                paperStyle = paperStyle
            )
            studyNoteDao.insertNote(fallbackNote)
        }
    }
}
