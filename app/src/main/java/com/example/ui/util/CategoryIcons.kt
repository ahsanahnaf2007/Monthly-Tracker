package com.example.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class IconOption(
    val key: String,
    val label: String,
    val icon: ImageVector
)

object CategoryIcons {
    val availableIcons: List<IconOption> = listOf(
        IconOption("restaurant", "Dining", Icons.Default.Restaurant),
        IconOption("shopping_bag", "Shopping", Icons.Default.ShoppingBag),
        IconOption("local_grocery_store", "Groceries", Icons.Default.LocalGroceryStore),
        IconOption("directions_car", "Transport", Icons.Default.DirectionsCar),
        IconOption("receipt_long", "Bills", Icons.Default.ReceiptLong),
        IconOption("local_cafe", "Cafe & Snacks", Icons.Default.LocalCafe),
        IconOption("movie", "Entertainment", Icons.Default.Movie),
        IconOption("local_hospital", "Health", Icons.Default.LocalHospital),
        IconOption("fitness_center", "Fitness", Icons.Default.FitnessCenter),
        IconOption("school", "Education", Icons.Default.School),
        IconOption("flight", "Travel", Icons.Default.Flight),
        IconOption("home", "Housing", Icons.Default.Home),
        IconOption("bolt", "Utilities", Icons.Default.Bolt),
        IconOption("local_mall", "Retail", Icons.Default.LocalMall),
        IconOption("sports_esports", "Gaming", Icons.Default.SportsEsports),
        IconOption("pets", "Pets", Icons.Default.Pets),
        IconOption("work", "Work", Icons.Default.Work),
        IconOption("wifi", "Internet", Icons.Default.Wifi),
        IconOption("celebration", "Gifts & Events", Icons.Default.Celebration),
        IconOption("build", "Maintenance", Icons.Default.Build),
        IconOption("account_balance", "Finance", Icons.Default.AccountBalance),
        IconOption("attach_money", "Cash", Icons.Default.AttachMoney),
        IconOption("payments", "Subscription", Icons.Default.Payments),
        IconOption("auto_awesome", "Special", Icons.Default.AutoAwesome),
        IconOption("more_horiz", "Other", Icons.Default.MoreHoriz)
    )

    fun getIcon(iconName: String): ImageVector {
        return availableIcons.find { it.key == iconName }?.icon ?: Icons.Default.MoreHoriz
    }
}
