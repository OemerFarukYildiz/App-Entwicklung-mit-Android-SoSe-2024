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

public class register extends AppCompatActivity {
    // UI-Komponenten
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonReg;
    private ProgressBar progressBar;
    private TextView loginNowTextView;

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
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();  // Instanz von FirebaseAuth initialisieren
        initView();  // Initialisiert die UI-Komponenten
    }

    // Initialisiert die UI-Komponenten und setzt Event Listener
    private void initView() {
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.Password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginNowTextView = findViewById(R.id.loginNow);

        // Listener für die Navigation zurück zur Login-Seite
        loginNowTextView.setOnClickListener(view -> navigateToLogin());

        // Listener für den Registrierungsprozess
        buttonReg.setOnClickListener(view -> attemptRegister());
    }

    // Führt den Registrierungsvorgang aus
    private void attemptRegister() {
        String email = editTextEmail.getText().toString().trim(); // E-Mail aus dem Eingabefeld holen
        String password = editTextPassword.getText().toString().trim(); // Passwort aus dem Eingabefeld holen

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Zeigt die Fortschrittsanzeige
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE); // Verbirgt die Fortschrittsanzeige
                    if (task.isSuccessful()) {
                        Toast.makeText(register.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        navigateToLogin(); // Navigation zur Login-Seite nach erfolgreicher Registrierung
                    } else {
                        Toast.makeText(register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show(); // Fehlermeldung bei Fehlschlag
                    }
                });
    }

    // Navigiert zur Hauptaktivität
    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Beendet die aktuelle Activity
    }

    // Navigiert zur Login-Seite
    private void navigateToLogin() {
        startActivity(new Intent(this, login.class));
        finish(); // Beendet die aktuelle Activity
    }
}
