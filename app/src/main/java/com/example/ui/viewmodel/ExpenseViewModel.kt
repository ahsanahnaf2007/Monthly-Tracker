package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Category
import com.example.data.model.Expense
import com.example.data.repository.ExpenseRepository
import com.example.ui.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class CategorySpend(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val totalAmount: Double,
    val count: Int,
    val percentage: Float
)

data class DashboardSummary(
    val totalMonthSpend: Double = 0.0,
    val todaySpend: Double = 0.0,
    val totalCount: Int = 0,
    val onlineSpend: Double = 0.0,
    val offlineSpend: Double = 0.0,
    val onlineCount: Int = 0,
    val offlineCount: Int = 0,
    val onlinePercentage: Float = 0f,
    val offlinePercentage: Float = 0f,
    val topCategories: List<CategorySpend> = emptyList()
)

data class MonthlyAnalytics(
    val selectedYear: Int,
    val selectedMonth: Int, // 0-indexed Calendar.MONTH
    val totalMonthSpend: Double = 0.0,
    val dailyAverageSpend: Double = 0.0,
    val highestDaySpend: Double = 0.0,
    val transactionCount: Int = 0,
    val onlineSpend: Double = 0.0,
    val offlineSpend: Double = 0.0,
    val onlinePercentage: Float = 0f,
    val offlinePercentage: Float = 0f,
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val monthlyExpenses: List<Expense> = emptyList()
)

