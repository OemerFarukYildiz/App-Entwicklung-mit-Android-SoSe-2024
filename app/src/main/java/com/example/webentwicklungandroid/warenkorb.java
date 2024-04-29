package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class warenkorb extends AppCompatActivity {

    // Deklaration der UI-Komponenten und Firebase Referenzen
    private ListView cartList;
    private Button backButton, checkoutButton;
    private TextView emptyCartMessage;
    private CartItemAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warenkorb);

        // Firebase Authentifizierung und Benutzerzuweisung
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialisierung der Datenbankreferenz
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        // Zuordnung der UI-Komponenten aus dem Layout
        cartList = findViewById(R.id.cart_list);
        backButton = findViewById(R.id.back_button);
        checkoutButton = findViewById(R.id.checkout_button);
        emptyCartMessage = findViewById(R.id.empty_cart_message);

        // Initialisierung und Zuweisung des Adapters für das ListView
        adapter = new CartItemAdapter(this, new ArrayList<>());
        cartList.setAdapter(adapter);

        // Listener für den Zurück-Button
        backButton.setOnClickListener(v -> finish());

        // Listener für den Checkout-Button
        checkoutButton.setOnClickListener(v -> checkoutCart());

        // Lädt die Warenkorb-Items, wenn ein Benutzer angemeldet ist
        if (currentUser != null) {
            loadCartItems();
        } else {
            // Leitet zum Login um, wenn kein Benutzer angemeldet ist
            startActivity(new Intent(this, login.class));
            finish();
        }
    }

    // Lädt Warenkorb-Items aus Firebase
    private void loadCartItems() {
        DatabaseReference cartRef = databaseReference.child("users").child(currentUser.getUid()).child("cart");
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CartItem> items = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = snapshot.getKey();
                    Integer quantity = snapshot.getValue(Integer.class);
                    items.add(new CartItem(item, quantity));
                }
                adapter.clear();
                adapter.addAll(items);
                adapter.notifyDataSetChanged();
                emptyCartMessage.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Daten lesen fehlgeschlagen: " + databaseError.getMessage());
            }
        });
    }

    // Führt den Checkout-Vorgang aus
    private void checkoutCart() {
        DatabaseReference userCartRef = databaseReference.child("users").child(currentUser.getUid()).child("cart");
        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Integer> orderDetails = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String product = snapshot.getKey();
                    Integer quantity = snapshot.getValue(Integer.class);
                    orderDetails.put(product, quantity);
                }

                // Speichert Bestelldetails und bereinigt den Warenkorb nach erfolgreichem Kauf
                if (!orderDetails.isEmpty()) {
                    databaseReference.child("orders").child(currentUser.getUid()).push().setValue(orderDetails)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(warenkorb.this, "Kauf war erfolgreich!", Toast.LENGTH_LONG).show();
                                userCartRef.removeValue();
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                                emptyCartMessage.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> Log.e("Firebase", "Fehler beim Speichern der Bestellung", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Daten lesen fehlgeschlagen: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }
}
