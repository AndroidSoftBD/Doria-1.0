package com.example.data.local

import kotlinx.coroutines.flow.Flow

class ChatHistoryRepository(private val chatDao: ChatDao) {

    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()

    suspend fun insertMessage(
        text: String,
        isUser: Boolean,
        engineModel: String = "Doria 1.0",
        isVoiceCommand: Boolean = false
    ): Long {
        val entity = ChatMessageEntity(
            text = text,
            isUser = isUser,
            timestamp = System.currentTimeMillis(),
            engineModel = engineModel,
            isVoiceCommand = isVoiceCommand
        )
        return chatDao.insertMessage(entity)
    }

    suspend fun deleteMessage(id: Long) {
        chatDao.deleteMessageById(id)
    }

    suspend fun clearHistory() {
        chatDao.clearAllMessages()
    }

    suspend fun getMessageCount(): Int {
        return chatDao.getMessageCount()
    }
}
