package com.example.vlad.exam.activity.employee;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vlad.exam.R;
import com.example.vlad.exam.controller.Controller;
import com.example.vlad.exam.controller.MyCallback;

import timber.log.Timber;

public class EmployeeAddActivity extends AppCompatActivity {

    private EditText editTextCarName;
    private EditText editTextCarType;

    private ProgressBar progressBar;

    private Button buttonSaveNewCar;

    private Controller controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_employee_add);

        controller = Controller.getController(getApplicationContext());

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        editTextCarName = findViewById(R.id.editTextCarName);
        editTextCarType = findViewById(R.id.editTextCarType);
        buttonSaveNewCar = findViewById(R.id.buttonSaveNewCar);

        buttonSaveNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = editTextCarName.getText().toString();
                final String type = editTextCarType.getText().toString();

                if (name.length() < 1) {
                    editTextCarName.setError("Name is required.");
                    editTextCarName.requestFocus();
                    return;
                }

                if (type.length() < 1) {
                    editTextCarType.setError("Type is required.");
                    editTextCarType.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                controller.addCar(
                        name,
                        type,
                        progressBar,
                        new MyCallback() {
                            @Override
                            public void showError(String message) {
                                Snackbar.make(
                                        buttonSaveNewCar,
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
                                        EmployeeAddActivity.this,
                                        "Car successfully added.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                finish();
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
