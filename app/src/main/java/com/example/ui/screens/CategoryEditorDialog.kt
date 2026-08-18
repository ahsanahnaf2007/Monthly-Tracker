package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Category
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.MinimalOnPrimary
import com.example.ui.theme.MinimalOnSurfaceVariant
import com.example.ui.theme.MinimalPrimary
import com.example.ui.util.CategoryIcons

@Composable
fun CategoryEditorDialog(
    categoryToEdit: Category?,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, iconName: String, colorHex: Long) -> Unit
) {
    var name by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.name ?: "")
    }

    var selectedIconName by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.iconName ?: "restaurant")
    }

    var selectedColorHex by remember(categoryToEdit) {
        mutableLongStateOf(categoryToEdit?.colorHex ?: CategoryColors.first())
    }

    val selectedColor = Color(selectedColorHex)
    val selectedIcon = CategoryIcons.getIcon(selectedIconName)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("category_editor_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = MinimalDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MinimalDarkOutline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (categoryToEdit == null) "NEW CATEGORY" else "EDIT CATEGORY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MinimalPrimary
                        )
                        Text(
                            text = if (categoryToEdit == null) "Create Tag" else "Update Tag",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MinimalOnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Preview Badge
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(selectedColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .border(2.dp, selectedColor, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = selectedIcon,
                        contentDescription = null,
                        tint = selectedColor,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name", color = MinimalOnSurfaceVariant) },
                    placeholder = { Text("e.g. Subscriptions, Pet Care", color = MinimalOnSurfaceVariant.copy(alpha = 0.5f)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_name_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MinimalDarkSurface,
                        unfocusedContainerColor = MinimalDarkSurface,
                        focusedBorderColor = selectedColor,
                        unfocusedBorderColor = MinimalDarkOutline,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Color Palette Selection
                Text(
                    text = "SELECT COLOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MinimalPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryColors.take(6).forEach { colorHex ->
                        ColorCircleItem(
                            colorHex = colorHex,
                            isSelected = selectedColorHex == colorHex,
                            onClick = { selectedColorHex = colorHex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryColors.drop(6).take(6).forEach { colorHex ->
                        ColorCircleItem(
                            colorHex = colorHex,
                            isSelected = selectedColorHex == colorHex,
                            onClick = { selectedColorHex = colorHex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Icon Selection Grid
                Text(
                    text = "SELECT ICON",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MinimalPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF222026))
                        .border(1.dp, MinimalDarkOutline, RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CategoryIcons.availableIcons) { iconOption ->
                            val isSelected = selectedIconName == iconOption.key
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) selectedColor.copy(alpha = 0.25f)
                                        else MinimalDarkSurface
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) selectedColor else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedIconName = iconOption.key }
                                    .testTag("icon_option_${iconOption.key}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconOption.icon,
                                    contentDescription = iconOption.label,
                                    tint = if (isSelected) selectedColor else MinimalOnSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Save Button
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(
                                categoryToEdit?.id ?: 0L,
                                name.trim(),
                                selectedIconName,
                                selectedColorHex
                            )
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_category_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedColor,
                        contentColor = Color(0xFF21005D)
                    )
                ) {
                    Text(
                        text = if (categoryToEdit == null) "Create Category" else "Save Changes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorCircleItem(
    colorHex: Long,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = Color(colorHex)
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .testTag("color_item_$colorHex"),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF21005D),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
