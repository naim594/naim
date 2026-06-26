package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudyViewModel
import com.example.ui.components.NotebookPaper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var activeTab by remember { mutableStateOf(0) } // 0 = Handwritten AI, 1 = Smart Note Gen
    var topicInput by remember { mutableStateOf("") }
    var noteTitleInput by remember { mutableStateOf("") }
    var paperStyleSelection by remember { mutableStateOf("ruled") }

    val notesList by viewModel.allNotes.collectAsState()
    val isNoteProcessing by viewModel.isNoteProcessing
    val noteStatusText by viewModel.noteStatusText

    var selectedNoteIdForView by remember { mutableStateOf<Long?>(null) }
    val selectedNote = notesList.find { it.id == selectedNoteIdForView }

    // Media & Doc Pickers
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    if (bytes != null) {
                        viewModel.processHandwrittenNote(
                            title = noteTitleInput.ifEmpty { "OCR Scan Note" },
                            imageBytes = bytes,
                            paperStyle = paperStyleSelection
                        )
                    } else {
                        snackbarHostState.showSnackbar("Failed to read image content.")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                }
            }
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    if (bytes != null) {
                        // Extracting PDF textual representations or using multimodal triggers
                        // For maximum stability we treat PDF upload similarly using Gemini parsing helper
                        viewModel.processHandwrittenNote(
                            title = noteTitleInput.ifEmpty { "PDF Study Guide" },
                            imageBytes = bytes,
                            paperStyle = paperStyleSelection
                        )
                    } else {
                        snackbarHostState.showSnackbar("Failed to read PDF content.")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error reading PDF: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBlack),
                title = {
                    Text(
                        text = "STUDY NOTEBOOK CENTER",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Tab Bar
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = CardBackground,
                    contentColor = PremiumGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                            color = PremiumGold
                        )
                    }
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Handwritten AI", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Smart Generator", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Processing Status Alert Banner
                if (noteStatusText.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(1.dp, GoldBorder, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF221F13))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isNoteProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PremiumGold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("💡", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = noteStatusText,
                                color = LightGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Tab Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (activeTab == 0) {
                        // --- HANDWRITTEN NOTES OCR SCANNER ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "UPLOAD ROUGH NOTES OR STUDY GUIDES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedGold,
                                letterSpacing = 1.sp
                            )

                            // Title Field
                            OutlinedTextField(
                                value = noteTitleInput,
                                onValueChange = { noteTitleInput = it },
                                label = { Text("Notebook Title (e.g. Physics Chapter 3)", color = MutedGray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PremiumGold,
                                    unfocusedBorderColor = GoldBorder,
                                    focusedTextColor = SoftWhite,
                                    unfocusedTextColor = SoftWhite
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Paper Style Selector (ruled, grid)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PaperStyleSelectorCard(
                                    title = "Ruled Notebook",
                                    icon = "📝",
                                    selected = paperStyleSelection == "ruled",
                                    modifier = Modifier.weight(1f),
                                    onClick = { paperStyleSelection = "ruled" }
                                )
                                PaperStyleSelectorCard(
                                    title = "Graph Grid",
                                    icon = "📊",
                                    selected = paperStyleSelection == "grid",
                                    modifier = Modifier.weight(1f),
                                    onClick = { paperStyleSelection = "grid" }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Clickable Upload Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardBackground)
                                    .border(BorderStroke(1.5.dp, GoldBorder), RoundedCornerShape(16.dp))
                                    .clickable {
                                        imagePickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📷", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("SCAN HANDWRITTEN IMAGE", color = PremiumGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Reads handwritten ink via Gemini vision OCR", color = MutedGray, fontSize = 10.sp)
                                }
                            }

                            // PDF Upload Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardBackground)
                                    .border(BorderStroke(1.5.dp, GoldBorder), RoundedCornerShape(16.dp))
                                    .clickable {
                                        pdfPickerLauncher.launch("application/pdf")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("📄", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("UPLOAD STUDY PDF", color = PremiumGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Extracts complete notes for print formatting", color = MutedGray, fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    } else {
                        // --- SMART TOPIC NOTES GENERATOR ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "ENTER TOPIC FOR PROFESSIONAL STUDY COMPILATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedGold,
                                letterSpacing = 1.sp
                            )

                            OutlinedTextField(
                                value = topicInput,
                                onValueChange = { topicInput = it },
                                placeholder = { Text("e.g. Einstein's Photoelectric Effect, Covalent bonds...", color = MutedGray) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PremiumGold,
                                    unfocusedBorderColor = GoldBorder,
                                    focusedTextColor = SoftWhite,
                                    unfocusedTextColor = SoftWhite
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    if (topicInput.trim().isNotEmpty()) {
                                        viewModel.generateSmartNotesFromTopic(topicInput)
                                        topicInput = ""
                                    }
                                },
                                enabled = topicInput.trim().isNotEmpty() && !isNoteProcessing,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumGold, contentColor = PureBlack),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("COMPILE SMART NOTEBOOK", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            // Quick suggested templates helper
                            Column {
                                Text("RECOMMENDED STUDY TOPICS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGold)
                                Spacer(modifier = Modifier.height(8.dp))
                                val suggestions = listOf(
                                    "Thermodynamics Laws & Equations",
                                    "Mitosis Cell Division phases",
                                    "Periodic Table bonding properties"
                                )
                                suggestions.forEach { suggestion ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { topicInput = suggestion },
                                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                                        border = BorderStroke(1.dp, GoldBorder)
                                    ) {
                                        Text(
                                            text = "✨ $suggestion",
                                            fontSize = 12.sp,
                                            color = SoftWhite,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notebook paper drawer slide-over full-screen layout!
            if (selectedNote != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBlack)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Notebook toolbar header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedNoteIdForView = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PremiumGold)
                            }
                            Text(
                                text = "DIGITAL STUDY LAPTOP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PremiumGold,
                                letterSpacing = 1.sp
                            )
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val file = viewModel.exportNoteAsPdf(selectedNote)
                                        if (file != null) {
                                            snackbarHostState.showSnackbar("Note printed as high-resolution PDF inside Documents folder! 📄")
                                        } else {
                                            snackbarHostState.showSnackbar("Print error.")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Print, contentDescription = "Print PDF", tint = PremiumGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Render Notebook Paper structure
                        NotebookPaper(
                            title = selectedNote.title,
                            content = selectedNote.originalText,
                            summary = selectedNote.summary,
                            formulas = selectedNote.formulas,
                            mindMap = selectedNote.mindMapPoints,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Trigger viewing a note if user select it
    LaunchedEffect(viewModel.allNotes.collectAsState().value) {
        // Quick hook up for auto open note when processing finishes successfully
        val list = viewModel.allNotes.value
        if (list.isNotEmpty() && !isNoteProcessing && selectedNoteIdForView == null && noteStatusText.contains("Success")) {
            selectedNoteIdForView = list.first().id
            viewModel.noteStatusText.value = "" // clear banner
        }
    }
}

@Composable
fun PaperStyleSelectorCard(
    title: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                1.dp,
                if (selected) PremiumGold else GoldBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF221E14) else CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) PremiumGold else SoftWhite
            )
        }
    }
}
