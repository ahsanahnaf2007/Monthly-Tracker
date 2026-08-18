package com.example.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddEditExpenseSheet
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CategoryEditorDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MinimalDarkBackground
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnPrimaryContainer
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimaryContainer
import com.example.ui.viewmodel.ExpenseViewModel

sealed class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Dashboard : AppDestination("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_dashboard")
    object Analytics : AppDestination("Analytics", Icons.Filled.Insights, Icons.Outlined.Insights, "nav_analytics")
    object Categories : AppDestination("Categories", Icons.Filled.Category, Icons.Outlined.Category, "nav_categories")
}

@Composable
fun MainApp(
    viewModel: ExpenseViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val destinations = listOf(
        AppDestination.Dashboard,
        AppDestination.Analytics,
        AppDestination.Categories
    )

    // ViewModel State Collection
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val dashboardSummary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val filteredExpenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
    val monthlyAnalytics by viewModel.monthlyAnalytics.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val paymentFilter by viewModel.paymentFilter.collectAsStateWithLifecycle()
    val selectedCategoryIdFilter by viewModel.selectedCategoryIdFilter.collectAsStateWithLifecycle()

    // Sheet / Dialog states
    var isAddExpenseSheetOpen by remember { mutableStateOf(false) }
    var isCategoryDialogOpen by remember { mutableStateOf(false) }
    val editingExpense by viewModel.editingExpense.collectAsStateWithLifecycle()
    val editingCategory by viewModel.editingCategory.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MinimalDarkBackground,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(
                        width = 1.dp,
                        color = MinimalDarkOutline
                    )
                    .testTag("bottom_navigation_bar"),
                containerColor = MinimalDarkSurface,
                tonalElevation = 0.dp
            ) {
                destinations.forEachIndexed { index, destination ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MinimalOnSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalOnPrimaryContainer,
                            unselectedIconColor = MinimalOnSurfaceVariant,
                            selectedTextColor = Color.White,
                            unselectedTextColor = MinimalOnSurfaceVariant,
                            indicatorColor = MinimalPrimaryContainer
                        ),
                        modifier = Modifier.testTag(destination.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> DashboardScreen(
                summary = dashboardSummary,
                expenses = filteredExpenses,
                categories = allCategories,
                searchQuery = searchQuery,
                paymentFilter = paymentFilter,
                selectedCategoryIdFilter = selectedCategoryIdFilter,
                onSearchQueryChange = viewModel::setSearchQuery,
                onPaymentFilterChange = viewModel::setPaymentFilter,
                onCategoryFilterChange = viewModel::setSelectedCategoryFilter,
                onExpenseClick = { expense ->
                    viewModel.setEditingExpense(expense)
                    isAddExpenseSheetOpen = true
                },
                onFastLogExpense = {
                    viewModel.setEditingExpense(null)
                    isAddExpenseSheetOpen = true
                },
                modifier = Modifier.padding(innerPadding)
            )
            1 -> AnalyticsScreen(
                analytics = monthlyAnalytics,
                categories = allCategories,
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                onResetToCurrentMonth = viewModel::resetToCurrentMonth,
                onExportCsv = { year, month -> viewModel.exportMonthlyCsv(context, year, month) },
                onExportJson = { year, month -> viewModel.exportMonthlyJson(context, year, month) },
                onExportAllCsv = { viewModel.exportAllExpensesCsv(context) },
                onExpenseClick = { expense ->
                    viewModel.setEditingExpense(expense)
                    isAddExpenseSheetOpen = true
                },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> CategoriesScreen(
                categories = allCategories,
                onAddCategory = {
                    viewModel.setEditingCategory(null)
                    isCategoryDialogOpen = true
                },
                onEditCategory = { category ->
                    viewModel.setEditingCategory(category)
                    isCategoryDialogOpen = true
                },
                onDeleteCategory = { category ->
                    viewModel.deleteCategory(category)
                },
                getExpenseCountForCategory = { catId ->
                    viewModel.getExpenseCountForCategory(catId)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    // Add / Edit Expense Sheet
    if (isAddExpenseSheetOpen) {
        AddEditExpenseSheet(
            expenseToEdit = editingExpense,
            categories = allCategories,
            onDismiss = {
                isAddExpenseSheetOpen = false
                viewModel.setEditingExpense(null)
            },
            onSave = { id, amount, category, isOnline, timestamp, note ->
                viewModel.saveExpense(id, amount, category, isOnline, timestamp, note)
                isAddExpenseSheetOpen = false
            },
            onDelete = { expense ->
                viewModel.deleteExpense(expense)
                isAddExpenseSheetOpen = false
            },
            onAddNewCategory = {
                viewModel.setEditingCategory(null)
                isCategoryDialogOpen = true
            }
        )
    }

    // Add / Edit Category Dialog
    if (isCategoryDialogOpen) {
        CategoryEditorDialog(
            categoryToEdit = editingCategory,
            onDismiss = {
                isCategoryDialogOpen = false
                viewModel.setEditingCategory(null)
            },
            onSave = { id, name, iconName, colorHex ->
                viewModel.saveCategory(id, name, iconName, colorHex)
                isCategoryDialogOpen = false
            }
        )
    }
}
