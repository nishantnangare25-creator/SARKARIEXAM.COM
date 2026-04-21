package sarkari.exam_ai.ui.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import sarkari.exam_ai.R
import sarkari.exam_ai.ui.theme.PrimaryBlue

// All supported languages — matches the website screenshot exactly
private data class LangOption(val label: String, val code: String, val shortCode: String)

private val languages = listOf(
    LangOption("English",    "en", "EN"),
    LangOption("हिन्दी",     "hi", "HI"),
    LangOption("मराठी",      "mr", "MR"),
    LangOption("தமிழ்",      "ta", "TA"),
    LangOption("తెలుగు",     "te", "TE"),
    LangOption("ಕನ್ನಡ",      "kn", "KN"),
    LangOption("മലയാളം",    "ml", "ML"),
    LangOption("বাংলা",      "bn", "BN"),
    LangOption("ગુજરાતી",    "gu", "GU"),
    LangOption("ਪੰਜਾਬੀ",     "pa", "PA"),
    LangOption("اردو",       "ur", "UR"),
    LangOption("ଓଡ଼ିଆ",      "or", "OR"),
    LangOption("অসমীয়া",    "as", "AS")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SarkariTopAppBar(
    titleText: String = "SarkariExamAI",
    onMenuClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var showLangMenu by remember { mutableStateOf(false) }

    // Track current locale for the button label
    var currentLang by remember {
        val tag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val match = languages.find { tag.startsWith(it.code) }
        mutableStateOf(match ?: languages[0])
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    fontSize = 18.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF111827)
                )
            }
        },
        actions = {
            // ── Globe + current language code button ───────────────────────
            Box {
                TextButton(
                    onClick = { showLangMenu = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🌐 ${currentLang.shortCode}",
                        color = Color(0xFF111827),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                // ── Dropdown — mirrors website language picker ─────────────
                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false },
                    offset = DpOffset(x = (-60).dp, y = 0.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .widthIn(min = 180.dp)
                ) {
                    languages.forEach { lang ->
                        val isSelected = lang.code == currentLang.code
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = lang.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PrimaryBlue else Color(0xFF111827),
                                    fontSize = 15.sp
                                )
                            },
                            onClick = {
                                currentLang = lang
                                showLangMenu = false
                                // Instant locale switch — no restart needed
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(lang.code)
                                )
                            },
                            modifier = Modifier.background(
                                if (isSelected) PrimaryBlue.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                        )
                    }
                }
            }

            // ── Profile / Login icon ───────────────────────────────────────
            IconButton(onClick = onLoginClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Login/Profile",
                    tint = PrimaryBlue
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White.copy(alpha = 0.95f),
            scrolledContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
