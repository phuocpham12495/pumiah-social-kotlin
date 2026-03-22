package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.ConversationWithDetails
import com.phuocpham.pumiahsocial.data.model.Message

interface MessagesRepository {
    suspend fun getConversations(): Result<List<ConversationWithDetails>>
    suspend fun getMessages(conversationId: String): Result<List<Message>>
    suspend fun sendMessage(conversationId: String, content: String): Result<Message>
    suspend fun getOrCreateConversation(otherUserId: String): Result<String>
    suspend fun markConversationRead(conversationId: String): Result<Unit>
}
