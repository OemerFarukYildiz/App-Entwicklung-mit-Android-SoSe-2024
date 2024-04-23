package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class warenkorb extends AppCompatActivity {

    private ListView cartList;
    private Button backButton, checkoutButton;
    private TextView emptyCartMessage;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> cartItems;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warenkorb);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Verwende die neue URL für die Datenbankreferenz
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        cartList = findViewById(R.id.cart_list);
        backButton = findViewById(R.id.back_button);
        checkoutButton = findViewById(R.id.checkout_button);
        emptyCartMessage = findViewById(R.id.empty_cart_message);

        cartItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cartItems);
        cartList.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(warenkorb.this, MainActivity.class);
            startActivity(intent);
        });

        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(warenkorb.this, checkout.class);
            startActivity(intent);
        });

        if (currentUser != null) {
            updateCartView();
        }
    }

    private void updateCartView() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference cartRef = databaseReference.child("users").child(userId).child("cart");
            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cartItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String item = snapshot.getValue(String.class);
                        cartItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    emptyCartMessage.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
                    cartList.setVisibility(cartItems.isEmpty() ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Daten lesen fehlgeschlagen: " + databaseError.getMessage());
                }
            });
        } else {
            startActivity(new Intent(this, login.class));
            finish();
        }
    }
}
