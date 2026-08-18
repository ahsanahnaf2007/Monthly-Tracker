package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons
import com.example.ui.util.CurrencyUtils
import com.example.ui.viewmodel.CategorySpend

@Composable
fun CategoryDistributionCard(
    categorySpends: List<CategorySpend>,
    modifier: Modifier = Modifier,
    title: String = "CATEGORY BREAKDOWN"
) {
    if (categorySpends.isEmpty()) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_distribution_card"),
        shape = RoundedCornerShape(20.dp),
        color = MinimalDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp
                ),
                fontWeight = FontWeight.Bold,
                color = MinimalPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment Combined Distribution Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MinimalDarkOutline)
            ) {
                categorySpends.forEach { cat ->
                    if (cat.percentage > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(cat.percentage.coerceAtLeast(0.02f))
                                .background(Color(cat.categoryColor))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Top categories list breakdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categorySpends.take(5).forEach { cat ->
                    val color = Color(cat.categoryColor)
                    val icon = CategoryIcons.getIcon(cat.categoryIcon)
                    val percentText = (cat.percentage * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(color.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = cat.categoryName,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Category name & percentage
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = cat.categoryName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = CurrencyUtils.format(cat.totalAmount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Individual bar & percent
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val animProgress by animateFloatAsState(
                                    targetValue = cat.percentage.coerceIn(0f, 1f),
                                    label = "catProgress"
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MinimalDarkOutline)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(animProgress)
                                            .background(color)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "$percentText%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
