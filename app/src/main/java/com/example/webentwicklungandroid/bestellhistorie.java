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
    private ListView orderListView; // ListView für die Anzeige der Bestellhistorie
    private Button backButton; // Button für die Rücknavigation
    private DatabaseReference databaseReference; // Referenz für die Firebase-Datenbank
    private FirebaseUser currentUser; // Derzeit angemeldeter Firebase-Benutzer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestell_historie);

        // Initialisierung der UI-Komponenten
        orderListView = findViewById(R.id.order_list);
        backButton = findViewById(R.id.back_to_main_button);

        // Authentifizierung und Datenbankinitialisierung
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        // Event Listener für den Zurück-Button, der die MainActivity startet
        backButton.setOnClickListener(v -> startActivity(new Intent(bestellhistorie.this, MainActivity.class)));

        // Prüft, ob ein Benutzer angemeldet ist und lädt gegebenenfalls die Bestellhistorie
        if (currentUser != null) {
            loadOrderHistory();
        } else {
            Toast.makeText(this, "Benutzer nicht angemeldet!", Toast.LENGTH_SHORT).show();
        }
    }

    // Methode zum Laden der Bestellhistorie aus Firebase
    private void loadOrderHistory() {
        DatabaseReference ordersRef = databaseReference.child("orders").child(currentUser.getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {   // https://stackoverflow.com/questions/47487817/list-retrieving-from-firebase/47487941#47487941
                ArrayList<String> orders = new ArrayList<>();
                // Iteriert über alle Bestellungen
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Long> order = (Map<String, Long>) snapshot.getValue();
                    StringBuilder orderDetails = new StringBuilder("Bestellung: ");
                    // Baut einen String mit den Details jeder Bestellung auf
                    if (order != null) {
                        for (Map.Entry<String, Long> entry : order.entrySet()) {
                            orderDetails.append(entry.getKey()).append(" x ").append(entry.getValue()).append(", ");
                        }
                        // Entfernt das letzte Komma und fügt die Bestellung zur Liste hinzu
                        orders.add(orderDetails.substring(0, orderDetails.length() - 2));
                    }
                }
                // Setzt den Adapter für die ListView mit den Bestelldetails
                ArrayAdapter<String> adapter = new ArrayAdapter<>(bestellhistorie.this, android.R.layout.simple_list_item_1, orders);
                orderListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Loggt Fehler, falls das Laden der Daten fehlschlägt
                Log.e("Firebase", "Fehler beim Laden der Bestellhistorie: " + databaseError.getMessage());
            }
        });
    }
}
