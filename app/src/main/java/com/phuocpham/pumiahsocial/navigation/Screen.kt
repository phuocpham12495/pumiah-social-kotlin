package com.phuocpham.pumiahsocial.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object SignUp : Screen("signup")

    // Main
    data object Feed : Screen("feed")
    data object CreatePost : Screen("create_post")
    data object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    // Profile
    data object MyProfile : Screen("my_profile")
    data object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
    data object EditProfile : Screen("edit_profile")
    data object CreateProfile : Screen("create_profile")

    // Friends
    data object FriendsList : Screen("friends_list")
    data object FriendRequests : Screen("friend_requests")

    // Notifications
    data object Notifications : Screen("notifications")

    // Messaging
    data object Conversations : Screen("conversations")
    data object NewConversation : Screen("new_conversation")
    data object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }

    // Search
    data object Search : Screen("search")

    // Settings
    data object Settings : Screen("settings")
}
