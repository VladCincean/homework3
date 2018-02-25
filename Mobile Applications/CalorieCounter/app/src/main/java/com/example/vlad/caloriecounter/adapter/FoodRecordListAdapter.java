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
import com.example.vlad.caloriecounter.model.FoodRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 14.01.2018.
 */

public class FoodRecordListAdapter extends ArrayAdapter<FoodRecord> {
    private Activity context;
    private List<FoodRecord> foodRecords;
    private List<FoodItem> foodItems;

    public FoodRecordListAdapter(@NonNull Activity context, List<FoodRecord> foodRecords) {
        super(context, R.layout.layout_row_food_item, foodRecords);
        this.context = context;
        this.foodRecords = foodRecords;
        this.foodItems = new ArrayList<>();
    }

    public View getView(int pos, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_row_food_item, null);

        TextView rowItemName = listViewItem.findViewById(R.id.rowItemName);
        TextView rowItemCalories = listViewItem.findViewById(R.id.rowItemCalories);

        final FoodRecord foodRecord = foodRecords.get(pos);
        FoodItem foodItem = null;
        for (FoodItem fi : foodItems) {
            if (fi.getId().equals(foodRecord.getFoodItemId())) {
                foodItem = fi;
                break;
            }
        }

        int calories = (int) (foodRecord.getQuantity() * (4.1 * foodItem.getCarbohydrates()
                + 4.1 * foodItem.getProteins() + 9.3 * foodItem.getFats()) / 100);
        String kcal = Integer.toString(calories) + " kcal";

        rowItemName.setText(foodItem.getName());
        rowItemCalories.setText(kcal);

        return listViewItem;
    }
}
