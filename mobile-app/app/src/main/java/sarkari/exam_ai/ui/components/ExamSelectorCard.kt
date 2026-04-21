package sarkari.exam_ai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sarkari.exam_ai.ui.theme.*

// ─── Exam options exactly matching the website screenshot ─────────────────────
data class ExamOption(val name: String, val icon: ImageVector, val emoji: String)

val EXAM_OPTIONS = listOf(
    ExamOption("UPSC Civil Services", Icons.Default.AccountBalance,  "🏛"),
    ExamOption("MPSC",                Icons.Default.LocalLibrary,    "📚"),
    ExamOption("SSC CGL/CHSL",        Icons.Default.WorkspacePremium,"✏️"),
    ExamOption("Banking (IBPS/SBI)",  Icons.Default.AccountBalance,  "🏦"),
    ExamOption("Railway (RRB)",       Icons.Default.Train,           "🚂"),
    ExamOption("NDA",                 Icons.Default.MilitaryTech,    "🎖"),
    ExamOption("State PSC",           Icons.Default.Book,            "📖"),
    ExamOption("UPSC CAPF",           Icons.Default.Security,        "🛡"),
    ExamOption("IAS/IPS",             Icons.Default.Stars,           "⭐"),
)

/**
 * Reusable exam selector that mirrors the website's "Select Target Exam" dropdown.
 * Shows icons + names in a clean card-style dropdown.
 */
@Composable
fun ExamSelectorCard(
    label: String = "Target Exam",
    selectedExam: String,
    onExamSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow"
    )

    Column(modifier = modifier) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Selector button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .border(
                    width = 1.5.dp,
                    color = if (expanded) PrimaryBlue else BorderColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedExam.isNotBlank()) {
                    val option = EXAM_OPTIONS.find { it.name == selectedExam }
                    Text(option?.emoji ?: "🎯", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = selectedExam,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = "Select Target Exam",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                }
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (expanded) PrimaryBlue else TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotation)
                )
            }
        }

        // Dropdown list — exactly like website
        AnimatedVisibility(visible = expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                color = Color.White,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    // "Select Target Exam" header row (blue, like website)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryBlue)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Select Target Exam",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Exam options
                    EXAM_OPTIONS.forEach { option ->
                        val isSelected = option.name == selectedExam
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onExamSelected(option.name)
                                    expanded = false
                                }
                                .background(if (isSelected) PrimaryBlue.copy(alpha = 0.06f) else Color.White)
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(option.emoji, fontSize = 18.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryBlue else TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                        if (option != EXAM_OPTIONS.last()) {
                            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
