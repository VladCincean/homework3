package com.example.vlad.caloriecounter.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class FoodRecordEditActivity extends AppCompatActivity {
    private static final String TAG = FoodRecordEditActivity.class.getName();

    private Spinner spinnerFoodItem;
    private List<FoodItem> foodItems;
    private EditText editTextQuantity;

    private DatabaseReference mDbRefFoodItems;
    private DatabaseReference mDbRefFoodRecords;

    private String myUserId;

    private FoodRecord foodRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_food_record_edit);

        spinnerFoodItem = findViewById(R.id.spinnerFoodItemName);
        editTextQuantity = findViewById(R.id.editTextEditFoodQuantity);

        editTextQuantity.setText("");

        Intent intent = getIntent();
        myUserId = intent.getStringExtra("myUserId");
        foodRecord = (FoodRecord) intent.getSerializableExtra(FoodRecord.class.getName());

        editTextQuantity.setText(Integer.toString(foodRecord.getQuantity()));
        // the spinner is set in the onStart method

        mDbRefFoodItems = FirebaseDatabase.getInstance().getReference("foodItems");
        mDbRefFoodItems.keepSynced(true);
        mDbRefFoodRecords = FirebaseDatabase.getInstance().getReference("foodRecords");
        mDbRefFoodRecords.keepSynced(true);

        foodItems = new ArrayList<>();

        // update button
        Button buttonEditFoodUpdate = findViewById(R.id.buttonEditFoodUpdate);
        buttonEditFoodUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean dataSaved = saveData();
                if (dataSaved) {
                    finish();
                } else {
                    Toast.makeText(
                            FoodRecordEditActivity.this,
                            "Something went wrong while trying to update the food record",
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

        FoodRecord newFoodRecord = new FoodRecord(
                foodRecord.getId(),
                foodRecord.getUserId(),
                foodItem.getId(),
                foodItem.getName(),
                quantity
        );

        Intent intent = new Intent();
        intent.putExtra(FoodRecord.class.getName(), newFoodRecord);
        setResult(RESULT_OK, intent);

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
                        FoodRecordEditActivity.this,
                        android.R.layout.simple_spinner_item,
                        foodItems
                );
                spinnerFoodItem.setAdapter(adapter);

                // set current food item
                for (int i = 0; i < foodItems.size(); i++) {
                    if (foodItems.get(i).getId().equals(foodRecord.getFoodItemId())) {
                        spinnerFoodItem.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
