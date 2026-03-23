package com.phuocpham.pumiahsocial.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.phuocpham.pumiahsocial.ui.auth.LoginScreen
import com.phuocpham.pumiahsocial.ui.auth.SignUpScreen
import com.phuocpham.pumiahsocial.ui.feed.CreatePostScreen
import com.phuocpham.pumiahsocial.ui.feed.FeedScreen
import com.phuocpham.pumiahsocial.ui.feed.PostDetailScreen
import com.phuocpham.pumiahsocial.ui.friends.FriendRequestsScreen
import com.phuocpham.pumiahsocial.ui.friends.FriendsListScreen
import com.phuocpham.pumiahsocial.ui.messaging.ChatScreen
import com.phuocpham.pumiahsocial.ui.messaging.ConversationsListScreen
import com.phuocpham.pumiahsocial.ui.messaging.NewConversationScreen
import com.phuocpham.pumiahsocial.ui.notifications.NotificationsScreen
import com.phuocpham.pumiahsocial.ui.profile.CreateProfileScreen
import com.phuocpham.pumiahsocial.ui.profile.EditProfileScreen
import com.phuocpham.pumiahsocial.ui.profile.ProfileScreen
import com.phuocpham.pumiahsocial.ui.search.SearchScreen
import com.phuocpham.pumiahsocial.ui.settings.SettingsScreen

@Composable
fun PumiahNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.CreateProfile.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        // Feed
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateToCreatePost = { navController.navigate(Screen.CreatePost.route) },
                onNavigateToPostDetail = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
            )
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            PostDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        // Profile
        composable(Screen.MyProfile.route) {
            ProfileScreen(
                userId = null,
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToFriends = { navController.navigate(Screen.FriendsList.route) },
                onNavigateToUserProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateBack = null
            )
        }
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            ProfileScreen(
                userId = userId,
                onNavigateToEditProfile = null,
                onNavigateToFriends = { navController.navigate(Screen.FriendsList.route) },
                onNavigateToUserProfile = { uid ->
                    navController.navigate(Screen.UserProfile.createRoute(uid))
                },
                onNavigateToSettings = null,
                onNavigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CreateProfile.route) {
            CreateProfileScreen(
                onProfileCreated = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.CreateProfile.route) { inclusive = true }
                    }
                }
            )
        }

        // Friends
        composable(Screen.FriendsList.route) {
            FriendsListScreen(
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onNavigateToRequests = { navController.navigate(Screen.FriendRequests.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.FriendRequests.route) {
            FriendRequestsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        // Notifications
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onNavigateToPost = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onNavigateToFriendRequests = {
                    navController.navigate(Screen.FriendRequests.route)
                }
            )
        }

        // Messaging
        composable(Screen.Conversations.route) {
            ConversationsListScreen(
                onNavigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                onNavigateToNewConversation = {
                    navController.navigate(Screen.NewConversation.route)
                }
            )
        }
        composable(Screen.NewConversation.route) {
            NewConversationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId)) {
                        popUpTo(Screen.Conversations.route)
                    }
                }
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        // Search
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
