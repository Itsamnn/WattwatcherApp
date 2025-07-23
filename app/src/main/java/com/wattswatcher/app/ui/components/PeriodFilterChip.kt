package com.wattswatcher.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterChip(
    period: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    FilterChip(
        onClick = onSelect,
        label = {
            Text(
                text = period.capitalize(),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = isSelected
    )
}