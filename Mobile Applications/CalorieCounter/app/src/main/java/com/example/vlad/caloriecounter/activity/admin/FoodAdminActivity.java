package com.example.vlad.caloriecounter.activity.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vlad.caloriecounter.LoginMainActivity;
import com.example.vlad.caloriecounter.R;
import com.example.vlad.caloriecounter.adapter.FoodItemListAdapter;
import com.example.vlad.caloriecounter.model.FoodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodAdminActivity extends AppCompatActivity  {
    private static final String TAG = FoodAdminActivity.class.getName();

    private ListView listView;
    private List<FoodItem> foodItems;

    private final int FOOD_ADMIN_ACTIVITY_ADD_REQUEST_CODE = 1;
    private final int FOOD_ADMIN_ACTIVITY_EDIT_REQUEST_CODE = 2;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRefFoodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mAuth = FirebaseAuth.getInstance();

        mDbRefFoodItems = FirebaseDatabase.getInstance().getReference("foodItems");
        mDbRefFoodItems.keepSynced(true);

        foodItems = new ArrayList<>();

        setContentView(R.layout.activity_food_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "R.id.fab::onClick");
                Intent intent = new Intent(view.getContext(), FoodAdminAddActivity.class);
                startActivityForResult(intent, FOOD_ADMIN_ACTIVITY_ADD_REQUEST_CODE);
            }
        });

        // logout button
        FloatingActionButton logoutButton = findViewById(R.id.adminLogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "R.id.adminLogoutButton");

                new AlertDialog.Builder(FoodAdminActivity.this)
                        .setMessage("Are you sure you want to sign out?")
                        .setTitle(mAuth.getCurrentUser().getEmail())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                dialogInterface.dismiss();

                                Intent intent = new Intent(
                                        FoodAdminActivity.this,
                                        LoginMainActivity.class
                                );
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        // listViewAdmin
        listView = findViewById(R.id.listViewAdmin);
        listView.setLongClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "R.id.listViewAdmin::onItemClick");
                final FoodItem foodItem = (FoodItem)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(view.getContext(), FoodAdminEditActivity.class);
                intent.putExtra(FoodItem.class.getName(), foodItem);
                intent.putExtra("position", i);
                startActivityForResult(intent, FOOD_ADMIN_ACTIVITY_EDIT_REQUEST_CODE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "R.id.listViewAdmin::onItemLongClick");
                final FoodItem foodItem = (FoodItem)adapterView.getItemAtPosition(i);
//                final FoodItem foodItem = foodItems.get(i);

                new AlertDialog.Builder(FoodAdminActivity.this)
                        .setMessage("Are you sure you want to delete it?")
                        .setTitle(foodItem.getName())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDbRefFoodItems.child(foodItem.getId()).removeValue();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(FoodAdminActivity.this,
                                        "Not deleted. Still there.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case FOOD_ADMIN_ACTIVITY_ADD_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(
                            FoodAdminActivity.this,
                            "Food item successfully added",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;

            case FOOD_ADMIN_ACTIVITY_EDIT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    FoodItem foodItem = (FoodItem) data.getExtras()
                            .getSerializable(FoodItem.class.getName());
                    int position = data.getIntExtra("position", foodItems.size());

                    if (foodItem == null) {
                        Log.w(TAG, "null foodItem received in onActivityResult EDIT request");
                        Toast.makeText(
                                FoodAdminActivity.this,
                                "Internal error: null foddItem received in onActivityResult EDIT request",
                                Toast.LENGTH_SHORT
                        ).show();
                        break;
                    }

                    if (position < foodItems.size()) {
                        foodItems.set(position, foodItem);
                    }

                    mDbRefFoodItems.child(foodItem.getId()).setValue(foodItem);
                }
                break;
        }
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

//                ArrayAdapter<FoodItem> adapter = new ArrayAdapter<FoodItem>(
//                        FoodAdminActivity.this,
//                        android.R.layout.simple_expandable_list_item_1,
//                        foodItems
//                );
                ArrayAdapter<FoodItem> adapter = new FoodItemListAdapter(
                        FoodAdminActivity.this,
                        foodItems
                );
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
