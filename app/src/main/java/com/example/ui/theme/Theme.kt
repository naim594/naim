package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PremiumGold,
  secondary = DeepGold,
  tertiary = LightGold,
  background = ObsidianBlack,
  surface = VelvetDark,
  onPrimary = PureBlack,
  onSecondary = SoftWhite,
  onTertiary = PureBlack,
  onBackground = SoftWhite,
  onSurface = SoftWhite,
  surfaceVariant = CharcoalDark,
  onSurfaceVariant = SoftWhite,
  outline = GoldBorder
)

private val LightColorScheme = darkColorScheme( // Enforce dark theme even in "light" mode for luxurious consistency
  primary = PremiumGold,
  secondary = DeepGold,
  tertiary = LightGold,
  background = ObsidianBlack,
  surface = VelvetDark,
  onPrimary = PureBlack,
  onSecondary = SoftWhite,
  onTertiary = PureBlack,
  onBackground = SoftWhite,
  onSurface = SoftWhite,
  surfaceVariant = CharcoalDark,
  onSurfaceVariant = SoftWhite,
  outline = GoldBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Dark mode by default
  dynamicColor: Boolean = false, // Keep custom gold & black theme by default
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
