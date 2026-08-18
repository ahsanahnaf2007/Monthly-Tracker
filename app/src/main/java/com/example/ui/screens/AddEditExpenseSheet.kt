package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Expense
import com.example.ui.components.FastKeypad
import com.example.ui.components.PaymentModeToggle
import com.example.ui.theme.MinimalDarkBackground
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkOutlineVariant
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons
import com.example.ui.util.CurrencyUtils
import com.example.ui.util.DateTimeUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseSheet(
    expenseToEdit: Expense?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (id: Long, amount: Double, category: Category, isOnline: Boolean, timestamp: Long, note: String) -> Unit,
    onDelete: ((Expense) -> Unit)? = null,
    onAddNewCategory: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    // Form state
    var amountString by remember(expenseToEdit) {
        mutableStateOf(
            if (expenseToEdit != null) {
                if (expenseToEdit.amount % 1.0 == 0.0) expenseToEdit.amount.toLong().toString()
                else expenseToEdit.amount.toString()
            } else ""
        )
    }

    var isOnline by remember(expenseToEdit) {
        mutableStateOf(expenseToEdit?.isOnline ?: true)
    }

    var selectedCategory by remember(expenseToEdit, categories) {
        mutableStateOf(
            if (expenseToEdit != null) {
                categories.find { it.id == expenseToEdit.categoryId } ?: categories.firstOrNull()
            } else {
                categories.firstOrNull()
            }
        )
    }

    var timestamp by remember(expenseToEdit) {
        mutableLongStateOf(expenseToEdit?.timestamp ?: System.currentTimeMillis())
    }

    var note by remember(expenseToEdit) {
        mutableStateOf(expenseToEdit?.note ?: "")
    }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val quickNoteSuggestions = listOf("Lunch", "Coffee", "Groceries", "Dinner", "Uber", "Amazon", "Fuel", "Snacks")

    // Date & Time Picker Helpers
    fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = timestamp
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                timestamp = newCal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = timestamp
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                timestamp = newCal.timeInMillis
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MinimalDarkSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(MinimalDarkOutline, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (expenseToEdit == null) "FAST LOG" else "EDIT ENTRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MinimalPrimary
                    )
                    Text(
                        text = if (expenseToEdit == null) "Log Expense" else "Edit Expense",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (expenseToEdit != null && onDelete != null) {
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("delete_expense_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Expense",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MinimalOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Display Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF222026),
                border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AMOUNT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MinimalPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (amountString.isEmpty()) "৳0" else "৳$amountString",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (amountString.isEmpty()) MinimalOnSurfaceVariant.copy(alpha = 0.4f) else Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("expense_amount_display")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Mode Toggle: Online vs Offline
            PaymentModeToggle(
                isOnline = isOnline,
                onModeChange = { isOnline = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection Header + Instant "+ New Category"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MinimalPrimary
                )

                TextButton(
                    onClick = onAddNewCategory,
                    modifier = Modifier.testTag("add_new_category_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MinimalPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Category",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MinimalPrimary
                    )
                }
            }

            // Category Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory?.id == category.id
                    val catColor = Color(category.colorHex)
                    val icon = CategoryIcons.getIcon(category.iconName)

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedCategory = category }
                            .testTag("category_chip_${category.id}"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) catColor.copy(alpha = 0.25f) else MinimalDarkBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) catColor else MinimalDarkOutline
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(catColor.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = catColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MinimalOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date & Time Picker Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date Button
                Surface(
                    onClick = { showDatePicker() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("date_picker_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = MinimalDarkBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = MinimalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = MinimalOnSurfaceVariant
                            )
                            Text(
                                text = DateTimeUtils.formatDate(timestamp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Time Button
                Surface(
                    onClick = { showTimePicker() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("time_picker_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = MinimalDarkBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = MinimalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MinimalOnSurfaceVariant
                            )
                            Text(
                                text = DateTimeUtils.formatTime(timestamp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Note Input Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Description (Optional)", color = MinimalOnSurfaceVariant) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_note_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MinimalDarkBackground,
                    unfocusedContainerColor = MinimalDarkBackground,
                    focusedBorderColor = MinimalPrimary,
                    unfocusedBorderColor = MinimalDarkOutline,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Quick Note Suggestions
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickNoteSuggestions) { suggestion ->
                    AssistChip(
                        onClick = { note = suggestion },
                        label = { Text(suggestion, style = MaterialTheme.typography.labelSmall, color = MinimalOnSurfaceVariant) },
                        shape = RoundedCornerShape(10.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MinimalDarkBackground
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = MinimalDarkOutline
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numeric Keypad
            FastKeypad(
                amountString = amountString,
                onAmountChange = { amountString = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Save / Log Button (Clean Minimalism Lavender CTA)
            val amountValue = amountString.toDoubleOrNull() ?: 0.0
            val isSaveEnabled = amountValue > 0 && selectedCategory != null

            Button(
                onClick = {
                    val cat = selectedCategory ?: return@Button
                    onSave(
                        expenseToEdit?.id ?: 0L,
                        amountValue,
                        cat,
                        isOnline,
                        timestamp,
                        note
                    )
                },
                enabled = isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_expense_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MinimalPrimary,
                    contentColor = MinimalOnPrimary,
                    disabledContainerColor = MinimalDarkOutline,
                    disabledContentColor = MinimalOnSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (expenseToEdit == null) "Log Expense (${CurrencyUtils.format(amountValue)})" else "Update Expense",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Confirmation dialog for deletion
    if (showDeleteConfirmDialog && expenseToEdit != null && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = MinimalDarkSurface,
            title = { Text("Delete Expense?", color = Color.White) },
            text = { Text("Are you sure you want to delete this expense of ${CurrencyUtils.format(expenseToEdit.amount)}?", color = MinimalOnSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete(expenseToEdit)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
