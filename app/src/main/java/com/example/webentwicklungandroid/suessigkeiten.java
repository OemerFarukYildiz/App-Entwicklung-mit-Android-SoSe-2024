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

public class suessigkeiten extends AppCompatActivity {

    private Button backButton, toCartButton;
    private Button buyChipsButton, buyLakritzeButton, buyMuffinsButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suessigkeiten);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        backButton = findViewById(R.id.back_to_main_button_sweets);
        toCartButton = findViewById(R.id.to_cart_button_sweets);
        buyChipsButton = findViewById(R.id.buy_chips_button);
        buyLakritzeButton = findViewById(R.id.buy_lakritze_button);
        buyMuffinsButton = findViewById(R.id.buy_muffins_button);

        backButton.setOnClickListener(v -> finish());
        toCartButton.setOnClickListener(v -> navigateToCart());

        setupItemButton(buyChipsButton, "Chips");
        setupItemButton(buyLakritzeButton, "Lakritze");
        setupItemButton(buyMuffinsButton, "Muffins");
    }

    private void setupItemButton(Button button, String productName) {
        button.setOnClickListener(v -> {
            if (currentUser != null) {
                addItemToCart(currentUser.getUid(), productName, 1);
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

    private void addItemToCart(String userId, String productName, int quantity) {
        DatabaseReference cartRef = databaseReference.child("users").child(userId).child("cart").child(productName);
        cartRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer existingQuantity = task.getResult().getValue(Integer.class);
                if (existingQuantity != null) {
                    cartRef.setValue(existingQuantity + quantity);
                } else {
                    cartRef.setValue(quantity);
                }
            } else {
                Log.e("Firebase", "Fehler beim Hinzufügen des Produkts", task.getException());
            }
        });
    }
}
