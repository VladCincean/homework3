package com.example.vlad.caloriecounter.activity.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vlad.caloriecounter.R;
import com.example.vlad.caloriecounter.model.FoodItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodAdminAddActivity extends AppCompatActivity {
    private static final String TAG = FoodAdminAddActivity.class.getName();

    private EditText editTextName;
    private EditText editTextCarbs;
    private EditText editTextProteins;
    private EditText editTextFats;

    private DatabaseReference mDbRefFoodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_food_admin_add);

        editTextName = findViewById(R.id.editTextAddFoodName);
        editTextCarbs = findViewById(R.id.editTextAddFoodCarbs);
        editTextProteins = findViewById(R.id.editTextAddFoodProteins);
        editTextFats = findViewById(R.id.editTextAddFoodFats);

        editTextName.setText("");
        editTextCarbs.setText("");
        editTextProteins.setText("");
        editTextFats.setText("");

        mDbRefFoodItems = FirebaseDatabase.getInstance().getReference("foodItems");

        // add button
        Button buttonAddFoodSave = findViewById(R.id.buttonAddFoodSave);
        buttonAddFoodSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean dataSaved = saveData();
                if (dataSaved) {
                    Toast.makeText(
                            FoodAdminAddActivity.this,
                            "Food Item added successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                    Log.d(TAG, "11");
                    setResult(RESULT_OK);
                    Log.d(TAG, "22");
                    finish();
                    Log.d(TAG, "33");
                } else {
                    Toast.makeText(
                            FoodAdminAddActivity.this,
                            "Something went wrong while trying to save the new food item",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private boolean saveData() {
        String name = editTextName.getText().toString();
        int carbs = 0;
        int proteins = 0;
        int fats = 0;

        try {
            carbs = Integer.parseInt(editTextCarbs.getText().toString());
            proteins = Integer.parseInt(editTextProteins.getText().toString());
            fats = Integer.parseInt(editTextFats.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }

        if (!validateData(name, carbs, proteins, fats)) {
            Log.d(TAG, "saveData: data not valid");
            return false;
        }

        String id = mDbRefFoodItems.push().getKey();

        FoodItem foodItem = new FoodItem(id, name, carbs, proteins, fats);

        mDbRefFoodItems.child(id).setValue(foodItem);

        return true;
    }

    private boolean validateData(String name, int carbs, int proteins, int fats) {
        if (name.isEmpty()) {
            editTextName.setError("Name cannot be empty");
            editTextName.requestFocus();
            return false;
        }

        if ((carbs < 0) || (carbs > 100)) {
            editTextCarbs.setError("Number of carbohydrates is between 0 and 100");
            editTextCarbs.requestFocus();
            return false;
        }

        if ((proteins < 0) || (proteins > 100)) {
            editTextProteins.setError("Number of proteins is between 0 and 100");
            editTextProteins.requestFocus();
            return false;
        }

        if ((fats < 0) || (fats > 100)) {
            editTextFats.setError("Number of fats is between 0 and 100");
            editTextFats.requestFocus();
            return false;
        }

        if ((carbs + proteins + fats) > 100) {
            editTextCarbs.setError("Possibly wrong value");
            editTextProteins.setError("Possibly wrong value");
            editTextFats.setError("Possibly wrong value");
            editTextCarbs.requestFocus();
            return false;
        }

        return true;
    }
}
