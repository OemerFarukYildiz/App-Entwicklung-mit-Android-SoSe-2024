package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class brot extends AppCompatActivity {

    private Button backButton, toCartButton;
    private Button buyBroetchenButton, buyKaiserbroetchenButton, buySchwarzbrotButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brot);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Verwende die neue URL für die Datenbankreferenz
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        backButton = findViewById(R.id.back_to_main_button);
        toCartButton = findViewById(R.id.to_cart_button);
        buyBroetchenButton = findViewById(R.id.buy_broetchen_button);
        buyKaiserbroetchenButton = findViewById(R.id.buy_kaiserbroetchen_button);
        buySchwarzbrotButton = findViewById(R.id.buy_schwarzbrot_button);

        backButton.setOnClickListener(v -> finish());
        toCartButton.setOnClickListener(v -> navigateToCart());

        setupItemButton(buyBroetchenButton, "Brötchen");
        setupItemButton(buyKaiserbroetchenButton, "Kaiserbrötchen");
        setupItemButton(buySchwarzbrotButton, "Schwarzbrot");
    }

    private void setupItemButton(Button button, String productName) {
        button.setOnClickListener(v -> {
            if (currentUser != null) {
                addItemToCart(currentUser.getUid(), productName);
            } else {
                Log.e("Firebase", "Kein Benutzer angemeldet");
                startActivity(new Intent(this, login.class));
                finish();
            }
        });
    }

    private void navigateToCart() {
        Intent intent = new Intent(this, warenkorb.class);
        startActivity(intent);
    }

    private void addItemToCart(String userId, String productName) {
        DatabaseReference cartRef = databaseReference.child("users").child(userId).child("cart");
        cartRef.push().setValue(productName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Produkt erfolgreich hinzugefügt: " + productName);
                navigateToCart();
            } else {
                Log.e("Firebase", "Fehler beim Hinzufügen des Produkts", task.getException());
            }
        });
    }
}
