package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.local.dao.MessageDao
import com.phuocpham.pumiahsocial.data.local.entity.MessageEntity
import com.phuocpham.pumiahsocial.data.model.Conversation
import com.phuocpham.pumiahsocial.data.model.ConversationWithDetails
import com.phuocpham.pumiahsocial.data.model.Message
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val messageDao: MessageDao
) : MessagesRepository {

    private val currentUserId: String
        get() = auth.currentUserOrNull()?.id ?: ""

    override suspend fun getConversations(): Result<List<ConversationWithDetails>> = safeApiCall {
        val convos1 = postgrest.from("conversations")
            .select { filter { eq("user1_id", currentUserId) } }
            .decodeList<Conversation>()
        val convos2 = postgrest.from("conversations")
            .select { filter { eq("user2_id", currentUserId) } }
            .decodeList<Conversation>()

        val allConvos = convos1 + convos2
        if (allConvos.isEmpty()) return@safeApiCall emptyList()

        val otherUserIds = allConvos.map { convo ->
            if (convo.user1Id == currentUserId) convo.user2Id else convo.user1Id
        }.distinct()

        val profiles = postgrest.from("profiles")
            .select { filter { isIn("id", otherUserIds) } }
            .decodeList<Profile>()
            .associateBy { it.id }

        allConvos.map { convo ->
            val otherUserId = if (convo.user1Id == currentUserId) convo.user2Id else convo.user1Id

            val lastMessage = try {
                postgrest.from("messages")
                    .select {
                        filter { eq("conversation_id", convo.id) }
                        order("created_at", Order.DESCENDING)
                        limit(1)
                    }
                    .decodeList<Message>()
                    .firstOrNull()
            } catch (_: Exception) { null }

            val unreadCount = try {
                postgrest.from("messages")
                    .select {
                        filter {
                            eq("conversation_id", convo.id)
                            eq("is_read", false)
                            neq("sender_id", currentUserId)
                        }
                    }
                    .decodeList<Message>()
                    .size
            } catch (_: Exception) { 0 }

            ConversationWithDetails(
                conversation = convo,
                otherUserProfile = profiles[otherUserId],
                lastMessage = lastMessage,
                unreadCount = unreadCount
            )
        }.sortedByDescending { it.lastMessage?.createdAt ?: it.conversation.createdAt }
    }

    override suspend fun getMessages(conversationId: String): Result<List<Message>> = safeApiCall {
        val messages = postgrest.from("messages")
            .select {
                filter { eq("conversation_id", conversationId) }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<Message>()

        messageDao.upsertMessages(messages.map { MessageEntity.fromMessage(it) })
        messages
    }

    override suspend fun sendMessage(conversationId: String, content: String): Result<Message> = safeApiCall {
        val message = mapOf(
            "conversation_id" to conversationId,
            "sender_id" to currentUserId,
            "content" to content
        )
        postgrest.from("messages").insert(message) {
            select()
        }.decodeSingle<Message>()
    }

    override suspend fun getOrCreateConversation(otherUserId: String): Result<String> = safeApiCall {
        // Check both orderings
        val existing1 = postgrest.from("conversations")
            .select {
                filter {
                    eq("user1_id", currentUserId)
                    eq("user2_id", otherUserId)
                }
            }
            .decodeList<Conversation>()

        if (existing1.isNotEmpty()) return@safeApiCall existing1.first().id

        val existing2 = postgrest.from("conversations")
            .select {
                filter {
                    eq("user1_id", otherUserId)
                    eq("user2_id", currentUserId)
                }
            }
            .decodeList<Conversation>()

        if (existing2.isNotEmpty()) return@safeApiCall existing2.first().id

        // Create new conversation
        val convo = mapOf(
            "user1_id" to currentUserId,
            "user2_id" to otherUserId
        )
        postgrest.from("conversations").insert(convo) {
            select()
        }.decodeSingle<Conversation>().id
    }

    override suspend fun markConversationRead(conversationId: String): Result<Unit> = safeApiCall {
        postgrest.from("messages").update(
            mapOf("is_read" to true)
        ) {
            filter {
                eq("conversation_id", conversationId)
                eq("is_read", false)
                neq("sender_id", currentUserId)
            }
        }
    }
}
