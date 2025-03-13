package com.example.bluetoothscoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private Button btnToggleSco;
    private TextView tvTranscription;
    private AudioManager audioManager;
    private SpeechRecognizer speechRecognizer;
    private boolean isScoOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToggleSco = findViewById(R.id.btnToggleSco);
        tvTranscription = findViewById(R.id.tvTranscription);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        checkAndRequestPermissions();
        setupSpeechRecognizer();
        setupButtonClickListener();
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT
        };

        ArrayList<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    tvTranscription.setText(text);
                }
            }

            @Override
            public void onReadyForSpeech(Bundle params) {}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {
                startListening();  // Restart listening when speech ends
            }
            @Override
            public void onError(int error) {
                startListening();  // Restart listening on error
            }
            @Override
            public void onPartialResults(Bundle partialResults) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void setupButtonClickListener() {
        btnToggleSco.setOnClickListener(v -> {
            if (!isScoOn) {
                startSco();
            } else {
                stopSco();
            }
        });
    }

    private void startSco() {
        if (!audioManager.isBluetoothScoAvailableOffCall()) {
            tvTranscription.setText("Bluetooth SCO is not available on this device");
            return;
        }

        audioManager.startBluetoothSco();
        audioManager.setBluetoothScoOn(true);
        isScoOn = true;
        btnToggleSco.setText("Stop Bluetooth SCO");
        startListening();
    }

    private void stopSco() {
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        isScoOn = false;
        btnToggleSco.setText("Start Bluetooth SCO");
        speechRecognizer.stopListening();
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizer.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (isScoOn) {
            stopSco();
        }
    }
}