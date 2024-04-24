package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.Map;

public class bestellhistorie extends AppCompatActivity {
    private ListView orderListView;
    private Button backButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestell_historie);

        orderListView = findViewById(R.id.order_list);
        backButton = findViewById(R.id.back_to_main_button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        backButton.setOnClickListener(v -> startActivity(new Intent(bestellhistorie.this, MainActivity.class)));

        if (currentUser != null) {
            loadOrderHistory();
        } else {
            Toast.makeText(this, "Benutzer nicht angemeldet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrderHistory() {
        DatabaseReference ordersRef = databaseReference.child("orders").child(currentUser.getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> orders = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Long> order = (Map<String, Long>) snapshot.getValue();
                    StringBuilder orderDetails = new StringBuilder("Bestellung: ");
                    if (order != null) {
                        for (Map.Entry<String, Long> entry : order.entrySet()) {
                            orderDetails.append(entry.getKey()).append(" x ").append(entry.getValue()).append(", ");
                        }
                        orders.add(orderDetails.toString());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(bestellhistorie.this, android.R.layout.simple_list_item_1, orders);
                orderListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Fehler beim Laden der Bestellhistorie: " + databaseError.getMessage());
            }
        });
    }
}
