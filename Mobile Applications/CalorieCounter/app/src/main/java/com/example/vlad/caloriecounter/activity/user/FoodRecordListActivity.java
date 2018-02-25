package com.example.vlad.caloriecounter.activity.user;

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
import com.example.vlad.caloriecounter.adapter.FoodRecordListAdapter;
import com.example.vlad.caloriecounter.model.FoodRecord;
import com.example.vlad.caloriecounter.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodRecordListActivity extends AppCompatActivity {
    private static final String TAG = FoodRecordListActivity.class.getName();

    private ListView listView;
    private List<FoodRecord> foodRecords;

    private final int FOOD_RECORD_LIST_ACTIVITY_ADD_REQUST_CODE = 3;
    private final int FOOD_RECORD_LIST_ACTIVITY_EDIT_REQUEST_CODE = 4;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRefFoodRecords;
    private String myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mAuth = FirebaseAuth.getInstance();
        loadUserId();

        mDbRefFoodRecords = FirebaseDatabase.getInstance().getReference("foodRecords");
        mDbRefFoodRecords.keepSynced(true);

        foodRecords = new ArrayList<>();

        setContentView(R.layout.activity_food_record_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "R.id.fab::onClick");
                Intent intent = new Intent(view.getContext(), FoodRecordAddActivity.class);
                intent.putExtra("myUserId", myUserId);
                startActivityForResult(intent, FOOD_RECORD_LIST_ACTIVITY_ADD_REQUST_CODE);
            }
        });

        // logout button
        FloatingActionButton logoutButton = findViewById(R.id.userLogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "R.id.userLogoutButton");

                new AlertDialog.Builder(FoodRecordListActivity.this)
                        .setMessage("Are you sure you want to sign out?")
                        .setTitle(mAuth.getCurrentUser().getEmail())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                dialogInterface.dismiss();

                                Intent intent = new Intent(
                                        FoodRecordListActivity.this,
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

        // listViewUser
        listView = findViewById(R.id.listViewUser);
        listView.setLongClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "r.id.listViewUser::onItemClick");
                final FoodRecord foodRecord = (FoodRecord) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(view.getContext(), FoodRecordEditActivity.class);
                intent.putExtra("myUserId", myUserId);
                intent.putExtra(FoodRecord.class.getName(), foodRecord);
                intent.putExtra("position", i);
                startActivityForResult(intent, FOOD_RECORD_LIST_ACTIVITY_EDIT_REQUEST_CODE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "R.id.listViewUser::onItemLongClick");
                final FoodRecord foodRecord = (FoodRecord) adapterView.getItemAtPosition(i);

                new AlertDialog.Builder(FoodRecordListActivity.this)
                        .setMessage("Are you sure you want to delete it?")
                        .setTitle("Food Record Delete Dialog")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDbRefFoodRecords.child(foodRecord.getId()).removeValue();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(FoodRecordListActivity.this,
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

    private void loadUserId() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail()).limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myUserId = null;

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            myUserId = user.getId();
                        }

                        if (myUserId == null) {
                            Log.w(TAG, "failed to load current user id into variable");
                            Toast.makeText(
                                    FoodRecordListActivity.this,
                                    "Internal error: Failed to load user id",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case FOOD_RECORD_LIST_ACTIVITY_ADD_REQUST_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(
                            FoodRecordListActivity.this,
                            "Food record successfully added",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;

            case FOOD_RECORD_LIST_ACTIVITY_EDIT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    FoodRecord foodRecord = (FoodRecord) data.getExtras()
                            .getSerializable(FoodRecord.class.getName());
                    int position = data.getIntExtra("position", foodRecords.size());

                    if (foodRecord == null) {
                        Log.w(TAG, "null foodRecord received in onActivityResult EDIT request");
                        Toast.makeText(
                                FoodRecordListActivity.this,
                                "Internal error: null foddRecord received in onActivityResult EDIT request",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    if (position < foodRecords.size()) {
                        foodRecords.set(position, foodRecord);
                    }

                    mDbRefFoodRecords.child(foodRecord.getId()).setValue(foodRecord);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        mDbRefFoodRecords
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        foodRecords.clear();

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            FoodRecord foodRecord = childSnapshot.getValue(FoodRecord.class);
                            if (myUserId.equals(foodRecord.getUserId())) {
                                foodRecords.add(foodRecord);
                            }
                        }

                        ArrayAdapter<FoodRecord> adapter = new ArrayAdapter<FoodRecord>(
                                FoodRecordListActivity.this,
                                android.R.layout.simple_expandable_list_item_1,
                                foodRecords
                        );
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
