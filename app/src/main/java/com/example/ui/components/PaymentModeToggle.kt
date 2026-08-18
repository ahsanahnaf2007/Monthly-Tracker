package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOffline
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalOnTertiary
import com.example.ui.theme.MinimalOnline

@Composable
fun PaymentModeToggle(
    isOnline: Boolean,
    onModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("payment_mode_toggle"),
        shape = RoundedCornerShape(16.dp),
        color = MinimalDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Online Option
            val onlineBgColor by animateColorAsState(
                targetValue = if (isOnline) MinimalOnline else Color.Transparent,
                label = "onlineBg"
            )
            val onlineContentColor by animateColorAsState(
                targetValue = if (isOnline) MinimalOnPrimary else MinimalOnSurfaceVariant,
                label = "onlineText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(onlineBgColor)
                    .clickable { onModeChange(true) }
                    .testTag("payment_mode_online"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Online Payment",
                        tint = onlineContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isOnline) FontWeight.Bold else FontWeight.Medium,
                        color = onlineContentColor
                    )
                }
            }

            // Offline Option
            val offlineBgColor by animateColorAsState(
                targetValue = if (!isOnline) MinimalOffline else Color.Transparent,
                label = "offlineBg"
            )
            val offlineContentColor by animateColorAsState(
                targetValue = if (!isOnline) MinimalOnTertiary else MinimalOnSurfaceVariant,
                label = "offlineText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(offlineBgColor)
                    .clickable { onModeChange(false) }
                    .testTag("payment_mode_offline"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Offline Payment",
                        tint = offlineContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Offline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (!isOnline) FontWeight.Bold else FontWeight.Medium,
                        color = offlineContentColor
                    )
                }
            }
        }
    }
}
