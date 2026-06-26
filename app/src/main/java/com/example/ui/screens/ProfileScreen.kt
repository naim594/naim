package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
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
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: StudyViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.profile.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var selectedClassInput by remember { mutableStateOf("") }
    var favoriteSubjectsInput by remember { mutableStateOf("") }
    var studyGoalMinutesInput by remember { mutableStateOf(60f) }

    // Synchronize UI from loaded database profile State
    LaunchedEffect(profileState) {
        profileState?.let {
            nameInput = it.name
            selectedClassInput = it.studentClass
            favoriteSubjectsInput = it.favoriteSubjects
            studyGoalMinutesInput = it.studyGoalMinutes.toFloat()
        }
    }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBlack),
                title = {
                    Text(
                        text = "ACADEMIC IDENTITY",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PremiumGold)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Portrait Crest
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFF221E14), RoundedCornerShape(45.dp))
                        .border(1.5.dp, PremiumGold, RoundedCornerShape(45.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎓", fontSize = 42.sp)
                }
            }

            Text(
                text = "PERSONAL INFORMATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGold,
                letterSpacing = 1.sp
            )

            // Name edit field
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Student Name", color = MutedGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumGold,
                    unfocusedBorderColor = GoldBorder,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = "CLASS & PREPARATION TRACK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGold,
                letterSpacing = 1.sp
            )

            // Class track selector rows
            val classTracks = listOf("Class 10", "Class 11", "Class 12", "NEET Prep", "JEE Prep")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                classTracks.forEach { track ->
                    val isSelected = selectedClassInput == track
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedClassInput = track }
                            .border(
                                1.dp,
                                if (isSelected) PremiumGold else GoldBorder,
                                RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF221E14) else CardBackground
                        )
                    ) {
                        Text(
                            text = track,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PremiumGold else SoftWhite,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            Text(
                text = "FAVORITE SUBJECT PREFERENCES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGold,
                letterSpacing = 1.sp
            )

            OutlinedTextField(
                value = favoriteSubjectsInput,
                onValueChange = { favoriteSubjectsInput = it },
                label = { Text("Subjects (comma separated)", color = MutedGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumGold,
                    unfocusedBorderColor = GoldBorder,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = "DAILY STUDY MINUTES TARGET",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGold,
                letterSpacing = 1.sp
            )

            // Goal slider
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Minutes Goal:", color = SoftWhite, fontSize = 13.sp)
                        Text("${studyGoalMinutesInput.toInt()} mins", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Slider(
                        value = studyGoalMinutesInput,
                        onValueChange = { studyGoalMinutesInput = it },
                        valueRange = 15f..240f,
                        steps = 14, // 15 mins step intervals
                        colors = SliderDefaults.colors(
                            thumbColor = PremiumGold,
                            activeTrackColor = PremiumGold,
                            inactiveTrackColor = Color(0xFF242013)
                        )
                    )
                }
            }

            // Save Actions Block
            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = nameInput,
                        studentClass = selectedClassInput,
                        favoriteSubjects = favoriteSubjectsInput,
                        goalMinutes = studyGoalMinutesInput.toInt()
                    )
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PremiumGold, contentColor = PureBlack),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = PureBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE PROFILE DETAILS", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
