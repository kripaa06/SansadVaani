package com.example.parliamentvoiceapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.parliamentvoiceapp.audio.SpeechManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceViewModel extends AndroidViewModel {

    private SpeechManager speechManager;
    private MutableLiveData<String> correctedText = new MutableLiveData<>();

    // Simple dictionary for autocorrect (expand as needed)
    private static final Map<String, String> DICTIONARY = new HashMap<>();

    static {
        DICTIONARY.put("teh", "the");
        DICTIONARY.put("recieve", "receive");
        DICTIONARY.put("adress", "address");
        // add more common misspellings
    }

    public VoiceViewModel(@NonNull Application application) {
        super(application);
        speechManager = new SpeechManager(application.getApplicationContext());

        // Observe recognized text and autocorrect it
        speechManager.getRecognizedText().observeForever(text -> {
            if (text != null) {
                if (text.isEmpty()) {
                    correctedText.postValue("");
                } else {
                    String autoCorrected = autocorrect(text);
                    correctedText.postValue(autoCorrected);
                }
            }
        });
    }

    /**
     * Basic autocorrect method that replaces common misspelled words from the dictionary.
     * Can be improved with more sophisticated algorithms or Android spellchecker.
     */
    private String autocorrect(String input) {
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();

        for (String word : words) {
            String lowerWord = word.toLowerCase();
            if (DICTIONARY.containsKey(lowerWord)) {
                corrected.append(DICTIONARY.get(lowerWord));
            } else {
                corrected.append(word);
            }
            corrected.append(" ");
        }

        return corrected.toString().trim();
    }

    public static class ChatMessage {
        public String text;
        public boolean isUser;
        public ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    private MutableLiveData<List<ChatMessage>> chatHistory = new MutableLiveData<>(new java.util.ArrayList<>());

    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistory;
    }

    public void submitQuery(String query) {
        if (query == null || query.trim().isEmpty()) return;
        List<ChatMessage> currentList = new java.util.ArrayList<>(chatHistory.getValue());
        currentList.add(new ChatMessage(query, true));
        chatHistory.postValue(currentList);
        
        // Clear the current input text since it's already sent
        correctedText.postValue("");
        
        // Simulating the fixed AI response after a short delay
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            List<ChatMessage> updatedList = new java.util.ArrayList<>(chatHistory.getValue());
            updatedList.add(new ChatMessage("Thank you for your parliamentary inquiry. I am the Parliament Voice Assistant. I will provide a detailed, context-aware political AI response once my backend is fully connected.", false));
            chatHistory.postValue(updatedList);
        }, 1000);
    }

    public void clearChat() {
        chatHistory.postValue(new java.util.ArrayList<>());
    }

    public LiveData<String> getCorrectedText() {
        return correctedText;
    }

    public void updateText(String newText) {
        correctedText.postValue(newText);
    }

    public LiveData<Boolean> getIsListening() {
        return speechManager.getIsListening();
    }

    public LiveData<Float> getRmsDb() {
        return speechManager.getRmsDb();
    }

    public void startListening() {
        speechManager.startListening();
    }

    public void stopListening() {
        speechManager.stopListening();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        speechManager.destroy();
    }
}