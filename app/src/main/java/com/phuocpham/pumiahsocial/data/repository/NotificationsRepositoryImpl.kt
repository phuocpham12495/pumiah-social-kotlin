package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.Notification
import com.phuocpham.pumiahsocial.data.model.NotificationWithProfile
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : NotificationsRepository {

    private val currentUserId: String
        get() = auth.currentUserOrNull()?.id ?: ""

    private val _unreadCount = MutableStateFlow(0)

    override suspend fun getNotifications(): Result<List<NotificationWithProfile>> = safeApiCall {
        val raw = postgrest.from("notifications")
            .select {
                filter { eq("recipient_id", currentUserId) }
                order("created_at", Order.DESCENDING)
                limit(100)
            }
            .decodeList<Notification>()

        // Dedupe: group by (sender_id, type) within a 1-minute window,
        // preferring the Vietnamese variant (message starts with "đã" or contains Vietnamese diacritics).
        val notifications = raw
            .groupBy { n ->
                val bucket = n.createdAt.take(15) // yyyy-MM-ddTHH:mm (minute precision)
                listOf(n.senderId, n.type, bucket)
            }
            .map { (_, group) ->
                group.firstOrNull { isVietnamese(it.message) } ?: group.first()
            }
            .sortedByDescending { it.createdAt }

        val senderIds = notifications.mapNotNull { it.senderId }.distinct()
        val profiles = if (senderIds.isNotEmpty()) {
            postgrest.from("profiles")
                .select { filter { isIn("id", senderIds) } }
                .decodeList<Profile>()
                .associateBy { it.id }
        } else emptyMap()

        _unreadCount.value = notifications.count { !it.isRead }

        notifications.map { notification ->
            NotificationWithProfile(
                notification = notification,
                senderProfile = notification.senderId?.let { profiles[it] }
            )
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = safeApiCall {
        postgrest.from("notifications").update(
            mapOf("is_read" to true)
        ) { filter { eq("id", notificationId) } }
        _unreadCount.value = (_unreadCount.value - 1).coerceAtLeast(0)
    }

    override suspend fun markAllAsRead(): Result<Unit> = safeApiCall {
        postgrest.from("notifications").update(
            mapOf("is_read" to true)
        ) {
            filter {
                eq("recipient_id", currentUserId)
                eq("is_read", false)
            }
        }
        _unreadCount.value = 0
    }

    override suspend fun deleteAllNotifications(): Result<Unit> = safeApiCall {
        postgrest.from("notifications").delete {
            filter { eq("recipient_id", currentUserId) }
        }
        _unreadCount.value = 0
    }

    override fun getUnreadCountFlow(): Flow<Int> = _unreadCount

    private fun isVietnamese(message: String?): Boolean {
        if (message.isNullOrBlank()) return false
        val vnMarkers = Regex("[đĐàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹ]")
        return vnMarkers.containsMatchIn(message) || message.startsWith("đã ")
    }

    override suspend fun refreshUnreadCount() {
        try {
            val notifications = postgrest.from("notifications")
                .select {
                    filter {
                        eq("recipient_id", currentUserId)
                        eq("is_read", false)
                    }
                }
                .decodeList<Notification>()
            _unreadCount.value = notifications.size
        } catch (_: Exception) {}
    }
}
