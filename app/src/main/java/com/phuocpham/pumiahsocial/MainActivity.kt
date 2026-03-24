package com.phuocpham.pumiahsocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.phuocpham.pumiahsocial.data.model.AuthState
import com.phuocpham.pumiahsocial.navigation.BottomNavItem
import com.phuocpham.pumiahsocial.navigation.PumiahNavGraph
import com.phuocpham.pumiahsocial.navigation.Screen
import com.phuocpham.pumiahsocial.ui.auth.AuthViewModel
import com.phuocpham.pumiahsocial.ui.profile.ProfileCheckViewModel
import com.phuocpham.pumiahsocial.ui.components.BottomNavigationBar
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.notifications.NotificationsViewModel
import com.phuocpham.pumiahsocial.ui.settings.SettingsViewModel
import com.phuocpham.pumiahsocial.ui.theme.PumiahSocialTheme
import com.phuocpham.pumiahsocial.util.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by dataStore.data
                .map { it[SettingsViewModel.DARK_MODE_KEY] ?: false }
                .collectAsState(initial = false)

            val isOnline by networkMonitor.isOnline.collectAsState(initial = true)

            PumiahSocialTheme(darkTheme = isDarkMode) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(visible = !isOnline) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.error)
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không có kết nối mạng",
                                color = MaterialTheme.colorScheme.onError,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PumiahSocialMainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun PumiahSocialMainContent() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = BottomNavItem.entries.map { it.route }
    val showBottomNav = currentRoute in bottomNavRoutes

    when (authState) {
        is AuthState.Loading -> {
            LoadingIndicator()
        }
        is AuthState.Unauthenticated, is AuthState.Error -> {
            PumiahNavGraph(
                navController = navController,
                startDestination = Screen.Login.route
            )
        }
        is AuthState.Authenticated -> {
            val authenticated = authState as AuthState.Authenticated
            var profileLoaded by remember { mutableStateOf(false) }
            var hasProfile by remember { mutableStateOf(true) }
            val profileViewModel: ProfileCheckViewModel = hiltViewModel()
            val profileState by profileViewModel.hasProfile.collectAsState()

            LaunchedEffect(authenticated.userId) {
                profileViewModel.checkProfile(authenticated.userId)
            }

            LaunchedEffect(profileState) {
                if (profileState != null) {
                    hasProfile = profileState!!
                    profileLoaded = true
                }
            }

            if (!profileLoaded) {
                LoadingIndicator()
            } else {
                val startDest = if (hasProfile) Screen.Feed.route else Screen.CreateProfile.route

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showBottomNav) {
                        val notificationsViewModel: NotificationsViewModel = hiltViewModel()
                        val unreadCount by notificationsViewModel.unreadCount.collectAsState()

                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onItemSelected = { item ->
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Feed.route) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            notificationBadgeCount = unreadCount
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    PumiahNavGraph(
                        navController = navController,
                        startDestination = startDest
                    )
                }
            }
            } // end else (profileLoaded)
        }
    }
}
