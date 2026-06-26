package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.ui.StudyViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: StudyViewModel,
    onSpeechRequested: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading
    val selectedSubject by viewModel.selectedSubject
    val selectedLanguage by viewModel.selectedLanguage
    val voiceStatus by viewModel.currentVoiceStatus

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // High-Tech Chat Control Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldBorder, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COGNITIVE ACADEMIC CHAT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Solve & Learn step-by-step",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite
                        )
                    }
                    IconButton(onClick = { viewModel.clearChatHistory() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = Color(0xFFCF6679))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selectors for Language & Subject
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Subject Selector
                    Box(modifier = Modifier.weight(1.2f)) {
                        var subjectExpanded by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { subjectExpanded = true }
                                .border(1.dp, GoldBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedSubject, fontSize = 12.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                            }
                        }
                        DropdownMenu(
                            expanded = subjectExpanded,
                            onDismissRequest = { subjectExpanded = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            val subjects = listOf("Physics", "Chemistry", "Mathematics", "Biology", "English")
                            subjects.forEach { sub ->
                                DropdownMenuItem(
                                    text = { Text(sub, color = SoftWhite, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.selectedSubject.value = sub
                                        subjectExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Language Selector
                    Box(modifier = Modifier.weight(1f)) {
                        var languageExpanded by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { languageExpanded = true }
                                .border(1.dp, GoldBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedLanguage, fontSize = 12.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                            }
                        }
                        DropdownMenu(
                            expanded = languageExpanded,
                            onDismissRequest = { languageExpanded = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            val languages = listOf("English", "Hindi", "Assamese")
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang, color = SoftWhite, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.selectedLanguage.value = lang
                                        languageExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active voice visualizer helper
        AnimatedVisibility(visible = voiceStatus != "Idle") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .border(1.dp, GoldBorder, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1710))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (voiceStatus == "Listening") "🎙️" else "🔊",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (voiceStatus == "Listening") "Naim Voice Assistant Listening..." else "Speaking Answer Aloud...",
                        color = PremiumGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Conversation history list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (chatHistory.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💡", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "I can solve your homework queries!",
                        color = SoftWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select a subject above, then enter any questions in Physics, Chemistry, Math, Biology, or English.",
                        color = MutedGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chatHistory) { msg ->
                        ChatMessageItem(
                            message = msg,
                            onSpeak = { onSpeechRequested(msg.messageText) }
                        )
                    }

                    if (isChatLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(CardBackground, RoundedCornerShape(12.dp))
                                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = PremiumGold,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("NAIM AI is reasoning...", color = MutedGold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Chat bottom input bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button (Voice input simulator)
                IconButton(
                    onClick = {
                        // Dictates a sample command based on selected subject for demo
                        val samples = mapOf(
                            "Physics" to "Explain Newton's second law with equations",
                            "Chemistry" to "Explain Covalent bonding vs Ionic bonding",
                            "Mathematics" to "Solve integral of x square dx step-by-step",
                            "Biology" to "Explain photosynthesis light reactions",
                            "English" to "What is the differences between active and passive voice?"
                        )
                        textInput = samples[selectedSubject] ?: "Explain step-by-step"
                        viewModel.executeVoiceCommand(textInput)
                    },
                    modifier = Modifier
                        .background(Color(0xFF221E14), RoundedCornerShape(24.dp))
                        .border(1.dp, GoldBorder, RoundedCornerShape(24.dp))
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = PremiumGold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text Input
                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask your academic question...", fontSize = 13.sp, color = MutedGray) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0D0D0D)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D0D0D),
                        unfocusedContainerColor = Color(0xFF0D0D0D),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (textInput.trim().isNotEmpty()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    enabled = textInput.trim().isNotEmpty(),
                    modifier = Modifier
                        .background(
                            if (textInput.trim().isNotEmpty()) PremiumGold else Color(0xFF242424),
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (textInput.trim().isNotEmpty()) PureBlack else MutedGray
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF221E14), RoundedCornerShape(14.dp))
                    .border(1.dp, PremiumGold, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚜️", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(0.85f, fill = false)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isUser) Color(0xFF1E1C15) else CardBackground,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 16.dp
                        )
                    )
                    .border(
                        1.dp,
                        if (isUser) PremiumGold else GoldBorder,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = message.messageText,
                        fontSize = 13.sp,
                        color = SoftWhite,
                        lineHeight = 18.sp
                    )

                    // Text-To-Speech Speaker option on AI Messages
                    if (!isUser) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = onSpeak,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = "Speak explanation",
                                    tint = PremiumGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
