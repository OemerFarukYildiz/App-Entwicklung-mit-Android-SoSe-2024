package com.example.webentwicklungandroid;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class warenkorb extends AppCompatActivity {

    private ListView cartList;
    private Button backButton, checkoutButton;
    private TextView emptyCartMessage;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> cartItems = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warenkorb);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        backButton = findViewById(R.id.back_button);
        checkoutButton = findViewById(R.id.checkout_button);
        cartList = findViewById(R.id.cart_list);
        emptyCartMessage = findViewById(R.id.empty_cart_message);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cartItems);
        cartList.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
        checkoutButton.setOnClickListener(v -> startActivity(new Intent(this, checkout.class)));

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
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItems.clear();
                quantities.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = snapshot.getKey();
                    Integer quantity = snapshot.getValue(Integer.class);
                    cartItems.add(item + " - Menge: " + quantity);
                    quantities.add(quantity);
                }
                adapter.notifyDataSetChanged();
                emptyCartMessage.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Daten lesen fehlgeschlagen: " + databaseError.getMessage());
            }
        });
    }
}
