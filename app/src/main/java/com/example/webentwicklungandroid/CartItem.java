package com.example.webentwicklungandroid;

// Klasse CartItem definiert ein Warenkorb-Element mit Name und Menge
public class CartItem {
    // Private Variablen für den Namen und die Menge des Produkts
    private String name;  // Name des Produkts
    private int quantity; // Menge des Produkts im Warenkorb

    // Konstruktor für die Klasse CartItem, initialisiert Name und Menge
    public CartItem(String name, int quantity) {
        this.name = name;       // Setzt den Namen des Produkts
        this.quantity = quantity; // Setzt die Menge des Produkts
    }

    // Getter-Methode, um den Namen des Produkts zu erhalten
    public String getName() {
        return name;
    }

    // Setter-Methode, um den Namen des Produkts zu setzen
    public void setName(String name) {
        this.name = name;
    }

    // Getter-Methode, um die Menge des Produkts zu erhalten
    public int getQuantity() {
        return quantity;
    }

    // Setter-Methode, um die Menge des Produkts zu ändern
    public void setQuantity(int quantity) {
        this.quantity = quantity; // Setzt die neue Menge des Produkts
    }
}
