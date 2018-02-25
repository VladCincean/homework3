package com.example.vlad.caloriecounter.activity.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vlad.caloriecounter.R;
import com.example.vlad.caloriecounter.model.FoodItem;

public class FoodAdminEditActivity extends AppCompatActivity {
    private static final String TAG = FoodAdminEditActivity.class.toString();

    private EditText editTextName;
    private EditText editTextCarbs;
    private EditText editTextProteins;
    private EditText editTextFats;

    private FoodItem foodItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_food_admin_edit);

        editTextName = findViewById(R.id.editTextEditFoodName);
        editTextCarbs = findViewById(R.id.editTextEditFoodCarbs);
        editTextProteins = findViewById(R.id.editTextEditFoodProteins);
        editTextFats = findViewById(R.id.editTextEditFoodFats);

        Intent intent = getIntent();
        foodItem = (FoodItem) intent.getSerializableExtra(FoodItem.class.getName());

        editTextName.setText(foodItem.getName());
        editTextCarbs.setText(Integer.toString(foodItem.getCarbohydrates()));
        editTextProteins.setText(Integer.toString(foodItem.getProteins()));
        editTextFats.setText(Integer.toString(foodItem.getFats()));

        Button buttonEditFoodUpdate = findViewById(R.id.buttonEditFoodUpdate);
        buttonEditFoodUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean dataSaved = saveData();
                if (dataSaved) {
                    finish();
                } else {
                    Toast.makeText(
                            FoodAdminEditActivity.this,
                            "Data coundn't be updated :(",
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
            return false;
        }

        FoodItem newFoodItem = new FoodItem(
                foodItem.getId(),
                name, carbs,
                proteins,
                fats
        );

        Intent intent = new Intent();
        intent.putExtra(FoodItem.class.getName(), newFoodItem);
        setResult(RESULT_OK, intent);

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
