package com.example.webentwicklungandroid;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

import java.util.ArrayList;

public class CartItemAdapter extends ArrayAdapter<CartItem> {
    // Konstruktor des Adapters
    public CartItemAdapter(Context context, ArrayList<CartItem> items) {
        super(context, R.layout.list_item_warenkorb, items);
    }

    // Diese Methode wird aufgerufen, um jede Zeile in der ListView zu erstellen
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // ViewHolder Pattern für bessere Performance
        ViewHolder holder;

        // Überprüfung, ob ein bestehendes View wiederverwendet werden kann, sonst inflating des Views
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

        // Holen des aktuellen Items basierend auf der Position
        CartItem currentItem = getItem(position);

        // Setzen des Item-Namens und der Menge
        holder.itemName.setText(currentItem.getName());
        holder.quantityText.setText(String.valueOf(currentItem.getQuantity()));

        // Setzen der Button-Logiken zum Erhöhen und Verringern der Menge
        holder.decreaseButton.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            if (currentQuantity > 1) {
                currentItem.setQuantity(currentQuantity - 1);
                notifyDataSetChanged();
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            currentItem.setQuantity(currentQuantity + 1);
            notifyDataSetChanged();
        });

        return convertView;
    }

    // ViewHolder Klasse für besseres Caching der Views
    private static class ViewHolder {
        TextView itemName;
        TextView quantityText;
        Button decreaseButton;
        Button increaseButton;
    }
}
