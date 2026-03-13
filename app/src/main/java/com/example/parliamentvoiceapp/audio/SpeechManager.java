package com.example.parliamentvoiceapp.audio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class SpeechManager {

    private SpeechRecognizer speechRecognizer;
    private Context context;
    private MutableLiveData<String> recognizedText = new MutableLiveData<>();
    private MutableLiveData<Boolean> isListening = new MutableLiveData<>(false);

    public SpeechManager(@NonNull Context context) {
        this.context = context;

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening.postValue(true);
                }

                @Override
                public void onBeginningOfSpeech() { }

                @Override
                public void onRmsChanged(float rmsdB) { }

                @Override
                public void onBufferReceived(byte[] buffer) { }

                @Override
                public void onEndOfSpeech() {
                    isListening.postValue(false);
                }

                @Override
                public void onError(int error) {
                    isListening.postValue(false);
                    recognizedText.postValue("Error recognizing speech. Please try again.");
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        recognizedText.postValue(matches.get(0));
                    }
                    isListening.postValue(false);
                }

                @Override
                public void onPartialResults(Bundle partialResults) { }

                @Override
                public void onEvent(int eventType, Bundle params) { }
            });
        } else {
            recognizedText.postValue("Speech Recognition not available on this device.");
        }
    }

    public LiveData<String> getRecognizedText() {
        return recognizedText;
    }

    public LiveData<Boolean> getIsListening() {
        return isListening;
    }

    public void startListening() {
        if (speechRecognizer != null && !Boolean.TRUE.equals(isListening.getValue())) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");  // you can add Hindi etc.
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            speechRecognizer.startListening(intent);
        }
    }

    public void stopListening() {
        if (speechRecognizer != null && Boolean.TRUE.equals(isListening.getValue())) {
            speechRecognizer.stopListening();
        }
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}