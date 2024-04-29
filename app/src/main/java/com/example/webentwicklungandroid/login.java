package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    // UI-Komponenten
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView registerNowTextView;

    // Firebase Authentifizierung
    private FirebaseAuth mAuth;

    // Überprüft beim Start der Activity, ob der Benutzer bereits angemeldet ist
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            navigateToMainActivity();  // Direktnavigation zur MainActivity, falls bereits angemeldet
        }
    }

    // Initialisierung der Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();  // Instanz von FirebaseAuth initialisieren
        initView();  // Initialisiert die UI-Komponenten
    }

    // Initialisiert die UI-Komponenten und setzt Event Listener
    private void initView() {
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.Password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        registerNowTextView = findViewById(R.id.RegisterNow);

        // Listener für die Navigation zur Registrierungsseite
        registerNowTextView.setOnClickListener(view -> navigateToRegister());

        // Listener für den Login-Vorgang
        buttonLogin.setOnClickListener(view -> attemptLogin());
    }

    // Führt den Login-Vorgang aus
    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim(); // E-Mail aus dem Eingabefeld holen
        String password = editTextPassword.getText().toString().trim(); // Passwort aus dem Eingabefeld holen

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Zeigt die Fortschrittsanzeige
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE); // Verbirgt die Fortschrittsanzeige
                    if (task.isSuccessful()) {
                        navigateToMainActivity(); // Navigation zur Hauptseite bei Erfolg
                    } else {
                        Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show(); // Fehlermeldung bei Fehlschlag
                    }
                });
    }

    // Navigiert zur Hauptaktivität
    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Beendet die aktuelle Activity
    }

    // Navigiert zur Registrierungsseite
    private void navigateToRegister() {
        startActivity(new Intent(this, register.class));
        finish(); // Beendet die aktuelle Activity
    }
}
