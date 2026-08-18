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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.EmptyState
import com.example.ui.theme.MinimalDarkBackground
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkOutlineVariant
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons

@Composable
fun CategoriesScreen(
    categories: List<Category>,
    onAddCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    getExpenseCountForCategory: suspend (Long) -> Int,
    modifier: Modifier = Modifier
) {
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var deleteCategoryExpenseCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(categoryToDelete) {
        val cat = categoryToDelete
        if (cat != null) {
            deleteCategoryExpenseCount = getExpenseCountForCategory(cat.id)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalDarkBackground)
    ) {
        if (categories.isEmpty()) {
            EmptyState(
                title = "No Categories Found",
                description = "Add custom categories with personalized colors and icons to organize your expenses.",
                icon = Icons.Default.Category,
                actionText = "Create Category",
                onActionClick = onAddCategory
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("categories_list"),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 6.dp)) {
                        Text(
                            text = "CUSTOMIZE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MinimalOnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${categories.size} categories configured",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinimalOnSurfaceVariant
                        )
                    }
                }

                items(categories, key = { it.id }) { category ->
                    CategoryItemCard(
                        category = category,
                        onEdit = { onEditCategory(category) },
                        onDelete = { categoryToDelete = category }
                    )
                }
            }
        }

        // Clean Minimalism Floating Action Button
        FloatingActionButton(
            onClick = onAddCategory,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_category"),
            containerColor = MinimalPrimary,
            contentColor = MinimalOnPrimary,
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Category",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Delete Confirmation Dialog
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = MinimalDarkSurface,
            title = { Text("Delete \"${cat.name}\"?", color = Color.White) },
            text = {
                if (deleteCategoryExpenseCount > 0) {
                    Text(
                        "This category has $deleteCategoryExpenseCount recorded transactions. Deleting it will remove the category from your list.",
                        color = MinimalOnSurfaceVariant
                    )
                } else {
                    Text("Are you sure you want to delete this category?", color = MinimalOnSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun CategoryItemCard(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(category.colorHex)
    val icon = CategoryIcons.getIcon(category.iconName)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onEdit)
            .testTag("category_row_${category.id}"),
        colors = CardDefaults.cardColors(containerColor = MinimalDarkSurface.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    if (category.isDefault) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MinimalDarkOutline,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MinimalOnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MinimalOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
