package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.FriendRequestWithProfile
import com.phuocpham.pumiahsocial.data.model.FriendshipStatus
import com.phuocpham.pumiahsocial.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {
    suspend fun sendFriendRequest(receiverId: String): Result<Unit>
    suspend fun acceptFriendRequest(requestId: String, senderId: String): Result<Unit>
    suspend fun declineFriendRequest(requestId: String): Result<Unit>
    suspend fun removeFriend(friendUserId: String): Result<Unit>
    suspend fun getFriendsList(userId: String): Result<List<Profile>>
    suspend fun getPendingRequests(): Result<List<FriendRequestWithProfile>>
    suspend fun getFriendshipStatus(otherUserId: String): FriendshipStatus
    fun getFriendIdsFlow(): Flow<List<String>>
}
