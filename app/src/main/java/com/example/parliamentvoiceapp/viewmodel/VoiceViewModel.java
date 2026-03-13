package com.example.parliamentvoiceapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.parliamentvoiceapp.audio.SpeechManager;

import java.util.HashMap;
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
            if (text != null && !text.isEmpty()) {
                String autoCorrected = autocorrect(text);
                correctedText.postValue(autoCorrected);
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

    public LiveData<String> getCorrectedText() {
        return correctedText;
    }

    public LiveData<Boolean> getIsListening() {
        return speechManager.getIsListening();
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