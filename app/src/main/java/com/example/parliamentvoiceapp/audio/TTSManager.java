package com.example.parliamentvoiceapp.audio;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class TTSManager {

    private static final String TAG = "TTSManager";
    private TextToSpeech tts;
    private boolean isInitialized = false;

    public TTSManager(Context context) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Specifically attempt to set Hindi locale for parliamentary responses
                Locale hindi = new Locale("hi", "IN");
                int result = tts.setLanguage(hindi);
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Hindi language is not supported on this device, falling back to default.");
                    tts.setLanguage(Locale.getDefault());
                }
                
                tts.setPitch(1.0f);
                tts.setSpeechRate(0.9f); // Slightly slower for better clarity
                isInitialized = true;
            } else {
                Log.e(TAG, "TTS Initialization failed!");
            }
        });
    }

    public void speak(String text) {
        if (isInitialized && text != null && !text.isEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SansadVaaniTTS");
        }
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}