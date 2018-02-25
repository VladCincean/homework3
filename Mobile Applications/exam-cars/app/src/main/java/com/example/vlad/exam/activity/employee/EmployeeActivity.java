package com.example.vlad.exam.activity.employee;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vlad.exam.R;
import com.example.vlad.exam.adapter.CarEmployeeAdapter;
import com.example.vlad.exam.controller.Controller;
import com.example.vlad.exam.controller.MyCallback;
import com.example.vlad.exam.model.Car;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class EmployeeActivity extends AppCompatActivity {

    private List<Car> carList = new ArrayList<>();
    private ListView listView;
    private CarEmployeeAdapter mAdapter;

    private ProgressBar progressBar;
    private FloatingActionButton fabAddCar;

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_employee);

        controller = Controller.getController(getApplicationContext());

        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.list_view_employee_cars);

        mAdapter = new CarEmployeeAdapter(this, carList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Car car = carList.get(i);

                new AlertDialog.Builder(EmployeeActivity.this)
                        .setTitle("Wanna delete?")
                        .setMessage(car.getName())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                controller.removeCar(
                                        car.getId(),
                                        progressBar,
                                        new MyCallback() {
                                            @Override
                                            public void showError(String message) {
                                                Snackbar.make(
                                                        listView,
                                                        message,
                                                        Snackbar.LENGTH_INDEFINITE
                                                ).show();
                                            }

                                            @Override
                                            public void clear() {
                                                // ...
                                            }

                                            @Override
                                            public void onRequestSuccess() {
                                                Toast.makeText(
                                                        EmployeeActivity.this,
                                                        "Car successfully deleted.",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                                reloadData();
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
                                        EmployeeActivity.this,
                                        "Not deleted. Still here.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        fabAddCar = findViewById(R.id.fab_addCar);
        fabAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        EmployeeActivity.this,
                        EmployeeAddActivity.class
                );
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart");

        reloadData();
    }

    private void reloadData() {
        progressBar.setVisibility(View.VISIBLE);
        controller.getAllCars(
                progressBar,
                new MyCallback() {
                    @Override
                    public void showError(String message) {
                        progressBar.setVisibility(View.GONE);

                        Snackbar.make(listView, message, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        reloadData();
                                    }
                                }).show();
                    }

                    @Override
                    public void clear() {
                        mAdapter.clear();
                    }

                    @Override
                    public void onRequestSuccess() {
                        mAdapter.setData(carList);
                        Toast.makeText(
                                EmployeeActivity.this,
                                "The list is now up to date.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                carList
        );
    }
}
