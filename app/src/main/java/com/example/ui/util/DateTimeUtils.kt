package com.example.ui.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val dayMonthFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val fullDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDayMonth(timestamp: Long): String {
        return dayMonthFormat.format(Date(timestamp))
    }

    fun formatMonthYear(timestamp: Long): String {
        return monthYearFormat.format(Date(timestamp))
    }

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }

    fun getDateHeader(timestamp: Long): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isSameYear = now.get(Calendar.YEAR) == target.get(Calendar.YEAR)
        val isToday = isSameYear && now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
        
        now.add(Calendar.DAY_OF_YEAR, -1)
        val isYesterday = isSameYear && now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> "Today"
            isYesterday -> "Yesterday"
            isSameYear -> SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(timestamp))
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun getStartOfMonth(year: Int, month: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfMonth(year: Int, month: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}

object CurrencyUtils {
    const val DEFAULT_CURRENCY_SYMBOL = "৳" // Bangladeshi Taka (৳)
    private val formatter = DecimalFormat("#,##0.00")
    private val compactFormatter = DecimalFormat("#,##0")

    fun format(amount: Double, symbol: String = DEFAULT_CURRENCY_SYMBOL): String {
        return "$symbol${formatter.format(amount)}"
    }

    fun formatCompact(amount: Double, symbol: String = DEFAULT_CURRENCY_SYMBOL): String {
        return if (amount % 1.0 == 0.0) {
            "$symbol${compactFormatter.format(amount)}"
        } else {
            "$symbol${formatter.format(amount)}"
        }
    }
}
