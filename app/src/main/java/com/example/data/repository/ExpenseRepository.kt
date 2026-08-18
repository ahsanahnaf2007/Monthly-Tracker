package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CategoryDao
import com.example.data.local.ExpenseDao
import com.example.data.model.Category
import com.example.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    fun getExpensesBetween(startTime: Long, endTime: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesBetween(startTime, endTime)
    }

    suspend fun getExpenseById(id: Long): Expense? = withContext(Dispatchers.IO) {
        expenseDao.getExpenseById(id)
    }

    suspend fun insertExpense(expense: Expense): Long = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) = withContext(Dispatchers.IO) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: Long) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun insertCategory(category: Category): Long = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(category)
    }

    suspend fun getExpenseCountForCategory(categoryId: Long): Int = withContext(Dispatchers.IO) {
        expenseDao.getExpenseCountForCategory(categoryId)
    }

    suspend fun ensureDefaultCategories() = withContext(Dispatchers.IO) {
        val count = categoryDao.getCategoryCount()
        if (count == 0) {
            categoryDao.insertAllCategories(AppDatabase.defaultCategories)
        }
    }
}
