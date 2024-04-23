package com.example.webentwicklungandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class shoppingMenu extends AppCompatActivity {

    Button backButton, fruitButton, sweetsButton, breadButton, cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_menu);

        // Initialisiere die Buttons
        backButton = findViewById(R.id.back_button);
        fruitButton = findViewById(R.id.fruit_button);
        sweetsButton = findViewById(R.id.sweets_button);
        breadButton = findViewById(R.id.bread_button);
        cartButton = findViewById(R.id.cart_button);

        // Setze OnClickListener für jeden Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beendet die aktuelle Activity und kehrt zur vorherigen zurück
                finish();
            }
        });

        fruitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Startet die Früchte-Seite
                Intent intent = new Intent(shoppingMenu.this, fruechte.class);
                startActivity(intent);
            }
        });

        sweetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Startet die Süßigkeiten-Seite
                Intent intent = new Intent(shoppingMenu.this, suessigkeiten.class);
                startActivity(intent);
            }
        });

        breadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Startet die Brot-Seite
                Intent intent = new Intent(shoppingMenu.this, brot.class);
                startActivity(intent);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Startet die Warenkorb-Seite
                Intent intent = new Intent(shoppingMenu.this, warenkorb.class);
                startActivity(intent);
            }
        });
    }
}
