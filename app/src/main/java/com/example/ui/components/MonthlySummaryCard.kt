package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CurrencyUtils
import com.example.ui.viewmodel.DashboardSummary

@Composable
fun MonthlySummaryCard(
    summary: DashboardSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Highlight Card (Clean Minimalism Lavender Theme)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, shape = RoundedCornerShape(28.dp), ambientColor = MinimalPrimary.copy(alpha = 0.3f), spotColor = MinimalPrimary.copy(alpha = 0.4f))
                .testTag("monthly_summary_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MinimalPrimary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "THIS MONTH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyUtils.format(summary.totalMonthSpend),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MinimalOnPrimary
                        )
                    }

                    // Activity Indicator Tag
                    Surface(
                        color = MinimalOnPrimary,
                        shape = CircleShape,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${summary.totalCount} txns",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(
                    color = MinimalOnPrimary.copy(alpha = 0.12f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Sub-metrics: Today & Transactions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column {
                        Text(
                            text = "TODAY",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnPrimary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyUtils.format(summary.todaySpend),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "TRANSACTIONS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnPrimary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${summary.totalCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnPrimary
                        )
                    }
                }
            }
        }

        // Payment Distribution Section (Grid Cards matching Clean Minimalism)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PAYMENT DISTRIBUTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp
                ),
                fontWeight = FontWeight.Bold,
                color = MinimalPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val onlinePercent = if (summary.totalMonthSpend > 0) (summary.onlinePercentage * 100).toInt() else 0
                val offlinePercent = if (summary.totalMonthSpend > 0) (summary.offlinePercentage * 100).toInt() else 0

                // Online Card
                DistributionSubCard(
                    title = "Online",
                    amount = summary.onlineSpend,
                    percent = onlinePercent,
                    progress = summary.onlinePercentage,
                    progressColor = MinimalPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Offline Card
                DistributionSubCard(
                    title = "Offline",
                    amount = summary.offlineSpend,
                    percent = offlinePercent,
                    progress = summary.offlinePercentage,
                    progressColor = MinimalPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DistributionSubCard(
    title: String,
    amount: Double,
    percent: Int,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MinimalDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MinimalOnSurfaceVariant
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyUtils.format(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            val animProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                label = "distProgress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MinimalDarkOutline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(if (animProgress <= 0f) 0.02f else animProgress)
                        .background(progressColor)
                )
            }
        }
    }
}
