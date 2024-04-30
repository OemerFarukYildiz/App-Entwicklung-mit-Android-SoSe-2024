package com.example.webentwicklungandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

// Adapter-Klasse für CartItem, erweitert ArrayAdapter, um CartItem-Objekte in einer ListView anzuzeigen
public class CartItemAdapter extends ArrayAdapter<CartItem> {
    private DatabaseReference databaseReference; // Referenz für Firebase-Datenbank

    // Konstruktor des Adapters, der den Kontext, das Layout und die Liste der CartItems erhält
    public CartItemAdapter(Context context, ArrayList<CartItem> items) {
        super(context, R.layout.list_item_warenkorb, items);
        // Initialisiert die Firebase-Datenbankreferenz mit dem spezifischen Link
        databaseReference = FirebaseDatabase.getInstance("https://login-register-7710b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    // Diese Methode wird aufgerufen, um jede Zeile in der ListView zu erstellen
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        // Überprüfung, ob ein bestehendes View wiederverwendet werden kann, sonst Inflation des Views
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_warenkorb, parent, false);
            holder = new ViewHolder();
            holder.itemName = convertView.findViewById(R.id.item_name);
            holder.quantityText = convertView.findViewById(R.id.quantity_text);
            holder.decreaseButton = convertView.findViewById(R.id.button_decrease);
            holder.increaseButton = convertView.findViewById(R.id.button_increase);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Holen des aktuellen CartItem-Objekts basierend auf der Position
        CartItem currentItem = getItem(position);

        // Setzen des Namen und der Menge des CartItem-Objekts auf die TextViews
        holder.itemName.setText(currentItem.getName());
        holder.quantityText.setText(String.valueOf(currentItem.getQuantity()));

        // Setzen der OnClickListener für die "Verringern" und "Erhöhen" Buttons
        holder.decreaseButton.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            if (currentQuantity > 1) { // Menge verringern, wenn sie größer als 1 ist
                updateItemQuantity(currentItem, currentQuantity - 1);
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            updateItemQuantity(currentItem, currentQuantity + 1); // Menge erhöhen
        });

        return convertView; // Rückgabe der fertigen Ansicht
    }

    // Aktualisiert die Menge eines CartItems sowohl im Adapter als auch in der Firebase-Datenbank
    private void updateItemQuantity(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        notifyDataSetChanged(); // Benachrichtigt den Adapter über die Änderung
        // Aktualisiert die Datenbank mit der neuen Menge
        databaseReference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("cart")
                .child(item.getName())
                .setValue(newQuantity);
    }

    // ViewHolder Klasse hilft bei der besseren Performance durch das Caching der View-Elemente
    private static class ViewHolder {
        TextView itemName;
        TextView quantityText;
        Button decreaseButton;
        Button increaseButton;
    }
}
