package com.example.vlad.caloriecounter.activity.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class FoodRecordAddActivity extends AppCompatActivity {
    private static final String TAG = FoodRecordAddActivity.class.getName();

    private Spinner spinnerFoodItem;
    private List<FoodItem> foodItems;
    private EditText editTextQuantity;

    private DatabaseReference mDbRefFoodItems;
    private DatabaseReference mDbRefFoodRecords;

    private String myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_food_record_add);

        spinnerFoodItem = findViewById(R.id.spinnerFoodItemName);
        editTextQuantity = findViewById(R.id.editTextAddFoodQuantity);

        editTextQuantity.setText("");

        myUserId = getIntent().getStringExtra("myUserId");

        mDbRefFoodItems = FirebaseDatabase.getInstance().getReference("foodItems");
        mDbRefFoodItems.keepSynced(true);
        mDbRefFoodRecords = FirebaseDatabase.getInstance().getReference("foodRecords");
        mDbRefFoodRecords.keepSynced(true);

        foodItems = new ArrayList<>();

        // add button
        Button buttonAddFoodSave = findViewById(R.id.buttonAddFoodSave);
        buttonAddFoodSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean dataSaved = saveData();
                if (dataSaved) {
                    Toast.makeText(
                            FoodRecordAddActivity.this,
                            "Food Record added successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(
                            FoodRecordAddActivity.this,
                            "Something went wrong while trying to save the new food record",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private boolean saveData() {
        FoodItem foodItem = (FoodItem) spinnerFoodItem.getSelectedItem();
        int quantity = 0;

        try {
            quantity = Integer.parseInt(editTextQuantity.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }

        // validate
        if (quantity < 0) {
            Log.d(TAG, "saveData: data not valid");
            editTextQuantity.setError("Quantity must be positive");
            editTextQuantity.requestFocus();
            return false;
        }

        String id = mDbRefFoodRecords.push().getKey();

        FoodRecord foodRecord = new FoodRecord(
                id,
                myUserId,
                foodItem.getId(),
                foodItem.getName(),
                quantity
        );

        mDbRefFoodRecords.child(id).setValue(foodRecord);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        mDbRefFoodItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodItems.clear();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    FoodItem foodItem = childSnapshot.getValue(FoodItem.class);
                    foodItems.add(foodItem);
                }

                ArrayAdapter<FoodItem> adapter = new ArrayAdapter<FoodItem>(
                        FoodRecordAddActivity.this,
                        android.R.layout.simple_spinner_item,
                        foodItems
                );
                spinnerFoodItem.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
