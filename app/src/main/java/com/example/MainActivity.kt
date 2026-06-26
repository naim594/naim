package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.StudyViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.VelvetDark
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private val TAG = "MainActivity"
    private var tts: TextToSpeech? = null
    private val viewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Voice Assistant Text To Speech system
        tts = TextToSpeech(this, this)

        // Reactively listen to ViewModel's speech commands
        lifecycleScope.launch {
            viewModel.speakTextTrigger.collect { answer ->
                try {
                    tts?.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "NAIM_VOICE_ASSIST_OUT")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to speak out loud", e)
                } finally {
                    viewModel.currentVoiceStatus.value = "Idle"
                }
            }
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                MainAppLayout(
                    navController = navController,
                    viewModel = viewModel,
                    onSpeechRequested = { rawText ->
                        // Clean up markdown or html tags from the speaking buffer
                        val cleanText = rawText.replace(Regex("<[^>]*>"), "")
                            .replace("*", "")
                            .replace("#", "")
                        viewModel.currentVoiceStatus.value = "Speaking"
                        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "MANUAL_SPEAK_OUT")
                    }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language US is not supported or missing data")
            }
        } else {
            Log.e(TAG, "TextToSpeech Initialization Failed!")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun MainAppLayout(
    navController: NavHostController,
    viewModel: StudyViewModel,
    onSpeechRequested: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Bottom bar displayed only on primary student workspace views
    val bottomTabs = listOf("dashboard", "chat", "notes", "tools")
    val showBottomBar = currentRoute in bottomTabs

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = VelvetDark,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") { popUpTo("dashboard") { saveState = true } } },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard", modifier = Modifier.size(20.dp)) },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PremiumGold,
                            selectedTextColor = PremiumGold,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == "chat",
                        onClick = { navController.navigate("chat") { popUpTo("dashboard") { saveState = true } } },
                        icon = { Icon(Icons.Default.Chat, contentDescription = "AI Chat", modifier = Modifier.size(20.dp)) },
                        label = { Text("AI Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PremiumGold,
                            selectedTextColor = PremiumGold,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == "notes",
                        onClick = { navController.navigate("notes") { popUpTo("dashboard") { saveState = true } } },
                        icon = { Icon(Icons.Default.Description, contentDescription = "Notebooks", modifier = Modifier.size(20.dp)) },
                        label = { Text("Notebooks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PremiumGold,
                            selectedTextColor = PremiumGold,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == "tools",
                        onClick = { navController.navigate("tools") { popUpTo("dashboard") { saveState = true } } },
                        icon = { Icon(Icons.Default.School, contentDescription = "Study Tools", modifier = Modifier.size(20.dp)) },
                        label = { Text("Study Labs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PremiumGold,
                            selectedTextColor = PremiumGold,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color(0x66FFFFFF),
                            unselectedTextColor = Color(0x66FFFFFF)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "onboarding",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onNavigateToDashboard = {
                        navController.navigate("dashboard") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { navController.navigate("chat") },
                    onNavigateToNotes = { navController.navigate("notes") },
                    onNavigateToTools = { navController.navigate("tools") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onViewNote = { noteId ->
                        // Automatically route to Notebook screen and view note!
                        navController.navigate("notes")
                    }
                )
            }

            composable("chat") {
                ChatScreen(
                    viewModel = viewModel,
                    onSpeechRequested = onSpeechRequested
                )
            }

            composable("notes") {
                NotesScreen(
                    viewModel = viewModel
                )
            }

            composable("tools") {
                ToolsScreen(
                    viewModel = viewModel
                )
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
