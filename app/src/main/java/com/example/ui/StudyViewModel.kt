package com.example.ui

import android.app.Application
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "StudyViewModel"
    private val database = AppDatabase.getDatabase(application)
    private val repository = StudyRepository(
        database.studentDao(),
        database.studyNoteDao(),
        database.chatDao(),
        database.flashcardDao(),
        database.quizDao()
    )

    // Reactive StateFlows
    val profile = repository.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudentProfile()
    )

    val allNotes = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val chatHistory = repository.chatHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allFlashcards = repository.allFlashcards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allQuizzes = repository.allQuizzes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Loading & Interaction States
    var isChatLoading = mutableStateOf(false)
    var isNoteProcessing = mutableStateOf(false)
    var isFlashcardGenerating = mutableStateOf(false)
    var isQuizGenerating = mutableStateOf(false)
    var isDiagramGenerating = mutableStateOf(false)
    
    var noteStatusText = mutableStateOf("")
    var currentVoiceStatus = mutableStateOf("Idle") // "Idle", "Listening", "Speaking"
    
    // Voice assistance states
    var speakTextTrigger = MutableSharedFlow<String>()

    // Current subject selection for tools
    var selectedSubject = mutableStateOf("Physics")
    var selectedLanguage = mutableStateOf("English")

    // Active Diagram State
    var activeDiagramType = mutableStateOf("circuit") // "circuit", "dna", "cell", "earth_layers"
    var activeDiagramLabels = mutableStateOf<List<String>>(listOf("Battery", "Resistor", "Switch", "Ammeter"))

    init {
        // Initialize student profile with default values if empty
        viewModelScope.launch {
            repository.profile.firstOrNull()?.let {
                if (it == null) {
                    repository.saveProfile(StudentProfile())
                }
            } ?: repository.saveProfile(StudentProfile())
        }
    }

    // Update Student Profile
    fun updateProfile(name: String, studentClass: String, favoriteSubjects: String, goalMinutes: Int) {
        viewModelScope.launch {
            val current = profile.value ?: StudentProfile()
            repository.saveProfile(
                current.copy(
                    name = name,
                    studentClass = studentClass,
                    favoriteSubjects = favoriteSubjects,
                    studyGoalMinutes = goalMinutes
                )
            )
        }
    }

    // Progress Simulation Tracker
    fun addStudyTime(minutes: Int) {
        viewModelScope.launch {
            val current = profile.value ?: StudentProfile()
            val newCompleted = (current.completedMinutes + minutes).coerceAtMost(current.studyGoalMinutes)
            repository.saveProfile(current.copy(completedMinutes = newCompleted))
        }
    }

    // AI Chat Assistant
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        isChatLoading.value = true
        viewModelScope.launch {
            try {
                val model = "gemini-3.5-flash"
                repository.askAssistant(
                    message = text,
                    model = model,
                    language = selectedLanguage.value,
                    subject = selectedSubject.value
                )
            } catch (e: Exception) {
                Log.e(TAG, "Chat request failed", e)
            } finally {
                isChatLoading.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // Multimodal Handwritten Note processing
    fun processHandwrittenNote(title: String, imageBytes: ByteArray, paperStyle: String) {
        isNoteProcessing.value = true
        noteStatusText.value = "Scanning handwritten notes via Gemini vision AI..."
        viewModelScope.launch {
            try {
                repository.parseHandwrittenNote(title, imageBytes, paperStyle)
                noteStatusText.value = "Success! Saved in your premium notebook."
            } catch (e: Exception) {
                noteStatusText.value = "Extraction failed: ${e.message}"
            } finally {
                isNoteProcessing.value = false
            }
        }
    }

    // Smart Notes generator (Text-based topic helper)
    fun generateSmartNotesFromTopic(topic: String) {
        if (topic.trim().isEmpty()) return
        isNoteProcessing.value = true
        noteStatusText.value = "Creating premium gold study notes..."
        viewModelScope.launch {
            try {
                val prompt = """
                    Generate a premium set of study notes for the subject: ${selectedSubject.value} on the topic: "$topic".
                    The notes must:
                    1. Keep comprehensive academic information.
                    2. Include colorful headers, sub-headings, and clear bullet points.
                    3. Highlight important definitions in highlighted blocks.
                    4. Outline important formulas if applicable.
                    5. Include a brief summary section.
                    6. Include a clear mind map outline (bulleted hierarchy).
                    
                    Return your response STRICTLY as a single JSON object with these exact keys:
                    {
                      "rawText": "full study notes overview",
                      "formattedContent": "premium formatted notebook text with colorful HTML headings and structures",
                      "formulas": "formulas and mathematical equations",
                      "summary": "comprehensive study summary",
                      "mindMap": "bulleted mind map hierarchy"
                    }
                    Do not wrap the JSON object inside markdown code blocks (such as ```json). Return raw JSON text directly.
                """.trimIndent()

                val model = "gemini-3.5-flash"
                val responseStr = GeminiService.generateContent(prompt, model)
                
                val cleanedJson = responseStr.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val json = JSONObject(cleanedJson)
                val note = StudyNote(
                    title = "Smart Note: $topic",
                    originalText = json.optString("rawText", "Topic: $topic"),
                    formattedContent = json.optString("formattedContent", "No formatted text"),
                    formulas = json.optString("formulas", ""),
                    summary = json.optString("summary", ""),
                    mindMapPoints = json.optString("mindMap", ""),
                    paperStyle = "ruled"
                )
                repository.insertNote(note)
                noteStatusText.value = "Smart note created successfully!"
            } catch (e: Exception) {
                noteStatusText.value = "Generation failed: ${e.message}"
                // Fallback direct insert
                val fallbackNote = StudyNote(
                    title = "Smart Note: $topic",
                    originalText = "Failed to parse JSON. Raw output below:\n$topic",
                    formattedContent = "Error parsing study note: ${e.message}",
                    summary = "Topic extraction fallback"
                )
                repository.insertNote(fallbackNote)
            } finally {
                isNoteProcessing.value = false
            }
        }
    }

    // AI Flashcards generator
    fun generateFlashcardsFromTopic(topic: String) {
        if (topic.trim().isEmpty()) return
        isFlashcardGenerating.value = true
        viewModelScope.launch {
            try {
                val prompt = """
                    Generate 5 highly important study flashcards for the subject: ${selectedSubject.value} on the topic: "$topic".
                    Return your response STRICTLY as a JSON array where each object has "question" and "answer" keys.
                    Example:
                    [
                      {"question": "What is Coulomb's Law?", "answer": "The force between two charges is directly proportional to the product of charges and inversely proportional to the square of distance between them."},
                      {"question": "Coulomb's Law Formula", "answer": "F = k * (q1 * q2) / r^2"}
                    ]
                    Do not wrap inside markdown code blocks. Return raw JSON text directly.
                """.trimIndent()

                val responseStr = GeminiService.generateContent(prompt, "gemini-3.5-flash")
                val cleanedJson = responseStr.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val jsonArray = JSONArray(cleanedJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val flashcard = Flashcard(
                        question = obj.getString("question"),
                        answer = obj.getString("answer"),
                        subject = selectedSubject.value,
                        chapter = topic
                    )
                    repository.insertFlashcard(flashcard)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Flashcard generation failed", e)
            } finally {
                isFlashcardGenerating.value = false
            }
        }
    }

    // AI MCQ/Quiz Generator
    fun generateQuizFromTopic(topic: String) {
        if (topic.trim().isEmpty()) return
        isQuizGenerating.value = true
        viewModelScope.launch {
            try {
                val prompt = """
                    Generate 4 academic multiple choice questions for the subject: ${selectedSubject.value} on the topic: "$topic".
                    Return your response STRICTLY as a JSON array where each object has:
                    "question", "optionA", "optionB", "optionC", "optionD", "correctAnswer" (which must be exactly "A", "B", "C", or "D"), and "explanation".
                    Example:
                    [
                      {
                        "question": "What is the SI unit of force?",
                        "optionA": "Joule",
                        "optionB": "Newton",
                        "optionC": "Watt",
                        "optionD": "Pascal",
                        "correctAnswer": "B",
                        "explanation": "Newton is the SI unit of force, defined as 1 kg*m/s^2."
                      }
                    ]
                    Do not wrap inside markdown code blocks. Return raw JSON text directly.
                """.trimIndent()

                val responseStr = GeminiService.generateContent(prompt, "gemini-3.5-flash")
                val cleanedJson = responseStr.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val jsonArray = JSONArray(cleanedJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val quiz = QuizQuestion(
                        question = obj.getString("question"),
                        optionA = obj.getString("optionA"),
                        optionB = obj.getString("optionB"),
                        optionC = obj.getString("optionC"),
                        optionD = obj.getString("optionD"),
                        correctAnswer = obj.getString("correctAnswer"),
                        explanation = obj.getString("explanation"),
                        subject = selectedSubject.value,
                        chapter = topic
                    )
                    repository.insertQuiz(quiz)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Quiz generation failed", e)
            } finally {
                isQuizGenerating.value = false
            }
        }
    }

    // AI Diagram Generator
    fun generateDiagramForTopic(topic: String) {
        if (topic.trim().isEmpty()) return
        isDiagramGenerating.value = true
        viewModelScope.launch {
            try {
                val prompt = """
                    Generate diagram metadata for the subject: ${selectedSubject.value} on the topic: "$topic".
                    We want to select one of these canonical interactive schemas to draw on our Canvas: "circuit", "dna", "cell", "earth_layers".
                    Analyze the topic and return a JSON object with:
                    "diagramType" (must be one of: "circuit", "dna", "cell", "earth_layers") and "labels" (a JSON array of 4 key labels corresponding to parts of the diagram).
                    Example:
                    {"diagramType": "cell", "labels": ["Nucleus", "Cytoplasm", "Mitochondria", "Cell Wall"]}
                    Return raw JSON only, no markdown blocks.
                """.trimIndent()

                val responseStr = GeminiService.generateContent(prompt, "gemini-3.5-flash")
                val cleanedJson = responseStr.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val json = JSONObject(cleanedJson)
                activeDiagramType.value = json.getString("diagramType")
                val labelsArray = json.getJSONArray("labels")
                val labelsList = mutableListOf<String>()
                for (i in 0 until labelsArray.length()) {
                    labelsList.add(labelsArray.getString(i))
                }
                activeDiagramLabels.value = labelsList
            } catch (e: Exception) {
                Log.e(TAG, "Diagram generation failed", e)
            } finally {
                isDiagramGenerating.value = false
            }
        }
    }

    // Voice assistant interaction
    fun executeVoiceCommand(command: String) {
        if (command.trim().isEmpty()) return
        currentVoiceStatus.value = "Listening"
        viewModelScope.launch {
            try {
                val prompt = """
                    You are NAIM AI's high-speed voice assistant.
                    The student said: "$command".
                    Provide a ultra-concise, highly educational voice response (maximum 2 sentences) in ${selectedLanguage.value}.
                    Explain it clearly as if speaking aloud.
                """.trimIndent()
                
                val response = GeminiService.generateContent(prompt, "gemini-3.5-flash")
                currentVoiceStatus.value = "Speaking"
                speakTextTrigger.emit(response)
            } catch (e: Exception) {
                currentVoiceStatus.value = "Idle"
            }
        }
    }

    // Note Book PDF Export (draws premium ruled notebook note and exports as file!)
    suspend fun exportNoteAsPdf(note: StudyNote): File? = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 page size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        try {
            // Colors
            val pageBgPaint = Paint().apply { color = 0xFFFFFFFF.toInt() }
            val linePaint = Paint().apply { color = 0xFFE0E0E0.toInt(); strokeWidth = 1f }
            val marginPaint = Paint().apply { color = 0xFFFF9999.toInt(); strokeWidth = 1.5f }
            
            val textPaint = Paint().apply {
                color = 0xFF121212.toInt()
                textSize = 14f
                isAntiAlias = true
            }
            val titlePaint = Paint().apply {
                color = 0xFFC59B27.toInt() // Premium Gold Accent Title
                textSize = 22f
                isFakeBoldText = true
                isAntiAlias = true
            }
            val headingPaint = Paint().apply {
                color = 0xFF121212.toInt()
                textSize = 16f
                isFakeBoldText = true
                isAntiAlias = true
            }

            // Draw premium white paper page
            canvas.drawRect(0f, 0f, 595f, 842f, pageBgPaint)

            // Draw notebook margin red line
            canvas.drawLine(70f, 0f, 70f, 842f, marginPaint)

            // Draw ruled horizontal notebook lines
            var currentY = 100f
            while (currentY < 820f) {
                canvas.drawLine(0f, currentY, 595f, currentY, linePaint)
                currentY += 28f
            }

            // Draw premium notebook title header
            canvas.drawText(note.title, 85f, 75f, titlePaint)
            canvas.drawLine(85f, 85f, 510f, 85f, Paint().apply { color = 0xFFC59B27.toInt(); strokeWidth = 2f })

            // Draw Content onto lines
            var textY = 122f
            
            // Draw summary section
            if (note.summary.isNotEmpty()) {
                canvas.drawText("SUMMARY:", 85f, textY, headingPaint)
                textY += 28f
                val lines = note.summary.chunked(60)
                for (line in lines) {
                    if (textY < 800f) {
                        canvas.drawText(line, 85f, textY, textPaint)
                        textY += 28f
                    }
                }
            }

            // Draw formulas section
            if (note.formulas.isNotEmpty()) {
                textY += 14f
                canvas.drawText("KEY FORMULAS:", 85f, textY, headingPaint)
                textY += 28f
                val lines = note.formulas.split("\n", ",")
                for (line in lines) {
                    if (line.trim().isNotEmpty() && textY < 800f) {
                        canvas.drawText("• ${line.trim()}", 85f, textY, textPaint)
                        textY += 28f
                    }
                }
            }

            // Draw formatted content
            textY += 14f
            canvas.drawText("STUDY NOTES DETAIL:", 85f, textY, headingPaint)
            textY += 28f
            
            // Filter HTML tags or raw text lines
            val rawLines = note.originalText.replace(Regex("<[^>]*>"), "").split("\n")
            for (rawLine in rawLines) {
                if (rawLine.trim().isEmpty()) continue
                val chunkedLines = rawLine.chunked(60)
                for (line in chunkedLines) {
                    if (textY < 800f) {
                        canvas.drawText(line, 85f, textY, textPaint)
                        textY += 28f
                    }
                }
            }

            pdfDocument.finishPage(page)

            // Save PDF to application external files directory
            val context = getApplication<Application>().applicationContext
            val pdfDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val pdfFile = File(pdfDir, "${note.title.replace(" ", "_")}_Notes.pdf")
            
            FileOutputStream(pdfFile).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write PDF", e)
            null
        } finally {
            pdfDocument.close()
        }
    }

    // Delete note
    fun deleteNote(note: StudyNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Toggle note favorite status
    fun toggleFavoriteNote(note: StudyNote) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isFavorite = !note.isFavorite))
        }
    }

    // Add mock quizzes & flashcards if empty for a delightful demo out-of-the-box experience
    fun seedDemoContentIfEmpty() {
        viewModelScope.launch {
            val quizzes = repository.allQuizzes.first()
            if (quizzes.isEmpty()) {
                // Add default Physics quiz
                repository.insertQuiz(
                    QuizQuestion(
                        question = "What is the unit of electric resistance?",
                        optionA = "Ampere",
                        optionB = "Ohm",
                        optionC = "Volt",
                        optionD = "Tesla",
                        correctAnswer = "B",
                        explanation = "Electric resistance is measured in Ohms, representing the ratio of voltage to current (R=V/I).",
                        subject = "Physics",
                        chapter = "Current Electricity"
                    )
                )
                repository.insertQuiz(
                    QuizQuestion(
                        question = "Which law states that pressure is inversely proportional to volume at constant temperature?",
                        optionA = "Charles's Law",
                        optionB = "Boyle's Law",
                        optionC = "Avogadro's Law",
                        optionD = "Gay-Lussac's Law",
                        correctAnswer = "B",
                        explanation = "Boyle's Law states that for a fixed mass of gas at constant temperature, P * V = constant.",
                        subject = "Chemistry",
                        chapter = "Gaseous State"
                    )
                )
            }

            val flashcards = repository.allFlashcards.first()
            if (flashcards.isEmpty()) {
                repository.insertFlashcard(
                    Flashcard(
                        question = "What are Newton's Laws of Motion?",
                        answer = "1. Law of Inertia. 2. F = ma. 3. Action and Reaction are equal and opposite.",
                        subject = "Physics",
                        chapter = "Laws of Motion"
                    )
                )
                repository.insertFlashcard(
                    Flashcard(
                        question = "What is Photosynthesis?",
                        answer = "The biochemical process by which plants use sunlight, carbon dioxide, and water to synthesize food (glucose) and release oxygen.",
                        subject = "Biology",
                        chapter = "Life Processes"
                    )
                )
            }
        }
    }
}
