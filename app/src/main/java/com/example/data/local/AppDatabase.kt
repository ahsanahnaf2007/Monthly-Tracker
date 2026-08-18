package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Category
import com.example.data.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Expense::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val defaultCategories = listOf(
            Category(name = "Food & Dining", iconName = "restaurant", colorHex = 0xFFF97316L, isDefault = true, orderIndex = 0),
            Category(name = "Shopping", iconName = "shopping_bag", colorHex = 0xFF8B5CF6L, isDefault = true, orderIndex = 1),
            Category(name = "Groceries", iconName = "local_grocery_store", colorHex = 0xFF10B981L, isDefault = true, orderIndex = 2),
            Category(name = "Transport", iconName = "directions_car", colorHex = 0xFF3B82F6L, isDefault = true, orderIndex = 3),
            Category(name = "Bills & Utilities", iconName = "receipt_long", colorHex = 0xFFEF4444L, isDefault = true, orderIndex = 4),
            Category(name = "Entertainment", iconName = "movie", colorHex = 0xFF06B6D4L, isDefault = true, orderIndex = 5),
            Category(name = "Health & Meds", iconName = "local_hospital", colorHex = 0xFFEC4899L, isDefault = true, orderIndex = 6),
            Category(name = "Coffee & Snacks", iconName = "local_cafe", colorHex = 0xFFF59E0BL, isDefault = true, orderIndex = 7),
            Category(name = "General", iconName = "more_horiz", colorHex = 0xFF64748BL, isDefault = true, orderIndex = 8)
        )

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.categoryDao()?.insertAllCategories(defaultCategories)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
