package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Expense
import com.example.ui.components.CategoryDistributionCard
import com.example.ui.components.EmptyState
import com.example.ui.components.ExpenseCard
import com.example.ui.components.MonthlySummaryCard
import com.example.ui.theme.MinimalDarkBackground
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkOutlineVariant
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalMuted
import com.example.ui.theme.MinimalOffline
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalOnline
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons
import com.example.ui.util.DateTimeUtils
import com.example.ui.viewmodel.DashboardSummary
import com.example.ui.viewmodel.PaymentFilter

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    expenses: List<Expense>,
    categories: List<Category>,
    searchQuery: String,
    paymentFilter: PaymentFilter,
    selectedCategoryIdFilter: Long?,
    onSearchQueryChange: (String) -> Unit,
    onPaymentFilterChange: (PaymentFilter) -> Unit,
    onCategoryFilterChange: (Long?) -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onFastLogExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Group expenses by date header (Today, Yesterday, etc.)
    val groupedExpenses = remember(expenses) {
        expenses.groupBy { DateTimeUtils.getDateHeader(it.timestamp) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_scroll_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Clean Minimalism Top Bar with App Logo
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MinimalPrimary, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_foreground),
                                contentDescription = "App Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "PERSONAL TRACKER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = 1.2.sp
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MinimalOnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Expenses",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { isSearchExpanded = !isSearchExpanded },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (isSearchExpanded || searchQuery.isNotBlank()) MinimalPrimary.copy(alpha = 0.2f) else MinimalDarkOutline,
                                    shape = CircleShape
                                )
                                .border(1.dp, MinimalMuted, CircleShape)
                                .testTag("toggle_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isSearchExpanded || searchQuery.isNotBlank()) MinimalPrimary else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Monthly Summary Card (Hero & Payment Distribution)
            item {
                MonthlySummaryCard(summary = summary)
            }

            // Category Spend Distribution Card
            if (summary.topCategories.isNotEmpty()) {
                item {
                    CategoryDistributionCard(categorySpends = summary.topCategories)
                }
            }

            // Search Bar
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(visible = isSearchExpanded || searchQuery.isNotBlank()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search transactions, notes, categories...", color = MinimalOnSurfaceVariant) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MinimalOnSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Search",
                                            tint = MinimalOnSurfaceVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .testTag("search_input_field"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MinimalDarkSurface,
                                unfocusedContainerColor = MinimalDarkSurface,
                                focusedBorderColor = MinimalPrimary,
                                unfocusedBorderColor = MinimalDarkOutline,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Filter Chips Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = paymentFilter == PaymentFilter.ALL && selectedCategoryIdFilter == null,
                                onClick = {
                                    onPaymentFilterChange(PaymentFilter.ALL)
                                    onCategoryFilterChange(null)
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
                                    selected = paymentFilter == PaymentFilter.ALL && selectedCategoryIdFilter == null,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = MinimalPrimary
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = paymentFilter == PaymentFilter.ONLINE,
                                onClick = {
                                    onPaymentFilterChange(
                                        if (paymentFilter == PaymentFilter.ONLINE) PaymentFilter.ALL else PaymentFilter.ONLINE
                                    )
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
                                    selected = paymentFilter == PaymentFilter.ONLINE,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = MinimalOnline
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = paymentFilter == PaymentFilter.OFFLINE,
                                onClick = {
                                    onPaymentFilterChange(
                                        if (paymentFilter == PaymentFilter.OFFLINE) PaymentFilter.ALL else PaymentFilter.OFFLINE
                                    )
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
                                    selected = paymentFilter == PaymentFilter.OFFLINE,
                                    borderColor = MinimalDarkOutline,
                                    selectedBorderColor = MinimalOffline
                                )
                            )
                        }

                        items(categories) { cat ->
                            val isSelected = selectedCategoryIdFilter == cat.id
                            val catColor = Color(cat.colorHex)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onCategoryFilterChange(if (isSelected) null else cat.id)
                                },
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

            // Recent Activity Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT ACTIVITY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MinimalPrimary
                    )

                    if (searchQuery.isNotBlank() || paymentFilter != PaymentFilter.ALL || selectedCategoryIdFilter != null) {
                        TextButton(onClick = {
                            onSearchQueryChange("")
                            onPaymentFilterChange(PaymentFilter.ALL)
                            onCategoryFilterChange(null)
                        }) {
                            Text("Reset", style = MaterialTheme.typography.labelSmall, color = MinimalPrimary)
                        }
                    }
                }
            }

            // Transactions Grouped by Date
            if (expenses.isEmpty()) {
                item {
                    EmptyState(
                        title = if (searchQuery.isNotBlank() || paymentFilter != PaymentFilter.ALL || selectedCategoryIdFilter != null)
                            "No Matching Expenses"
                        else
                            "No Expenses Logged Yet",
                        description = if (searchQuery.isNotBlank())
                            "Try searching with another keyword or resetting filters."
                        else
                            "Tap the '+' button below to quickly log your first transaction.",
                        icon = Icons.Default.ReceiptLong,
                        actionText = if (expenses.isEmpty() && searchQuery.isBlank() && paymentFilter == PaymentFilter.ALL) "Log First Expense" else null,
                        onActionClick = onFastLogExpense
                    )
                }
            } else {
                groupedExpenses.forEach { (dateHeader, dateExpenses) ->
                    item(key = "header_$dateHeader") {
                        Text(
                            text = dateHeader,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }

                    items(dateExpenses, key = { it.id }) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onClick = { onExpenseClick(expense) }
                        )
                    }
                }
            }
        }

        // Clean Minimalism Squircle Floating Action Button (rounded-2xl)
        FloatingActionButton(
            onClick = onFastLogExpense,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_fast_log_expense"),
            containerColor = MinimalPrimary,
            contentColor = MinimalOnPrimary,
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Fast Log Expense",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
