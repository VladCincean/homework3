package com.example.vlad.caloriecounter.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vlad.caloriecounter.R;
import com.example.vlad.caloriecounter.model.FoodItem;

import java.util.List;

public class FoodItemListAdapter extends ArrayAdapter<FoodItem> {
    private Activity context;
    private List<FoodItem> foodItems;

    public FoodItemListAdapter(@NonNull Activity context, List<FoodItem> foodItems) {
        super(context, R.layout.layout_row_food_item, foodItems);
        this.context = context;
        this.foodItems = foodItems;
    }

    public View getView(int pos, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_row_food_item, null);

        TextView rowItemName = listViewItem.findViewById(R.id.rowItemName);
        TextView rowItemCalories = listViewItem.findViewById(R.id.rowItemCalories);

        final FoodItem foodItem = foodItems.get(pos);

        rowItemName.setText(foodItem.getName());
        rowItemCalories.setText(foodItem.getCalories());

        return listViewItem;
    }

}
