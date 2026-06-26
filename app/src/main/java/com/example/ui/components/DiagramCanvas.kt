package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun DiagramCanvas(
    diagramType: String,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    // Infinite transition for fluid particle and helix animations
    val infiniteTransition = rememberInfiniteTransition(label = "diagram_motion")
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color(0xFF0D0D0D), RoundedCornerShape(16.dp))
            .border(1.dp, GoldBorder, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Draw the main customized canvas
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val centerX = width / 2
            val centerY = height / 2

            when (diagramType.lowercase()) {
                "circuit" -> {
                    // --- PHYSICS: Electrical Circuit Schema ---
                    // Draw outer circuit wire pathway
                    val wirePath = Path().apply {
                        moveTo(centerX - 120f, centerY - 80f)
                        lineTo(centerX + 120f, centerY - 80f)
                        lineTo(centerX + 120f, centerY + 80f)
                        lineTo(centerX - 120f, centerY + 80f)
                        close()
                    }
                    drawPath(wirePath, color = GoldBorder, style = Stroke(width = 3f))

                    // 1. Draw Battery (Parallel Plates)
                    drawRect(Color(0xFF0D0D0D), Offset(centerX - 15f, centerY - 95f), Size(30f, 30f))
                    drawLine(PremiumGold, Offset(centerX - 10f, centerY - 95f), Offset(centerX - 10f, centerY - 65f), strokeWidth = 5f)
                    drawLine(PremiumGold, Offset(centerX, centerY - 90f), Offset(centerX, centerY - 70f), strokeWidth = 2f)
                    drawLine(PremiumGold, Offset(centerX + 10f, centerY - 95f), Offset(centerX + 10f, centerY - 65f), strokeWidth = 5f)

                    // 2. Draw Resistor (Zig-zag)
                    drawRect(Color(0xFF0D0D0D), Offset(centerX + 105f, centerY - 30f), Size(30f, 60f))
                    val resistorPath = Path().apply {
                        moveTo(centerX + 120f, centerY - 30f)
                        lineTo(centerX + 110f, centerY - 20f)
                        lineTo(centerX + 130f, centerY - 10f)
                        lineTo(centerX + 110f, centerY)
                        lineTo(centerX + 130f, centerY + 10f)
                        lineTo(centerX + 110f, centerY + 20f)
                        lineTo(centerX + 120f, centerY + 30f)
                    }
                    drawPath(resistorPath, color = PremiumGold, style = Stroke(width = 4f))

                    // 3. Draw Ammeter circle
                    drawCircle(Color(0xFF0D0D0D), radius = 18f, center = Offset(centerX - 120f, centerY))
                    drawCircle(PremiumGold, radius = 18f, center = Offset(centerX - 120f, centerY), style = Stroke(width = 3f))

                    // 4. Current Flowing Particles (glowing gold dots)
                    val particlePos = (animationOffset / (2 * PI.toFloat())) * 800f
                    val pathLength = 800f // Approx wire perimeter 240 * 2 + 160 * 2
                    val dotOffset = getCircuitCoord(particlePos, centerX, centerY)
                    drawCircle(Color.White, radius = 5f, center = dotOffset)
                    drawCircle(PremiumGold, radius = 8f, center = dotOffset, style = Stroke(width = 2f))
                }

                "dna" -> {
                    // --- BIOLOGY: DNA Double Helix ---
                    val rangeX = width - 80f
                    val startX = 40f
                    val amplitude = 50f
                    val frequency = 0.02f

                    for (x in startX.toInt().. (width - 40f).toInt() step 12) {
                        val currX = x.toFloat()
                        // Calculate standard animated double sine wave values
                        val y1 = centerY + amplitude * sin(frequency * currX + animationOffset)
                        val y2 = centerY - amplitude * sin(frequency * currX + animationOffset)

                        // Base pairing bars (vertical bars connecting chains)
                        if (x % 36 == 0) {
                            drawLine(
                                brush = Brush.verticalGradient(listOf(DeepGold, LightGold)),
                                start = Offset(currX, y1),
                                end = Offset(currX, y2),
                                strokeWidth = 3f
                            )
                            // Draw base connection junctions
                            drawCircle(PremiumGold, radius = 4f, center = Offset(currX, (y1 + y2) / 2))
                        }

                        // Spine particles (Helices)
                        drawCircle(PremiumGold, radius = 5f, center = Offset(currX, y1))
                        drawCircle(MutedGold, radius = 5f, center = Offset(currX, y2))
                    }
                }

                "cell" -> {
                    // --- BIOLOGY: Cell Organelles ---
                    // Draw outer Cell Membrane
                    drawCircle(
                        brush = Brush.radialGradient(listOf(Color(0x11E5C158), Color(0xFF0D0D0D))),
                        radius = 110f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = GoldBorder,
                        radius = 110f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 4f)
                    )

                    // Draw Nucleus (Center)
                    drawCircle(
                        color = PremiumGold,
                        radius = 35f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 2f)
                    )
                    drawCircle(
                        color = DeepGold,
                        radius = 15f,
                        center = Offset(centerX, centerY)
                    )

                    // Mitochondria shapes (ellipses)
                    drawCircle(MutedGold, radius = 10f, center = Offset(centerX - 60f, centerY - 45f))
                    drawCircle(MutedGold, radius = 12f, center = Offset(centerX + 55f, centerY + 35f))
                    
                    // Cytoplasm dots
                    for (i in 0..12) {
                        val dotX = centerX + (40..80).random() * if (i % 2 == 0) 1 else -1
                        val dotY = centerY + (40..80).random() * if (i % 3 == 0) 1 else -1
                        drawCircle(LightGold, radius = 2f, center = Offset(dotX.toFloat(), dotY.toFloat()))
                    }
                }

                "earth_layers" -> {
                    // --- GEOGRAPHY: Inner Earth Layers ---
                    val radiusBase = 120f
                    // Crust / Atmosphere ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700), // Core
                                Color(0xFFB8860B), // Mantle
                                Color(0xFF8B6508), // Outer Mantle
                                Color(0xFF1C1C1C)  // Crust
                            ),
                            center = Offset(centerX, centerY),
                            radius = radiusBase
                        )
                    )

                    // Inner cores outlines
                    drawCircle(Color.Black, radius = radiusBase, center = Offset(centerX, centerY), style = Stroke(width = 3f))
                    drawCircle(PremiumGold, radius = radiusBase * 0.7f, center = Offset(centerX, centerY), style = Stroke(width = 1.5f))
                    drawCircle(LightGold, radius = radiusBase * 0.35f, center = Offset(centerX, centerY), style = Stroke(width = 2f))
                }
            }
        }

        // Overlay Interactive Part Labels!
        Box(modifier = Modifier.fillMaxSize()) {
            if (labels.size >= 4) {
                // Label 1: Top Left
                DiagramLabelItem(
                    text = "① ${labels[0]}",
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                )

                // Label 2: Top Right
                DiagramLabelItem(
                    text = "② ${labels[1]}",
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                )

                // Label 3: Bottom Left
                DiagramLabelItem(
                    text = "③ ${labels[2]}",
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                )

                // Label 4: Bottom Right
                DiagramLabelItem(
                    text = "④ ${labels[3]}",
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun DiagramLabelItem(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xE6141414), RoundedCornerShape(8.dp))
            .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = PremiumGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// Coordinates translation logic for circuit particles motion loop
private fun getCircuitCoord(pos: Float, centerX: Float, centerY: Float): Offset {
    val xStart = centerX - 120f
    val xEnd = centerX + 120f
    val yStart = centerY - 80f
    val yEnd = centerY + 80f

    val w = 240f
    val h = 160f

    return when {
        pos < w -> Offset(xStart + pos, yStart) // Top wire
        pos < (w + h) -> Offset(xEnd, yStart + (pos - w)) // Right wire
        pos < (2 * w + h) -> Offset(xEnd - (pos - (w + h)), yEnd) // Bottom wire
        else -> Offset(xStart, yEnd - (pos - (2 * w + h))) // Left wire
    }
}
