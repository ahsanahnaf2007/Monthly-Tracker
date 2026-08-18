package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkOutlineVariant
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons
import com.example.ui.util.CurrencyUtils
import com.example.ui.util.DateTimeUtils

@Composable
fun ExpenseCard(
    expense: Expense,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(expense.categoryColor)
    val icon = CategoryIcons.getIcon(expense.categoryIcon)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("expense_item_${expense.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MinimalDarkSurface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge (Rounded 14dp container)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = categoryColor.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = expense.categoryName,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (expense.note.isNotBlank()) expense.note else expense.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(2.dp))

                val modeText = if (expense.isOnline) "Online" else "Offline"
                val timeText = DateTimeUtils.formatTime(expense.timestamp)
                val catText = if (expense.note.isNotBlank()) " • ${expense.categoryName}" else ""

                Text(
                    text = "$modeText • $timeText$catText",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MinimalOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Amount Display
            Text(
                text = "-${CurrencyUtils.format(expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
