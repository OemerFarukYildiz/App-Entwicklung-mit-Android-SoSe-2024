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

    FirebaseAuth auth;
    Button logoutButton;
    Button shoppingMenuButton;
    Button warenkorbButton;
    Button bestellHistorieButton;
    TextView userDetails;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout);
        shoppingMenuButton = findViewById(R.id.shopping_menu_button);
        warenkorbButton = findViewById(R.id.cart_button);
        bestellHistorieButton = findViewById(R.id.order_history_button);
        userDetails = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        } else {
            userDetails.setText(user.getEmail());
        }

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        });

        shoppingMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), shoppingMenu.class);
            startActivity(intent);
        });

        warenkorbButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), warenkorb.class);
            startActivity(intent);
        });

        bestellHistorieButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), bestellhistorie.class);
            startActivity(intent);
        });
    }
}
