package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Flashcard
import com.example.data.QuizQuestion
import com.example.ui.StudyViewModel
import com.example.ui.components.DiagramCanvas
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0 = Diagrams, 1 = Flashcards, 2 = Quizzes
    var topicInput by remember { mutableStateOf("") }

    val isDiagramGenerating by viewModel.isDiagramGenerating
    val activeDiagramType by viewModel.activeDiagramType
    val activeDiagramLabels by viewModel.activeDiagramLabels

    val flashcardsList by viewModel.allFlashcards.collectAsState()
    val isFlashcardGenerating by viewModel.isFlashcardGenerating

    val quizzesList by viewModel.allQuizzes.collectAsState()
    val isQuizGenerating by viewModel.isQuizGenerating

    // Active Flashcard Pager pointer
    var currentFlashcardIndex by remember { mutableStateOf(0) }
    var isFlashcardFlipped by remember { mutableStateOf(false) }

    // Active Quiz pointer
    var currentQuizIndex by remember { mutableStateOf(0) }
    var selectedQuizOption by remember { mutableStateOf<String?>(null) } // "A", "B", "C", "D"
    var isQuizAnswerSubmitted by remember { mutableStateOf(false) }

    // Seed default demo tools if empty
    LaunchedEffect(Unit) {
        viewModel.seedDemoContentIfEmpty()
    }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBlack),
                title = {
                    Text(
                        text = "ACADEMIC LABS & TOOLS",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High-Tech Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = activeSubTab,
                containerColor = CardBackground,
                contentColor = PremiumGold,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = PremiumGold
                    )
                }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("AI Animated Diagrams", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Flashcards", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = { Text("MCQ Quizzes", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unified Generator Input Box at top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = when (activeSubTab) {
                            0 -> "GENERATE ANIMATED SCIENTIFIC DIAGRAMS"
                            1 -> "GENERATE CUSTOM COGNITIVE FLASHCARDS"
                            else -> "GENERATE REAL-TIME ASSESSMENT QUIZ"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            placeholder = { Text("Enter topic (e.g. Electric circuit, DNA helix, Cell...)", fontSize = 12.sp, color = MutedGray) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PremiumGold,
                                unfocusedBorderColor = GoldBorder,
                                focusedTextColor = SoftWhite,
                                unfocusedTextColor = SoftWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (topicInput.trim().isNotEmpty()) {
                                    when (activeSubTab) {
                                        0 -> viewModel.generateDiagramForTopic(topicInput)
                                        1 -> {
                                            viewModel.generateFlashcardsFromTopic(topicInput)
                                            isFlashcardFlipped = false
                                        }
                                        2 -> {
                                            viewModel.generateQuizFromTopic(topicInput)
                                            selectedQuizOption = null
                                            isQuizAnswerSubmitted = false
                                        }
                                    }
                                    topicInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(PremiumGold, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = PureBlack)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs Content Viewport
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (activeSubTab) {
                    0 -> {
                        // --- AI DIAGRAM LAB ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (isDiagramGenerating) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = PremiumGold)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Gemini is assembling vector schematic elements...", color = LightGold, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                // Draw the dynamic Canvas!
                                DiagramCanvas(
                                    diagramType = activeDiagramType,
                                    labels = activeDiagramLabels
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Explanatory box
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "🔬 Animated Diagram Metadata",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PremiumGold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Subject: ${viewModel.selectedSubject.value} • Labeled Parts: ${activeDiagramLabels.joinToString()}",
                                            fontSize = 12.sp,
                                            color = SoftWhite
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "This premium vector diagram features live-drawn custom layout vectors and golden moving particles to help visualize complex subject topics for high cognitive retention in exam formats.",
                                            fontSize = 11.sp,
                                            color = MutedGray,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // --- FLASHCARDS CENTER ---
                        if (isFlashcardGenerating) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = PremiumGold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Compiling cognitive flashcards...", color = LightGold, fontSize = 12.sp)
                                }
                            }
                        } else if (flashcardsList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No flashcards saved yet.", color = MutedGray)
                            }
                        } else {
                            // Render Active Flashcard with Flip Animation!
                            val flashcard = flashcardsList.getOrNull(currentFlashcardIndex.coerceIn(0, flashcardsList.size - 1))
                            if (flashcard != null) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Animated Float for Rotation
                                    val rotation by animateFloatAsState(
                                        targetValue = if (isFlashcardFlipped) 180f else 0f,
                                        animationSpec = tween(500),
                                        label = "flip"
                                    )

                                    // Interactive Flashcard Box with Flip Projection
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .graphicsLayer {
                                                rotationY = rotation
                                                cameraDistance = 8 * density
                                            }
                                            .clickable { isFlashcardFlipped = !isFlashcardFlipped }
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF141414))
                                            .border(1.5.dp, PremiumGold, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (rotation <= 90f) {
                                            // Front Side: Question
                                            Column(
                                                modifier = Modifier.padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("🎓 QUESTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedGold)
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = flashcard.question,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SoftWhite,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(24.dp))
                                                Text("TAP CARD TO REVEAL ANSWER", fontSize = 9.sp, color = MutedGray)
                                            }
                                        } else {
                                            // Back Side: Answer (rotated 180 so it appears correctly!)
                                            Column(
                                                modifier = Modifier
                                                    .padding(24.dp)
                                                    .graphicsLayer { rotationY = 180f },
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("✅ CORRECT ANSWER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PremiumGold)
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = flashcard.answer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = LightGold,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(20.dp))
                                                Text("TAP TO REVERT", fontSize = 9.sp, color = MutedGray)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Controls Bar
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (currentFlashcardIndex > 0) {
                                                    currentFlashcardIndex--
                                                    isFlashcardFlipped = false
                                                }
                                            },
                                            enabled = currentFlashcardIndex > 0,
                                            modifier = Modifier.background(CardBackground, RoundedCornerShape(24.dp))
                                        ) {
                                            Icon(Icons.Default.ArrowBack, contentDescription = "Prev", tint = PremiumGold)
                                        }

                                        Text(
                                            text = "${currentFlashcardIndex + 1} / ${flashcardsList.size}",
                                            color = SoftWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        IconButton(
                                            onClick = {
                                                if (currentFlashcardIndex < flashcardsList.size - 1) {
                                                    currentFlashcardIndex++
                                                    isFlashcardFlipped = false
                                                }
                                            },
                                            enabled = currentFlashcardIndex < flashcardsList.size - 1,
                                            modifier = Modifier.background(CardBackground, RoundedCornerShape(24.dp))
                                        ) {
                                            Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = PremiumGold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // --- MCQ QUIZ TERMINAL ---
                        if (isQuizGenerating) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = PremiumGold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Assembling academic MCQ quizzes...", color = LightGold, fontSize = 12.sp)
                                }
                            }
                        } else if (quizzesList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No quiz questions saved yet.", color = MutedGray)
                            }
                        } else {
                            val quiz = quizzesList.getOrNull(currentQuizIndex.coerceIn(0, quizzesList.size - 1))
                            if (quiz != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Quiz Header Status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "QUESTION ${currentQuizIndex + 1} OF ${quizzesList.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MutedGold
                                        )
                                        Text(
                                            text = quiz.subject.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PremiumGold,
                                            modifier = Modifier
                                                .background(Color(0xFF221E14), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Question text box
                                    Text(
                                        text = quiz.question,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftWhite,
                                        lineHeight = 22.sp
                                    )

                                    // Option choices
                                    QuizOptionItem(
                                        label = "A",
                                        text = quiz.optionA,
                                        selected = selectedQuizOption == "A",
                                        submitted = isQuizAnswerSubmitted,
                                        isCorrect = quiz.correctAnswer == "A",
                                        onClick = {
                                            if (!isQuizAnswerSubmitted) {
                                                selectedQuizOption = "A"
                                            }
                                        }
                                    )

                                    QuizOptionItem(
                                        label = "B",
                                        text = quiz.optionB,
                                        selected = selectedQuizOption == "B",
                                        submitted = isQuizAnswerSubmitted,
                                        isCorrect = quiz.correctAnswer == "B",
                                        onClick = {
                                            if (!isQuizAnswerSubmitted) {
                                                selectedQuizOption = "B"
                                            }
                                        }
                                    )

                                    QuizOptionItem(
                                        label = "C",
                                        text = quiz.optionC,
                                        selected = selectedQuizOption == "C",
                                        submitted = isQuizAnswerSubmitted,
                                        isCorrect = quiz.correctAnswer == "C",
                                        onClick = {
                                            if (!isQuizAnswerSubmitted) {
                                                selectedQuizOption = "C"
                                            }
                                        }
                                    )

                                    QuizOptionItem(
                                        label = "D",
                                        text = quiz.optionD,
                                        selected = selectedQuizOption == "D",
                                        submitted = isQuizAnswerSubmitted,
                                        isCorrect = quiz.correctAnswer == "D",
                                        onClick = {
                                            if (!isQuizAnswerSubmitted) {
                                                selectedQuizOption = "D"
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Bottom Navigation Controls
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Prev Button
                                        Button(
                                            onClick = {
                                                if (currentQuizIndex > 0) {
                                                    currentQuizIndex--
                                                    selectedQuizOption = null
                                                    isQuizAnswerSubmitted = false
                                                }
                                            },
                                            enabled = currentQuizIndex > 0,
                                            colors = ButtonDefaults.buttonColors(containerColor = CardBackground, contentColor = PremiumGold)
                                        ) {
                                            Text("PREV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Submit / Explanation reveal
                                        if (selectedQuizOption != null && !isQuizAnswerSubmitted) {
                                            Button(
                                                onClick = { isQuizAnswerSubmitted = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = PremiumGold, contentColor = PureBlack)
                                            ) {
                                                Text("SUBMIT ANSWER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Next Button
                                        Button(
                                            onClick = {
                                                if (currentQuizIndex < quizzesList.size - 1) {
                                                    currentQuizIndex++
                                                    selectedQuizOption = null
                                                    isQuizAnswerSubmitted = false
                                                }
                                            },
                                            enabled = currentQuizIndex < quizzesList.size - 1,
                                            colors = ButtonDefaults.buttonColors(containerColor = CardBackground, contentColor = PremiumGold)
                                        ) {
                                            Text("NEXT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Explanation Box Reveal!
                                    AnimatedVisibility(
                                        visible = isQuizAnswerSubmitted,
                                        enter = slideInVertically() + fadeIn(),
                                        exit = slideOutVertically() + fadeOut()
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                val scoreFeedback = if (selectedQuizOption == quiz.correctAnswer) {
                                                    "🎉 Excellent! Correct Answer."
                                                } else {
                                                    "❌ Incorrect. Correct Option is: ${quiz.correctAnswer}"
                                                }
                                                Text(
                                                    text = scoreFeedback,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (selectedQuizOption == quiz.correctAnswer) PremiumGold else ErrorRed
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "Detailed Explanation:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MutedGold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = quiz.explanation,
                                                    fontSize = 12.sp,
                                                    color = SoftWhite,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOptionItem(
    label: String,
    text: String,
    selected: Boolean,
    submitted: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        submitted && isCorrect -> Color(0xFF2E3D2A) // correct green accent
        submitted && selected && !isCorrect -> Color(0xFF4A1E23) // wrong red accent
        selected -> Color(0xFF221E14) // golden selection
        else -> CardBackground
    }

    val borderColor = when {
        submitted && isCorrect -> Color(0xFF4CAF50)
        submitted && selected && !isCorrect -> Color(0xFFF44336)
        selected -> PremiumGold
        else -> GoldBorder
    }

    val textColor = if (submitted && isCorrect) Color(0xFF81C784) else SoftWhite

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(if (selected) PremiumGold else Color(0xFF242424), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) PureBlack else PremiumGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                color = textColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
