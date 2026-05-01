package com.example.parliamentvoiceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.parliamentvoiceapp.audio.SpeechManager
import com.example.parliamentvoiceapp.audio.TTSManager
import com.example.parliamentvoiceapp.db.AppDatabase
import com.example.parliamentvoiceapp.db.ChatSession
import com.example.parliamentvoiceapp.db.LocalChatMessage
import com.example.parliamentvoiceapp.network.AskRequest
import com.example.parliamentvoiceapp.network.HistoryItem
import com.example.parliamentvoiceapp.network.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val speechManager = SpeechManager(application.applicationContext)
    private val ttsManager = TTSManager(application.applicationContext)
    
    private val _correctedText = MutableLiveData<String>("")
    val correctedText: LiveData<String> = _correctedText

    private val _chatHistory = MutableLiveData<List<ChatMessage>>(emptyList())
    val chatHistory: LiveData<List<ChatMessage>> = _chatHistory

    private var currentSessionId: Long? = null
    private var sessionObservationJob: Job? = null

    private val _allSessions = MutableLiveData<List<ChatSession>>(emptyList())
    val allSessions: LiveData<List<ChatSession>> = _allSessions

    data class ChatMessage(
        val text: String,
        val isUser: Boolean,
        val isError: Boolean = false
    )

    init {
        speechManager.recognizedText.observeForever { text ->
            if (text != null) _correctedText.postValue(text)
        }

        viewModelScope.launch {
            chatDao.getAllSessions().collectLatest {
                _allSessions.postValue(it)
            }
        }
    }

    fun loadSession(sessionId: Long) {
        // Cancel previous session observation to prevent data mixing
        sessionObservationJob?.cancel()
        currentSessionId = sessionId
        
        sessionObservationJob = viewModelScope.launch {
            chatDao.getMessagesForSession(sessionId).collectLatest { messages ->
                _chatHistory.postValue(messages.map { 
                    ChatMessage(it.text, it.isUser, it.isError) 
                })
            }
        }
    }

    fun startNewSession() {
        sessionObservationJob?.cancel()
        currentSessionId = null
        _chatHistory.value = emptyList()
        _correctedText.value = ""
    }

    fun submitQuery(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            // 1. Ensure Session exists
            if (currentSessionId == null) {
                val newId = chatDao.insertSession(ChatSession(title = query))
                currentSessionId = newId
                // Start observing the new session immediately
                loadSession(newId)
            }

            val sessionId = currentSessionId!!

            // 2. Immediate UI Update for User Message
            val updatedListWithUser = (_chatHistory.value ?: emptyList()).toMutableList()
            updatedListWithUser.add(ChatMessage(query, true))
            _chatHistory.value = updatedListWithUser
            _correctedText.value = ""
            ttsManager.stop()

            // 3. Background Save
            launch { chatDao.insertMessage(LocalChatMessage(sessionId = sessionId, text = query, isUser = true)) }

            // 4. Prepare Context for Backend
            val historyItems = updatedListWithUser
                .filter { !it.isError }
                .map { HistoryItem(role = if (it.isUser) "user" else "assistant", content = it.text) }
                .dropLast(1)

            // 5. API Call
            try {
                val response = RetrofitClient.apiService.askQuestion(
                    AskRequest(query = query, history = historyItems)
                )
                val answer = response.answer ?: "क्षमा करें, कोई उत्तर नहीं मिला।"
                
                // 6. Immediate UI Update for Assistant Bubble
                val updatedListWithAI = _chatHistory.value?.toMutableList() ?: mutableListOf()
                updatedListWithAI.add(ChatMessage(answer, false))
                _chatHistory.postValue(updatedListWithAI)

                // 7. Speak and Save in Parallel
                ttsManager.speak(answer)
                launch { chatDao.insertMessage(LocalChatMessage(sessionId = sessionId, text = answer, isUser = false)) }
                
            } catch (e: Exception) {
                val errorMsg = "Network error. Please try again."
                val updatedListWithError = _chatHistory.value?.toMutableList() ?: mutableListOf()
                updatedListWithError.add(ChatMessage(errorMsg, false, true))
                _chatHistory.postValue(updatedListWithError)
                ttsManager.speak("नेटवर्क त्रुटि।")
            }
        }
    }

    fun updateText(newText: String) = _correctedText.postValue(newText)
    fun clearChat() = startNewSession()

    val isListening: LiveData<Boolean> = speechManager.isListening
    val rmsDb: LiveData<Float> = speechManager.rmsDb

    fun startListening() {
        ttsManager.stop()
        speechManager.startListening()
    }
    
    fun stopListening() = speechManager.stopListening()

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
        ttsManager.shutdown()
    }
}