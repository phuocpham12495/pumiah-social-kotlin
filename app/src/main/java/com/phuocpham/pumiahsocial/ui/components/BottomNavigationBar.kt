package com.phuocpham.pumiahsocial.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.phuocpham.pumiahsocial.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemSelected: (BottomNavItem) -> Unit,
    notificationBadgeCount: Int = 0,
    messageBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        BottomNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            val badgeCount = when (item) {
                BottomNavItem.NOTIFICATIONS -> notificationBadgeCount
                BottomNavItem.MESSAGES -> messageBadgeCount
                else -> 0
            }

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge { Text("$badgeCount") }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}
