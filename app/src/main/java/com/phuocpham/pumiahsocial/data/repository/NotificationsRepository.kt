package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.NotificationWithProfile
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    suspend fun getNotifications(): Result<List<NotificationWithProfile>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun markAllAsRead(): Result<Unit>
    fun getUnreadCountFlow(): Flow<Int>
    suspend fun refreshUnreadCount()
    suspend fun deleteAllNotifications(): Result<Unit>
}
