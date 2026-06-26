package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyNote
import com.example.ui.StudyViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onViewNote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.profile.collectAsState()
    val notesList by viewModel.allNotes.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ObsidianBlack,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NAIM AI",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            style = androidx.compose.ui.text.TextStyle(
                                brush = Brush.horizontalGradient(listOf(BrightGold, LightGold, PremiumGold))
                            ),
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "ELITE ACADEMIC SUITE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumGold.copy(alpha = 0.7f),
                            letterSpacing = 2.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateToChat,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0x0DFFFFFF), RoundedCornerShape(22.dp))
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice Assistant", tint = PremiumGold, modifier = Modifier.size(20.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .border(2.dp, PremiumGold, RoundedCornerShape(22.dp))
                                .padding(2.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF111111))
                                .clickable { onNavigateToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = SoftWhite, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Sophisticated Dark Glass Welcome Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0x1AFFFFFF), Color(0x0DFFFFFF))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PremiumGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "POWERED BY GEMINI 1.5",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PremiumGold,
                                    letterSpacing = 1.5.sp
                                )
                            }
                            Text(
                                text = "Welcome, ${profileState?.name ?: "Naim"}. Ready to master Biology today?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Serif,
                                color = SoftWhite,
                                lineHeight = 26.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x66000000), RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0x33C5A059), RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToChat() }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ask me any academic question...",
                                    fontSize = 13.sp,
                                    color = Color(0x99FFFFFF),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                                Button(
                                    onClick = onNavigateToChat,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PremiumGold,
                                        contentColor = PureBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("CHAT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // High-Tech Goal Progress & Study Metrics Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular progress meter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            val target = profileState?.studyGoalMinutes ?: 60
                            val completed = profileState?.completedMinutes ?: 0
                            val progress = if (target > 0) completed.toFloat() / target else 0f

                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = PremiumGold,
                                strokeWidth = 8.dp,
                                trackColor = Color(0xFF242013)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$completed",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PremiumGold
                                )
                                Text(
                                    text = "min",
                                    fontSize = 10.sp,
                                    color = MutedGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TODAY'S ACADEMIC GOAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedGold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${profileState?.completedMinutes ?: 0}/${profileState?.studyGoalMinutes ?: 60} minutes logged",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftWhite
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            // Simulating study pomodoro click
                            Button(
                                onClick = {
                                    viewModel.addStudyTime(15)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("15 Minutes of deep study logged! Keep going! 🎯")
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF221E14),
                                    contentColor = PremiumGold
                                ),
                                border = BorderStroke(1.dp, GoldBorder),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp), tint = PremiumGold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("FOCUS 15 MIN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Core Modules 2-Column Grid
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CORE MODULES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedGray,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "View All",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumGold,
                            modifier = Modifier.clickable { onNavigateToNotes() }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Module 1: Handwritten AI
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp))
                                .clickable { onNavigateToNotes() },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Brush.linearGradient(listOf(PremiumGold, DeepGold)),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = PureBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Handwritten AI",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftWhite
                                    )
                                    Text(
                                        text = "OCR & Optimization",
                                        fontSize = 10.sp,
                                        color = MutedGray
                                    )
                                }
                            }
                        }

                        // Module 2: Diagram Gen
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp))
                                .clickable { onNavigateToTools() },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Brush.linearGradient(listOf(PremiumGold, DeepGold)),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AccountTree,
                                        contentDescription = null,
                                        tint = PureBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Diagram Gen",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftWhite
                                    )
                                    Text(
                                        text = "Physics & Biology",
                                        fontSize = 10.sp,
                                        color = MutedGray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Subject list
            item {
                Column {
                    Text(
                        text = "FAVORITE SUBJECTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedGold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val subjects = profileState?.favoriteSubjects?.split(",") ?: listOf("Physics", "Chemistry", "Mathematics")
                        for (sub in subjects) {
                            if (sub.trim().isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF141414), RoundedCornerShape(20.dp))
                                        .border(1.dp, GoldBorder, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(text = sub.trim(), color = PremiumGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Recent study notes list / uploads
            item {
                Text(
                    text = "RECENT STUDY NOTES & UPLOADS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedGold,
                    letterSpacing = 1.sp
                )
            }

            if (notesList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📜", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No study notes saved yet.",
                            color = SoftWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Scan handwritten pages or write smart topics to create premium notebooks.",
                            color = MutedGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToNotes,
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold, contentColor = PureBlack),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("CREATE FIRST NOTE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(notesList) { note ->
                    RecentNoteCard(
                        note = note,
                        onView = { onViewNote(note.id) },
                        onExportPdf = {
                            scope.launch {
                                val file = viewModel.exportNoteAsPdf(note)
                                if (file != null) {
                                    snackbarHostState.showSnackbar("Note exported as PDF successfully to Documents directory! 📄")
                                } else {
                                    snackbarHostState.showSnackbar("Failed to export PDF.")
                                }
                            }
                        },
                        onDelete = { viewModel.deleteNote(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardNavIcon(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, GoldBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF221E14), RoundedCornerShape(8.dp))
                    .border(1.dp, GoldBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SoftWhite)
            Text(text = subtitle, fontSize = 10.sp, color = MutedGray)
        }
    }
}

@Composable
fun RecentNoteCard(
    note: StudyNote,
    onView: () -> Unit,
    onExportPdf: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(note.dateCreated))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF221E14), RoundedCornerShape(8.dp))
                    .border(1.dp, GoldBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📔", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite
                )
                Text(
                    text = "$dateStr • ${note.paperStyle.uppercase()} Style",
                    fontSize = 11.sp,
                    color = MutedGray
                )
            }
            // Action Icon Buttons (View, PDF, Delete)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onView) {
                    Icon(Icons.Default.Book, contentDescription = "View inside notebook", tint = PremiumGold)
                }
                IconButton(onClick = onExportPdf) {
                    Icon(Icons.Default.Share, contentDescription = "Export PDF", tint = LightGold)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFCF6679))
                }
            }
        }
    }
}
