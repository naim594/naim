package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for the golden crest
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crest_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Luxury Glowing Gold Crest
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(listOf(Color(0x33E5C158), Color.Transparent)),
                    RoundedCornerShape(65.dp)
                )
                .clip(RoundedCornerShape(65.dp))
                .border(2.dp, PremiumGold, RoundedCornerShape(65.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon_1782410064100),
                contentDescription = "NAIM AI Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Premium Typography App Branding
        Text(
            text = "NAIM AI",
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.verticalGradient(listOf(LightGold, DeepGold))
            ),
            textAlign = TextAlign.Center,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "THE LUXURY SUITE FOR ACADEMIC EXCELLENCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedGold,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Glassmorphism Features Highlights Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OnboardingFeatureRow("✨", "Cognitive AI Chat", "Explains complex STEM concepts step-by-step in English, Hindi, and Assamese.")
                Spacer(modifier = Modifier.height(14.dp))
                OnboardingFeatureRow("📸", "Handwritten Notes AI", "Multimodal scanner reads rough handwritten pages & converts them to premium notebooks.")
                Spacer(modifier = Modifier.height(14.dp))
                OnboardingFeatureRow("📐", "Interactive Diagrammer", "Live animated schemas for Physics, Chemistry, Biology & Geography.")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Deluxe Golden Navigation Button
        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, LightGold, RoundedCornerShape(28.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = PremiumGold,
                contentColor = PureBlack
            )
        ) {
            Text(
                text = "ENTER WORKSPACE",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Designed for high-performing students",
            fontSize = 11.sp,
            color = MutedGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnboardingFeatureRow(emoji: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
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
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SoftWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MutedGray,
                lineHeight = 16.sp
            )
        }
    }
}
