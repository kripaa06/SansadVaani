package com.example.parliamentvoiceapp.network

import com.google.gson.annotations.SerializedName

/**
 * Represent a single message in the conversation history for the LLM.
 */
data class HistoryItem(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

/**
 * Request payload for /api/ask
 */
data class AskRequest(
    @SerializedName("query") val query: String,
    @SerializedName("history") val history: List<HistoryItem> = emptyList()
)

/**
 * Response payload from /api/ask
 */
data class AskResponse(
    @SerializedName("answer") val answer: String?,
    @SerializedName("sources") val sources: List<Source>?
)

data class Source(
    @SerializedName("id") val id: String?,
    @SerializedName("similarity") val similarity: Double?
)