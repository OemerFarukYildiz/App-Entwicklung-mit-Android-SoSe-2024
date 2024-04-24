package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        cartList = findViewById(R.id.cart_list);
        backButton = findViewById(R.id.back_button);
        checkoutButton = findViewById(R.id.checkout_button);
        emptyCartMessage = findViewById(R.id.empty_cart_message);

        adapter = new CartItemAdapter(this, new ArrayList<>());
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

    private class CartItemAdapter extends ArrayAdapter<CartItem> {
        CartItemAdapter(AppCompatActivity context, ArrayList<CartItem> cartItems) {
            super(context, 0, cartItems);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_warenkorb, parent, false);
            }
            TextView itemName = convertView.findViewById(R.id.item_name);
            TextView quantityText = convertView.findViewById(R.id.quantity_text);
            Button subtractButton = convertView.findViewById(R.id.button_decrease);
            Button addButton = convertView.findViewById(R.id.button_increase);

            CartItem currentItem = getItem(position);
            itemName.setText(currentItem.getName());
            quantityText.setText(String.valueOf(currentItem.getQuantity()));

            subtractButton.setOnClickListener(v -> {
                int currentQuantity = currentItem.getQuantity();
                if (currentQuantity > 1) {
                    currentItem.setQuantity(currentQuantity - 1);
                    updateItemInDatabase(currentItem);
                } else {
                    removeFromDatabase(currentItem.getName());
                }
            });

            addButton.setOnClickListener(v -> {
                int currentQuantity = currentItem.getQuantity();
                currentItem.setQuantity(currentQuantity + 1);
                updateItemInDatabase(currentItem);
            });

            return convertView;
        }

        private void updateItemInDatabase(CartItem cartItem) {
            databaseReference.child("users").child(currentUser.getUid()).child("cart")
                    .child(cartItem.getName()).setValue(cartItem.getQuantity());
        }

        private void removeFromDatabase(String itemName) {
            databaseReference.child("users").child(currentUser.getUid()).child("cart")
                    .child(itemName).removeValue();
        }
    }

    // Repräsentiert ein Item im Warenkorb
    public static class CartItem {
        private String name;
        private int quantity;

        public CartItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
