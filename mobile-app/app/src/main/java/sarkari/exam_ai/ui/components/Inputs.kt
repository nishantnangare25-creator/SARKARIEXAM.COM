package sarkari.exam_ai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sarkari.exam_ai.ui.theme.BorderColor
import sarkari.exam_ai.ui.theme.PrimaryBlue
import sarkari.exam_ai.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SarkariTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium) },
        placeholder = placeholder?.let { { Text(it, fontSize = 14.sp) } },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        isError = isError,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = BorderColor,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = TextPrimary.copy(alpha = 0.6f),
            cursorColor = PrimaryBlue
        ),
        singleLine = true
    )
}
