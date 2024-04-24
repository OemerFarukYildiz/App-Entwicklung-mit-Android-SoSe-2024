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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Hier setzen Sie den spezifischen Link zu Ihrer Firebase-Datenbank ein
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        cartList = findViewById(R.id.cart_list);
        backButton = findViewById(R.id.back_button);
        checkoutButton = findViewById(R.id.checkout_button);
        emptyCartMessage = findViewById(R.id.empty_cart_message);

        adapter = new CartItemAdapter(this, new ArrayList<>());
        cartList.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
        checkoutButton.setOnClickListener(v -> checkoutCart());

        if (currentUser != null) {
            loadCartItems();
        } else {
            startActivity(new Intent(this, login.class));
            finish();
        }
    }

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
