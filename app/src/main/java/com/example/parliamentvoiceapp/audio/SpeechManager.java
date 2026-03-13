package com.example.parliamentvoiceapp.audio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class SpeechManager {

    private static final String TAG = "SpeechManager";
    private SpeechRecognizer speechRecognizer;
    private final Context context;
    private final MutableLiveData<String> recognizedText = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isListening = new MutableLiveData<>(false);

    public SpeechManager(@NonNull Context context) {
        this.context = context;
        initSpeechRecognizer();
    }

    private void initSpeechRecognizer() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (speechRecognizer != null) {
                speechRecognizer.destroy();
            }

            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        Log.d(TAG, "Ready for speech");
                        isListening.postValue(true);
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        Log.d(TAG, "Speech beginning");
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) { }

                    @Override
                    public void onBufferReceived(byte[] buffer) { }

                    @Override
                    public void onEndOfSpeech() {
                        Log.d(TAG, "Speech end");
                        isListening.postValue(false);
                    }

                    @Override
                    public void onError(int error) {
                        String message = getErrorText(error);
                        Log.e(TAG, "Error: " + message);
                        isListening.postValue(false);
                        
                        if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                            recognizedText.postValue("Didn't catch that. Try again?");
                        } else {
                            recognizedText.postValue("Error: " + message);
                        }
                    }

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (matches != null && !matches.isEmpty()) {
                            Log.d(TAG, "Result: " + matches.get(0));
                            recognizedText.postValue(matches.get(0));
                        }
                        isListening.postValue(false);
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {
                        ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (matches != null && !matches.isEmpty()) {
                            recognizedText.postValue(matches.get(0));
                        }
                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) { }
                });
            } else {
                Log.e(TAG, "Speech recognition not available");
                recognizedText.postValue("Speech Recognition not available on this device.");
            }
        });
    }

    public LiveData<String> getRecognizedText() {
        return recognizedText;
    }

    public LiveData<Boolean> getIsListening() {
        return isListening;
    }

    public void startListening() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (speechRecognizer == null) {
                initSpeechRecognizer();
            }

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
            
            try {
                speechRecognizer.startListening(intent);
                recognizedText.postValue(""); // Clear previous text
            } catch (Exception e) {
                Log.e(TAG, "Failed to start listening", e);
                recognizedText.postValue("Failed to start microphone.");
            }
        });
    }

    public void stopListening() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
            }
            isListening.postValue(false);
        });
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO: return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT: return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Missing microphone permission";
            case SpeechRecognizer.ERROR_NETWORK: return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH: return "No speech match found";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "Recognition service busy";
            case SpeechRecognizer.ERROR_SERVER: return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "No speech input";
            default: return "Unknown error (" + errorCode + ")";
        }
    }

    public void destroy() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (speechRecognizer != null) {
                speechRecognizer.destroy();
                speechRecognizer = null;
            }
        });
    }
}