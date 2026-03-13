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
        // SpeechRecognizer must be created on the Main Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        Log.d(TAG, "onReadyForSpeech");
                        isListening.postValue(true);
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        Log.d(TAG, "onBeginningOfSpeech");
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) { }

                    @Override
                    public void onBufferReceived(byte[] buffer) { }

                    @Override
                    public void onEndOfSpeech() {
                        Log.d(TAG, "onEndOfSpeech");
                        isListening.postValue(false);
                    }

                    @Override
                    public void onError(int error) {
                        String message = getErrorText(error);
                        Log.e(TAG, "onError: " + message);
                        isListening.postValue(false);
                        // Only show error if it's not a "no match" during active listening
                        if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                             recognizedText.postValue("Error: " + message);
                        }
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
                recognizedText.postValue("Speech Recognition not available");
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
            if (speechRecognizer != null) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                
                try {
                    speechRecognizer.startListening(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start listening", e);
                }
            }
        });
    }

    public void stopListening() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
                isListening.postValue(false);
            }
        });
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO: return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT: return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK: return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH: return "No match found";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "RecognitionService busy";
            case SpeechRecognizer.ERROR_SERVER: return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "No speech input";
            default: return "Unknown error";
        }
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}