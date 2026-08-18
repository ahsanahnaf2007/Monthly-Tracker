package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Expense
import com.example.ui.components.CategoryDistributionCard
import com.example.ui.components.EmptyState
import com.example.ui.components.ExpenseCard
import com.example.ui.theme.MinimalDarkBackground
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkOutlineVariant
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOffline
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalOnline
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons
import com.example.ui.util.CurrencyUtils
import com.example.ui.util.DateTimeUtils
import com.example.ui.viewmodel.MonthlyAnalytics
import com.example.ui.viewmodel.PaymentFilter
import java.util.Calendar

@Composable
fun AnalyticsScreen(
    analytics: MonthlyAnalytics,
    categories: List<Category>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    onExportCsv: (year: Int, month: Int) -> Unit,
    onExportJson: (year: Int, month: Int) -> Unit,
    onExportAllCsv: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }

    // Filter states for analytics transactions
    var selectedPaymentFilter by remember { mutableStateOf(PaymentFilter.ALL) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, analytics.selectedYear)
        set(Calendar.MONTH, analytics.selectedMonth)
    }
    val currentMonthFormatted = DateTimeUtils.formatMonthYear(cal.timeInMillis)

    val isCurrentMonth = remember(analytics.selectedYear, analytics.selectedMonth) {
        val now = Calendar.getInstance()
        now.get(Calendar.YEAR) == analytics.selectedYear && now.get(Calendar.MONTH) == analytics.selectedMonth
    }

    // Filter monthly expenses
    val filteredMonthlyExpenses = remember(analytics.monthlyExpenses, selectedPaymentFilter, selectedCategoryId) {
        analytics.monthlyExpenses.filter { exp ->
            val matchesPayment = when (selectedPaymentFilter) {
                PaymentFilter.ALL -> true
                PaymentFilter.ONLINE -> exp.isOnline
                PaymentFilter.OFFLINE -> !exp.isOnline
            }
            val matchesCat = selectedCategoryId == null || exp.categoryId == selectedCategoryId
            matchesPayment && matchesCat
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("analytics_scroll_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header & Month Navigator
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ANALYTICS & HISTORY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 1.2.sp
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MinimalOnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Monthly Report",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        // Export Button
                        IconButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = MinimalPrimary,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .testTag("export_data_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Export Report",
                                tint = MinimalOnPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Month Selector Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = MinimalDarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onPreviousMonth,
                                modifier = Modifier.testTag("prev_month_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Month",
                                    tint = Color.White
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { onResetToCurrentMonth() }
                            ) {
                                Text(
                                    text = currentMonthFormatted,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (!isCurrentMonth) {
                                    Text(
                                        text = "Tap to jump to current month",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MinimalPrimary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onNextMonth,
                                modifier = Modifier.testTag("next_month_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Month",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Key Monthly Overview Hero Card (Lavender #D0BCFF theme)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, shape = RoundedCornerShape(28.dp), ambientColor = MinimalPrimary.copy(alpha = 0.3f), spotColor = MinimalPrimary.copy(alpha = 0.4f)),
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
                        Text(
                            text = "TOTAL EXPENDITURE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnPrimary.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = CurrencyUtils.format(analytics.totalMonthSpend),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MinimalOnPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            color = MinimalOnPrimary.copy(alpha = 0.12f),
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Breakdown (Daily Average, Highest Day, Txn Count)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricHighlightSub(
                                title = "DAILY AVG",
                                value = CurrencyUtils.format(analytics.dailyAverageSpend),
                                modifier = Modifier.weight(1f)
                            )

                            MetricHighlightSub(
                                title = "HIGHEST DAY",
                                value = CurrencyUtils.format(analytics.highestDaySpend),
                                modifier = Modifier.weight(1f)
                            )

                            MetricHighlightSub(
                                title = "TOTAL TXNS",
                                value = "${analytics.transactionCount}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Offline CSV Backup Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MinimalDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MinimalPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FileDownload,
                                        contentDescription = null,
                                        tint = MinimalPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Offline CSV Backups",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Export your financial records",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinimalOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onExportCsv(analytics.selectedYear, analytics.selectedMonth) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("export_month_csv_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MinimalPrimary,
                                    contentColor = MinimalOnPrimary
                                )
                            ) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Month CSV", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onExportAllCsv,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("export_all_csv_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                            ) {
                                Icon(Icons.Default.CloudDownload, null, tint = MinimalPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Full Backup", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Category Spend Distribution
            if (analytics.categoryBreakdown.isNotEmpty()) {
                item {
                    CategoryDistributionCard(
                        categorySpends = analytics.categoryBreakdown,
                        title = "CATEGORY BREAKDOWN ($currentMonthFormatted)"
                    )
                }
            }

            // Filter Chips for Monthly History
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "MONTHLY TRANSACTIONS (${filteredMonthlyExpenses.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MinimalPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedPaymentFilter == PaymentFilter.ALL && selectedCategoryId == null,
                                onClick = {
                                    selectedPaymentFilter = PaymentFilter.ALL
                                    selectedCategoryId = null
                                },
                                label = { Text("All") },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MinimalDarkSurface,
                                    labelColor = MinimalOnSurfaceVariant,
                                    selectedContainerColor = MinimalPrimary,
                                    selectedLabelColor = MinimalOnPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedPaymentFilter == PaymentFilter.ALL && selectedCategoryId == null,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = MinimalPrimary
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = selectedPaymentFilter == PaymentFilter.ONLINE,
                                onClick = {
                                    selectedPaymentFilter = if (selectedPaymentFilter == PaymentFilter.ONLINE) PaymentFilter.ALL else PaymentFilter.ONLINE
                                },
                                label = { Text("Online") },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MinimalDarkSurface,
                                    labelColor = MinimalOnSurfaceVariant,
                                    selectedContainerColor = MinimalOnline,
                                    selectedLabelColor = MinimalOnPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedPaymentFilter == PaymentFilter.ONLINE,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = MinimalOnline
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = selectedPaymentFilter == PaymentFilter.OFFLINE,
                                onClick = {
                                    selectedPaymentFilter = if (selectedPaymentFilter == PaymentFilter.OFFLINE) PaymentFilter.ALL else PaymentFilter.OFFLINE
                                },
                                label = { Text("Offline") },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MinimalDarkSurface,
                                    labelColor = MinimalOnSurfaceVariant,
                                    selectedContainerColor = MinimalOffline,
                                    selectedLabelColor = Color(0xFF492532)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedPaymentFilter == PaymentFilter.OFFLINE,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = MinimalOffline
                                )
                            )
                        }

                        items(categories) { cat ->
                            val isSelected = selectedCategoryId == cat.id
                            val catColor = Color(cat.colorHex)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryId = if (isSelected) null else cat.id },
                                label = { Text(cat.name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = CategoryIcons.getIcon(cat.iconName),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isSelected) Color(0xFF21005D) else catColor
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MinimalDarkSurface,
                                    labelColor = MinimalOnSurfaceVariant,
                                    selectedContainerColor = catColor,
                                    selectedLabelColor = Color(0xFF21005D)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = catColor
                                )
                            )
                        }
                    }
                }
            }

            // Monthly History List
            if (filteredMonthlyExpenses.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Expenses in $currentMonthFormatted",
                        description = "There are no transactions recorded for this selected month or filter.",
                        icon = Icons.Default.CalendarMonth
                    )
                }
            } else {
                items(filteredMonthlyExpenses, key = { it.id }) { exp ->
                    ExpenseCard(
                        expense = exp,
                        onClick = { onExpenseClick(exp) }
                    )
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = MinimalDarkSurface,
            title = { Text("Export $currentMonthFormatted Report", color = Color.White) },
            text = {
                Text(
                    "Export all ${analytics.transactionCount} transactions for $currentMonthFormatted directly to your device or share via email/cloud storage.",
                    color = MinimalOnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExportDialog = false
                        onExportCsv(analytics.selectedYear, analytics.selectedMonth)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPrimary, contentColor = MinimalOnPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Export CSV", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showExportDialog = false
                        onExportJson(analytics.selectedYear, analytics.selectedMonth)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                ) {
                    Text("Export JSON", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun MetricHighlightSub(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            fontWeight = FontWeight.Bold,
            color = MinimalOnPrimary.copy(alpha = 0.7f),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MinimalOnPrimary,
            maxLines = 1
        )
    }
}
