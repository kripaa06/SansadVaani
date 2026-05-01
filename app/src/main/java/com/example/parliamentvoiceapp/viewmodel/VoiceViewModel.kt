package com.example.parliamentvoiceapp.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.parliamentvoiceapp.audio.SpeechManager;
import com.example.parliamentvoiceapp.network.ApiService;
import com.example.parliamentvoiceapp.network.AskRequest;
import com.example.parliamentvoiceapp.network.AskResponse;
import com.example.parliamentvoiceapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoiceViewModel extends AndroidViewModel {

    private static final String TAG = "VoiceViewModel";
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

    private MutableLiveData<List<ChatMessage>> chatHistory = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistory;
    }

    public void submitQuery(String query) {
        if (query == null || query.trim().isEmpty()) return;
        
        List<ChatMessage> currentList = new ArrayList<>(chatHistory.getValue());
        currentList.add(new ChatMessage(query, true));
        chatHistory.postValue(currentList);
        
        // Clear the current input text since it's already sent
        correctedText.postValue("");
        
        // Call the backend API
        ApiService apiService = RetrofitClient.getApiService();
        AskRequest request = new AskRequest(query);
        
        apiService.askQuestion(request).enqueue(new Callback<AskResponse>() {
            @Override
            public void onResponse(Call<AskResponse> call, Response<AskResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String answer = response.body().getAnswer();
                    addBotResponse(answer);
                } else {
                    Log.e(TAG, "API Error: " + response.code());
                    addBotResponse("Error: Unable to get response from server. (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<AskResponse> call, Throwable t) {
                Log.e(TAG, "API Failure: " + t.getMessage());
                addBotResponse("Error: Network failure. Please check your connection.");
            }
        });
    }

    private void addBotResponse(String responseText) {
        List<ChatMessage> updatedList = new ArrayList<>(chatHistory.getValue());
        updatedList.add(new ChatMessage(responseText, false));
        chatHistory.postValue(updatedList);
    }

    public void clearChat() {
        chatHistory.postValue(new ArrayList<>());
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