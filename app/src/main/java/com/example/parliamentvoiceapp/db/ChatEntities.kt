package com.example.parliamentvoiceapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class LocalChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)