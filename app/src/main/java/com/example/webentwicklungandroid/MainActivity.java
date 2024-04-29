package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Firebase Authentifizierungsinstanz
    FirebaseAuth auth;
    // UI-Komponenten
    Button logoutButton;
    Button shoppingMenuButton;
    Button warenkorbButton;
    Button bestellHistorieButton;
    TextView userDetails;
    // Aktuell angemeldeter Benutzer
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Lädt das Layout für die Hauptaktivität

        // Initialisiert Firebase Auth
        auth = FirebaseAuth.getInstance();
        // Verbindet die UI-Elemente mit den in der Layout-Datei definierten IDs
        logoutButton = findViewById(R.id.logout);
        shoppingMenuButton = findViewById(R.id.shopping_menu_button);
        warenkorbButton = findViewById(R.id.cart_button);
        bestellHistorieButton = findViewById(R.id.order_history_button);
        userDetails = findViewById(R.id.user_details);
        // Holt den derzeit angemeldeten Benutzer
        user = auth.getCurrentUser();

        // Überprüft, ob ein Benutzer angemeldet ist
        if (user == null) {
            // Wenn kein Benutzer angemeldet ist, navigiere zur Login-Seite
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();  // Beendet die aktuelle Activity
        } else {
            // Zeigt die E-Mail des angemeldeten Benutzers an
            userDetails.setText(user.getEmail());
        }

        // Setzt einen OnClickListener für den Logout-Button
        logoutButton.setOnClickListener(v -> {
            auth.signOut();  // Meldet den Benutzer ab
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();  // Beendet die aktuelle Activity
        });

        // OnClickListener für den Shopping-Menü-Button
        shoppingMenuButton.setOnClickListener(v -> {
            // Navigiert zur Shopping-Menü-Activity
            Intent intent = new Intent(getApplicationContext(), shoppingMenu.class);
            startActivity(intent);
        });

        // OnClickListener für den Warenkorb-Button
        warenkorbButton.setOnClickListener(v -> {
            // Navigiert zur Warenkorb-Activity
            Intent intent = new Intent(getApplicationContext(), warenkorb.class);
            startActivity(intent);
        });

        // OnClickListener für den Bestellhistorie-Button
        bestellHistorieButton.setOnClickListener(v -> {
            // Navigiert zur Bestellhistorie-Activity
            Intent intent = new Intent(getApplicationContext(), bestellhistorie.class);
            startActivity(intent);
        });
    }
}