enum class PaymentFilter {
    ALL, ONLINE, OFFLINE
}

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = ExpenseRepository(database.expenseDao(), database.categoryDao())
        viewModelScope.launch {
            repository.ensureDefaultCategories()
        }
    }

    val allExpenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _paymentFilter = MutableStateFlow(PaymentFilter.ALL)
    val paymentFilter = _paymentFilter.asStateFlow()

    private val _selectedCategoryIdFilter = MutableStateFlow<Long?>(null)
    val selectedCategoryIdFilter = _selectedCategoryIdFilter.asStateFlow()

    // Analytics Month Selection
    private val currentCal = Calendar.getInstance()
    private val _analyticsYear = MutableStateFlow(currentCal.get(Calendar.YEAR))
    val analyticsYear = _analyticsYear.asStateFlow()

    private val _analyticsMonth = MutableStateFlow(currentCal.get(Calendar.MONTH))
    val analyticsMonth = _analyticsMonth.asStateFlow()

    // Active Edit State
    private val _editingExpense = MutableStateFlow<Expense?>(null)
    val editingExpense = _editingExpense.asStateFlow()

    // Active Category Edit State
    private val _editingCategory = MutableStateFlow<Category?>(null)
    val editingCategory = _editingCategory.asStateFlow()

    // Dashboard Summary Calculations
    val dashboardSummary: StateFlow<DashboardSummary> = allExpenses.combine(allCategories) { expenses, _ ->
        calculateDashboardSummary(expenses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // Filtered Recent Expenses for Dashboard
    val filteredExpenses: StateFlow<List<Expense>> = combine(
        allExpenses,
        _searchQuery,
        _paymentFilter,
        _selectedCategoryIdFilter
    ) { expenses, query, filter, catId ->
        expenses.filter { expense ->
            val matchesQuery = query.isBlank() ||
                    expense.note.contains(query, ignoreCase = true) ||
                    expense.categoryName.contains(query, ignoreCase = true) ||
                    expense.amount.toString().contains(query)

            val matchesPayment = when (filter) {
                PaymentFilter.ALL -> true
                PaymentFilter.ONLINE -> expense.isOnline
                PaymentFilter.OFFLINE -> !expense.isOnline
            }

            val matchesCategory = catId == null || expense.categoryId == catId

            matchesQuery && matchesPayment && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Analytics Calculation
    val monthlyAnalytics: StateFlow<MonthlyAnalytics> = combine(
        allExpenses,
        _analyticsYear,
        _analyticsMonth
    ) { expenses, year, month ->
        calculateMonthlyAnalytics(expenses, year, month)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthlyAnalytics(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH))
    )

    private fun calculateDashboardSummary(expenses: List<Expense>): DashboardSummary {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH)

        val monthStart = DateTimeUtils.getStartOfMonth(currentYear, currentMonth)
        val monthEnd = DateTimeUtils.getEndOfMonth(currentYear, currentMonth)
        val todayStart = DateTimeUtils.getStartOfDay()
        val todayEnd = DateTimeUtils.getEndOfDay()

        val monthExpenses = expenses.filter { it.timestamp in monthStart..monthEnd }
        val todayExpenses = expenses.filter { it.timestamp in todayStart..todayEnd }

        val totalMonthSpend = monthExpenses.sumOf { it.amount }
        val todaySpend = todayExpenses.sumOf { it.amount }
        val totalCount = monthExpenses.size

        val onlineExpenses = monthExpenses.filter { it.isOnline }
        val offlineExpenses = monthExpenses.filter { !it.isOnline }

        val onlineSpend = onlineExpenses.sumOf { it.amount }
        val offlineSpend = offlineExpenses.sumOf { it.amount }

        val onlinePercentage = if (totalMonthSpend > 0) (onlineSpend / totalMonthSpend).toFloat() else 0f
        val offlinePercentage = if (totalMonthSpend > 0) (offlineSpend / totalMonthSpend).toFloat() else 0f

        // Category breakdown
        val categoryMap = monthExpenses.groupBy { it.categoryId }
        val categorySpends = categoryMap.map { (_, list) ->
            val first = list.first()
            val amount = list.sumOf { it.amount }
            val percentage = if (totalMonthSpend > 0) (amount / totalMonthSpend).toFloat() else 0f
            CategorySpend(
                categoryId = first.categoryId,
                categoryName = first.categoryName,
                categoryIcon = first.categoryIcon,
                categoryColor = first.categoryColor,
                totalAmount = amount,
                count = list.size,
                percentage = percentage
            )
        }.sortedByDescending { it.totalAmount }

        return DashboardSummary(
            totalMonthSpend = totalMonthSpend,
            todaySpend = todaySpend,
            totalCount = totalCount,
            onlineSpend = onlineSpend,
            offlineSpend = offlineSpend,
            onlineCount = onlineExpenses.size,
            offlineCount = offlineExpenses.size,
            onlinePercentage = onlinePercentage,
            offlinePercentage = offlinePercentage,
            topCategories = categorySpends
        )
    }

    private fun calculateMonthlyAnalytics(expenses: List<Expense>, year: Int, month: Int): MonthlyAnalytics {
        val monthStart = DateTimeUtils.getStartOfMonth(year, month)
        val monthEnd = DateTimeUtils.getEndOfMonth(year, month)

        val monthExpenses = expenses.filter { it.timestamp in monthStart..monthEnd }
        val totalSpend = monthExpenses.sumOf { it.amount }

        val onlineExpenses = monthExpenses.filter { it.isOnline }
        val offlineExpenses = monthExpenses.filter { !it.isOnline }

        val onlineSpend = onlineExpenses.sumOf { it.amount }
        val offlineSpend = offlineExpenses.sumOf { it.amount }

        val onlinePercentage = if (totalSpend > 0) (onlineSpend / totalSpend).toFloat() else 0f
        val offlinePercentage = if (totalSpend > 0) (offlineSpend / totalSpend).toFloat() else 0f

        // Daily calculations
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dailyAverage = if (daysInMonth > 0) totalSpend / daysInMonth else 0.0

        val dailyGroups = monthExpenses.groupBy {
            val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            c.get(Calendar.DAY_OF_MONTH)
        }
        val highestDay = dailyGroups.values.maxOfOrNull { list -> list.sumOf { it.amount } } ?: 0.0

        val categoryMap = monthExpenses.groupBy { it.categoryId }
        val categorySpends = categoryMap.map { (_, list) ->
            val first = list.first()
            val amount = list.sumOf { it.amount }
            val percentage = if (totalSpend > 0) (amount / totalSpend).toFloat() else 0f
            CategorySpend(
                categoryId = first.categoryId,
                categoryName = first.categoryName,
                categoryIcon = first.categoryIcon,
                categoryColor = first.categoryColor,
                totalAmount = amount,
                count = list.size,
                percentage = percentage
            )
        }.sortedByDescending { it.totalAmount }

        return MonthlyAnalytics(
            selectedYear = year,
            selectedMonth = month,
            totalMonthSpend = totalSpend,
            dailyAverageSpend = dailyAverage,
            highestDaySpend = highestDay,
            transactionCount = monthExpenses.size,
            onlineSpend = onlineSpend,
            offlineSpend = offlineSpend,
            onlinePercentage = onlinePercentage,
            offlinePercentage = offlinePercentage,
            categoryBreakdown = categorySpends,
            monthlyExpenses = monthExpenses
        )
    }

    // Actions
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPaymentFilter(filter: PaymentFilter) {
        _paymentFilter.value = filter
    }

    fun setSelectedCategoryFilter(categoryId: Long?) {
        _selectedCategoryIdFilter.value = categoryId
    }

    fun previousMonth() {
        var year = _analyticsYear.value
        var month = _analyticsMonth.value - 1
        if (month < 0) {
            month = 11
            year -= 1
        }
        _analyticsYear.value = year
        _analyticsMonth.value = month
    }

    fun nextMonth() {
        var year = _analyticsYear.value
        var month = _analyticsMonth.value + 1
        if (month > 11) {
            month = 0
            year += 1
        }
        _analyticsYear.value = year
        _analyticsMonth.value = month
    }

    fun setMonthYear(year: Int, month: Int) {
        _analyticsYear.value = year
        _analyticsMonth.value = month
    }

    fun resetToCurrentMonth() {
        val now = Calendar.getInstance()
        _analyticsYear.value = now.get(Calendar.YEAR)
        _analyticsMonth.value = now.get(Calendar.MONTH)
    }

    fun setEditingExpense(expense: Expense?) {
        _editingExpense.value = expense
    }

    fun setEditingCategory(category: Category?) {
        _editingCategory.value = category
    }

    fun saveExpense(
        id: Long = 0,
        amount: Double,
        category: Category,
        isOnline: Boolean,
        timestamp: Long,
        note: String
    ) {
        viewModelScope.launch {
            val expense = Expense(
                id = id,
                amount = amount,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.iconName,
                categoryColor = category.colorHex,
                isOnline = isOnline,
                timestamp = timestamp,
                note = note.trim()
            )
            if (id == 0L) {
                repository.insertExpense(expense)
            } else {
                repository.updateExpense(expense)
            }
            _editingExpense.value = null
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            if (_editingExpense.value?.id == expense.id) {
                _editingExpense.value = null
            }
        }
    }

    fun saveCategory(
        id: Long = 0,
        name: String,
        iconName: String,
        colorHex: Long
    ) {
        viewModelScope.launch {
            val category = Category(
                id = id,
                name = name.trim(),
                iconName = iconName,
                colorHex = colorHex,
                isDefault = false
            )
            if (id == 0L) {
                repository.insertCategory(category)
            } else {
                repository.updateCategory(category)
            }
            _editingCategory.value = null
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            if (_editingCategory.value?.id == category.id) {
                _editingCategory.value = null
            }
        }
    }

    suspend fun getExpenseCountForCategory(categoryId: Long): Int {
        return repository.getExpenseCountForCategory(categoryId)
    }

    // Export Data Helpers
    fun exportAllExpensesCsv(context: Context) {
        val expenses = allExpenses.value
        val csvBuilder = StringBuilder()
        csvBuilder.append("ID,Date,Time,Category,Amount (৳),Payment Mode,Note\n")
        expenses.forEach { exp ->
            val date = DateTimeUtils.formatDate(exp.timestamp)
            val time = DateTimeUtils.formatTime(exp.timestamp)
            val mode = if (exp.isOnline) "Online" else "Offline"
            val sanitizedNote = exp.note.replace("\"", "\"\"")
            csvBuilder.append("${exp.id},\"$date\",\"$time\",\"${exp.categoryName}\",${exp.amount},\"$mode\",\"$sanitizedNote\"\n")
        }

        shareText(
            context = context,
            title = "All Expenses Backup (CSV)",
            content = csvBuilder.toString(),
            mimeType = "text/csv"
        )
    }

    fun exportMonthlyCsv(context: Context, year: Int, month: Int) {
        val monthStart = DateTimeUtils.getStartOfMonth(year, month)
        val monthEnd = DateTimeUtils.getEndOfMonth(year, month)
        val monthExpenses = allExpenses.value.filter { it.timestamp in monthStart..monthEnd }

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
        val monthName = DateTimeUtils.formatMonthYear(cal.timeInMillis)

        val csvBuilder = StringBuilder()
        csvBuilder.append("ID,Date,Time,Category,Amount (৳),Payment Mode,Note\n")
        monthExpenses.forEach { exp ->
            val date = DateTimeUtils.formatDate(exp.timestamp)
            val time = DateTimeUtils.formatTime(exp.timestamp)
            val mode = if (exp.isOnline) "Online" else "Offline"
            val sanitizedNote = exp.note.replace("\"", "\"\"")
            csvBuilder.append("${exp.id},\"$date\",\"$time\",\"${exp.categoryName}\",${exp.amount},\"$mode\",\"$sanitizedNote\"\n")
        }

        shareText(
            context = context,
            title = "Expense Report - $monthName (CSV)",
            content = csvBuilder.toString(),
            mimeType = "text/csv"
        )
    }

    fun exportMonthlyJson(context: Context, year: Int, month: Int) {
        val monthStart = DateTimeUtils.getStartOfMonth(year, month)
        val monthEnd = DateTimeUtils.getEndOfMonth(year, month)
        val monthExpenses = allExpenses.value.filter { it.timestamp in monthStart..monthEnd }

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
        val monthName = DateTimeUtils.formatMonthYear(cal.timeInMillis)

        val jsonArray = JSONArray()
        monthExpenses.forEach { exp ->
            val obj = JSONObject().apply {
                put("id", exp.id)
                put("date", DateTimeUtils.formatDate(exp.timestamp))
                put("time", DateTimeUtils.formatTime(exp.timestamp))
                put("timestamp", exp.timestamp)
                put("category", exp.categoryName)
                put("categoryId", exp.categoryId)
                put("amount", exp.amount)
                put("isOnline", exp.isOnline)
                put("paymentMode", if (exp.isOnline) "Online" else "Offline")
                put("note", exp.note)
            }
            jsonArray.put(obj)
        }

        val root = JSONObject().apply {
            put("period", monthName)
            put("exportedAt", System.currentTimeMillis())
            put("totalSpend", monthExpenses.sumOf { it.amount })
            put("transactionCount", monthExpenses.size)
            put("expenses", jsonArray)
        }

        shareText(
            context = context,
            title = "Expense Report - $monthName (JSON)",
            content = root.toString(2),
            mimeType = "application/json"
        )
    }

    private fun shareText(context: Context, title: String, content: String, mimeType: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
