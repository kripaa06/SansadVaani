package com.example.parliamentvoiceapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<ChatSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSession): Long

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<LocalChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: LocalChatMessage)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}