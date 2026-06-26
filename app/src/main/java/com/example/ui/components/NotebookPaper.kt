package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepGold
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.PremiumGold

@Composable
fun NotebookPaper(
    title: String,
    content: String,
    summary: String = "",
    formulas: String = "",
    mindMap: String = "",
    modifier: Modifier = Modifier
) {
    // Ruled paper background draw effect
    val notebookPaperModifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .drawBehind {
            // Draw margin red line (left side standard notebook margin)
            drawLine(
                color = Color(0xFFFF9999), // Red margin line
                start = Offset(45.dp.toPx(), 0f),
                end = Offset(45.dp.toPx(), size.height),
                strokeWidth = 3f
            )

            // Draw horizontal ruled notebook blue-gray lines
            var currentY = 56.dp.toPx()
            while (currentY < size.height) {
                drawLine(
                    color = Color(0xFFE2EAF4), // Light bluish paper line
                    start = Offset(0f, currentY),
                    end = Offset(size.width, currentY),
                    strokeWidth = 2f
                )
                currentY += 28.dp.toPx() // Height of each ruled notebook row
            }
        }
        .background(Color(0xFFFEFDF7), RoundedCornerShape(12.dp)) // Cozy soft vintage notebook cream color
        .border(1.2.dp, PremiumGold, RoundedCornerShape(12.dp))
        .padding(start = 55.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)

    Column(modifier = modifier.then(notebookPaperModifier)) {
        // Lined Page Title Header
        Text(
            text = title,
            fontFamily = FontFamily.Cursive,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C3D5A), // Real dark blue notebook fountain pen ink
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HorizontalDivider(color = Color(0xFF1C3D5A), thickness = 1.5.dp, modifier = Modifier.padding(bottom = 20.dp))

        // Summary Highlight Box
        if (summary.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFBEA), RoundedCornerShape(8.dp)) // Warm highlighter yellow
                    .border(1.dp, Color(0xFFE5C158), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "💡 Study Summary",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9A7B1C)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        color = Color(0xFF2C3E50)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Formulas Section
        if (formulas.isNotEmpty()) {
            Text(
                text = "📝 Important Formulas",
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111)
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            val formulaList = formulas.split(",", "\n")
            for (formula in formulaList) {
                if (formula.trim().isNotEmpty()) {
                    Text(
                        text = "⚡  ${formula.trim()}",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A5276), // Blue ink highlight
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Mind Map Section
        if (mindMap.isNotEmpty()) {
            Text(
                text = "🗺️ Quick Mind Map Points",
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111)
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            val points = mindMap.split("-", "\n", "•")
            for (p in points) {
                if (p.trim().isNotEmpty()) {
                    Text(
                        text = "➔  ${p.trim()}",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Verbatim Original / Extracted Content
        Text(
            text = "📖 Comprehensive Study Notes",
            fontFamily = FontFamily.Cursive,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111)
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = content.replace(Regex("<[^>]*>"), ""), // strip any HTML tags
            fontFamily = FontFamily.Cursive,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = Color(0xFF111111), // dark notebook ink
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}
