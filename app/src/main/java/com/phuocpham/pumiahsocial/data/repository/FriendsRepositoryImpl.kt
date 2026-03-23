package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.local.dao.FriendDao
import com.phuocpham.pumiahsocial.data.local.entity.FriendEntity
import com.phuocpham.pumiahsocial.data.model.Friend
import com.phuocpham.pumiahsocial.data.model.FriendRequest
import com.phuocpham.pumiahsocial.data.model.FriendRequestWithProfile
import com.phuocpham.pumiahsocial.data.model.FriendshipStatus
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val friendDao: FriendDao
) : FriendsRepository {

    private val currentUserId: String
        get() = auth.currentUserOrNull()?.id ?: ""

    override suspend fun sendFriendRequest(receiverId: String): Result<Unit> = safeApiCall {
        // Delete any previous declined/accepted request to avoid unique constraint violation
        postgrest.from("friend_requests").delete {
            filter {
                eq("sender_id", currentUserId)
                eq("receiver_id", receiverId)
                neq("status", "pending")
            }
        }
        postgrest.from("friend_requests").delete {
            filter {
                eq("sender_id", receiverId)
                eq("receiver_id", currentUserId)
                neq("status", "pending")
            }
        }
        // Insert new pending request
        postgrest.from("friend_requests").insert(
            mapOf(
                "sender_id" to currentUserId,
                "receiver_id" to receiverId,
                "status" to "pending"
            )
        )
        // Create notification (non-blocking so friend request still succeeds)
        try {
            postgrest.from("notifications").insert(
                mapOf(
                    "recipient_id" to receiverId,
                    "sender_id" to currentUserId,
                    "type" to "friend_request",
                    "message" to "đã gửi cho bạn lời mời kết bạn",
                    "target_url" to ""
                )
            )
        } catch (_: Exception) { }
    }

    override suspend fun acceptFriendRequest(requestId: String, senderId: String): Result<Unit> = safeApiCall {
        // Update request status
        postgrest.from("friend_requests").update(
            mapOf("status" to "accepted")
        ) { filter { eq("id", requestId) } }

        // Insert into friends table (ensure user1_id < user2_id)
        val (u1, u2) = if (currentUserId < senderId) {
            currentUserId to senderId
        } else {
            senderId to currentUserId
        }
        postgrest.from("friendships").insert(
            mapOf("user1_id" to u1, "user2_id" to u2)
        )

        // Notify sender (non-blocking)
        try {
            postgrest.from("notifications").insert(
                mapOf(
                    "recipient_id" to senderId,
                    "sender_id" to currentUserId,
                    "type" to "friend_request",
                    "message" to "đã chấp nhận lời mời kết bạn của bạn",
                    "target_url" to ""
                )
            )
        } catch (_: Exception) { }

        refreshFriendsList()
    }

    override suspend fun declineFriendRequest(requestId: String): Result<Unit> = safeApiCall {
        postgrest.from("friend_requests").update(
            mapOf("status" to "declined")
        ) { filter { eq("id", requestId) } }
    }

    override suspend fun removeFriend(friendUserId: String): Result<Unit> = safeApiCall {
        val (u1, u2) = if (currentUserId < friendUserId) {
            currentUserId to friendUserId
        } else {
            friendUserId to currentUserId
        }
        postgrest.from("friendships").delete {
            filter {
                eq("user1_id", u1)
                eq("user2_id", u2)
            }
        }
        // Also clean up friend_requests so getFriendshipStatus returns NONE
        postgrest.from("friend_requests").delete {
            filter {
                or {
                    and {
                        eq("sender_id", currentUserId)
                        eq("receiver_id", friendUserId)
                    }
                    and {
                        eq("sender_id", friendUserId)
                        eq("receiver_id", currentUserId)
                    }
                }
            }
        }
        refreshFriendsList()
    }

    override suspend fun getFriendsList(userId: String): Result<List<Profile>> = safeApiCall {
        val friends1 = postgrest.from("friendships")
            .select { filter { eq("user1_id", userId) } }
            .decodeList<Friend>()
        val friends2 = postgrest.from("friendships")
            .select { filter { eq("user2_id", userId) } }
            .decodeList<Friend>()

        val friendIds = friends1.map { it.user2Id } + friends2.map { it.user1Id }

        if (friendIds.isEmpty()) return@safeApiCall emptyList()

        postgrest.from("profiles")
            .select { filter { isIn("id", friendIds) } }
            .decodeList<Profile>()
    }

    override suspend fun getPendingRequests(): Result<List<FriendRequestWithProfile>> = safeApiCall {
        val requests = postgrest.from("friend_requests")
            .select { filter { eq("receiver_id", currentUserId); eq("status", "pending") } }
            .decodeList<FriendRequest>()

        val senderIds = requests.map { it.senderId }
        val profiles = if (senderIds.isNotEmpty()) {
            postgrest.from("profiles")
                .select { filter { isIn("id", senderIds) } }
                .decodeList<Profile>()
                .associateBy { it.id }
        } else emptyMap()

        requests.map { request ->
            FriendRequestWithProfile(
                request = request,
                senderProfile = profiles[request.senderId]
            )
        }
    }

    override suspend fun getFriendshipStatus(otherUserId: String): FriendshipStatus {
        return try {
            // Check if already friends in friendships table
            val (u1, u2) = if (currentUserId < otherUserId) {
                currentUserId to otherUserId
            } else {
                otherUserId to currentUserId
            }
            val friends = postgrest.from("friendships")
                .select {
                    filter {
                        eq("user1_id", u1)
                        eq("user2_id", u2)
                    }
                }
                .decodeList<Friend>()
            if (friends.isNotEmpty()) return FriendshipStatus.FRIENDS

            // Check all friend requests between the two users (both directions)
            val allRequests = postgrest.from("friend_requests")
                .select {
                    filter {
                        or {
                            and {
                                eq("sender_id", currentUserId)
                                eq("receiver_id", otherUserId)
                            }
                            and {
                                eq("sender_id", otherUserId)
                                eq("receiver_id", currentUserId)
                            }
                        }
                    }
                }
                .decodeList<FriendRequest>()

            for (request in allRequests) {
                when {
                    // Accepted request means friends (even if friendships entry is missing)
                    request.status == "accepted" -> return FriendshipStatus.FRIENDS
                    // Pending request sent by current user
                    request.status == "pending" && request.senderId == currentUserId ->
                        return FriendshipStatus.PENDING_SENT
                    // Pending request received by current user
                    request.status == "pending" && request.receiverId == currentUserId ->
                        return FriendshipStatus.PENDING_RECEIVED
                }
            }

            FriendshipStatus.NONE
        } catch (e: Exception) {
            FriendshipStatus.NONE
        }
    }

    override fun getFriendIdsFlow(): Flow<List<String>> {
        return friendDao.getAllFriends().map { friends ->
            friends.map { it.friendUserId }
        }
    }

    private suspend fun refreshFriendsList() {
        val friends1 = postgrest.from("friendships")
            .select { filter { eq("user1_id", currentUserId) } }
            .decodeList<Friend>()
        val friends2 = postgrest.from("friendships")
            .select { filter { eq("user2_id", currentUserId) } }
            .decodeList<Friend>()

        val entities = friends1.map {
            FriendEntity(it.user1Id, it.user2Id, it.user2Id, it.createdAt)
        } + friends2.map {
            FriendEntity(it.user1Id, it.user2Id, it.user1Id, it.createdAt)
        }
        friendDao.clearAll()
        friendDao.upsertFriends(entities)
    }
}
