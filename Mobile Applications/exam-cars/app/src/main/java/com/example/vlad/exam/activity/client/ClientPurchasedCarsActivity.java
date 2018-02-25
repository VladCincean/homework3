package com.example.vlad.exam.activity.client;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vlad.exam.R;
import com.example.vlad.exam.adapter.PurchasedCarAdapter;
import com.example.vlad.exam.controller.Controller;
import com.example.vlad.exam.controller.MyCallback;
import com.example.vlad.exam.model.PurchasedCar;
import com.example.vlad.exam.room.AppDatabase;

import java.util.List;

import timber.log.Timber;

public class ClientPurchasedCarsActivity extends AppCompatActivity {
    private List<PurchasedCar> carList;
    private ListView listView;
    private PurchasedCarAdapter mAdapter;

    private ProgressBar progressBar;
    private FloatingActionButton fabRemoveAll;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_client_purchased_cars);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        controller = Controller.getController(getApplicationContext());

        listView = findViewById(R.id.list_view_purchased_cars);

        carList = AppDatabase.getInstance(ClientPurchasedCarsActivity.this)
                .getPurchasedCarDao().selectAllSync();

        mAdapter = new PurchasedCarAdapter(
                this,
                carList
        );
        listView.setAdapter(mAdapter);

        AppDatabase.getInstance(ClientPurchasedCarsActivity.this)
                .getPurchasedCarDao()
                .selectAll()
                .observe(
                        ClientPurchasedCarsActivity.this,
                        new Observer<List<PurchasedCar>>() {
                            @Override
                            public void onChanged(@Nullable List<PurchasedCar> purchasedCars) {
                                carList.clear();
                                if (purchasedCars != null) {
                                    carList.addAll(purchasedCars);
                                }
                                mAdapter.setData(carList);
                            }
                        }
                );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final PurchasedCar purchasedCar = carList.get(i);

                new AlertDialog.Builder(ClientPurchasedCarsActivity.this)
                        .setTitle("Wanna return?")
                        .setMessage(purchasedCar.getName())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressBar.setVisibility(View.VISIBLE);
                                controller.returnCar(
                                        purchasedCar.getId(),
                                        progressBar,
                                        new MyCallback() {
                                            @Override
                                            public void showError(String message) {
                                                Snackbar.make(listView, message, Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("Dismiss", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // do nothing ...
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void clear() {
                                                // ...
                                            }

                                            @Override
                                            public void onRequestSuccess() {
                                                Toast.makeText(
                                                        ClientPurchasedCarsActivity.this,
                                                        "Car was returned",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                );
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(
                                        ClientPurchasedCarsActivity.this,
                                        "Not returned. Still in your garage.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
                        .show();
            }
        });

        fabRemoveAll = findViewById(R.id.fab_removeAll);
        fabRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.removeAllCars(
                        progressBar,
                        new MyCallback() {
                            @Override
                            public void showError(String message) {
                                Snackbar.make(listView, message, Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // do nothing ...
                                            }
                                        });
                            }

                            @Override
                            public void clear() {
                                // ...
                            }

                            @Override
                            public void onRequestSuccess() {
                                Toast.makeText(
                                        ClientPurchasedCarsActivity.this,
                                        "All cars from your garage were removed.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart");
    }
}
