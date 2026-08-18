package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary

@Composable
fun FastKeypad(
    amountString: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPresets = listOf(5, 10, 20, 50, 100)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Quick Presets Row (+5, +10, +20, +50, +100)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickPresets.forEach { preset ->
                Surface(
                    onClick = {
                        val currentVal = amountString.toDoubleOrNull() ?: 0.0
                        val newVal = currentVal + preset
                        onAmountChange(
                            if (newVal % 1.0 == 0.0) newVal.toLong().toString()
                            else String.format("%.2f", newVal)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("preset_$preset"),
                    shape = RoundedCornerShape(10.dp),
                    color = MinimalDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+৳$preset",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = MinimalPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3x4 Numeric Keypad Grid
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0", "BACKSPACE")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (key) {
                                "BACKSPACE" -> {
                                    if (amountString.isNotEmpty()) {
                                        onAmountChange(amountString.dropLast(1))
                                    }
                                }
                                "." -> {
                                    if (!amountString.contains(".")) {
                                        onAmountChange(if (amountString.isEmpty()) "0." else "$amountString.")
                                    }
                                }
                                else -> {
                                    // Limit 2 decimal places
                                    if (amountString.contains(".")) {
                                        val parts = amountString.split(".")
                                        if (parts.size > 1 && parts[1].length >= 2) return@KeypadButton
                                    }
                                    // Prevent multiple leading zeroes
                                    if (amountString == "0") {
                                        onAmountChange(key)
                                    } else {
                                        onAmountChange(amountString + key)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("keypad_$key"),
        shape = RoundedCornerShape(14.dp),
        color = MinimalDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (key == "BACKSPACE") {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = MinimalOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = key,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
